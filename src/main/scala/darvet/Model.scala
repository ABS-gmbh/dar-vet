package darvet

import com.digitalasset.daml.lf.archive.{Dar, DarDecoder}
import com.digitalasset.daml.lf.data.Ref
import com.digitalasset.daml.lf.language.Ast

import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile
import scala.collection.mutable
import scala.jdk.CollectionConverters._

/** A loaded package version: the main package, every package in the DAR, and the bundled source files. */
final class PackageVersion(val path: String) {
  private val dar: Dar[(Ref.PackageId, Ast.Package)] =
    DarDecoder.readArchiveFromFile(new File(path)).fold(e => sys.error(s"$path: $e"), identity)

  val mainId: Ref.PackageId = dar.main._1
  val main: Ast.Package = dar.main._2
  val packages: Map[Ref.PackageId, Ast.Package] = dar.all.toMap
  val name: String = main.metadata.name.toString
  val version: String = main.metadata.version.toString
  def label: String = s"$name-$version"

  /** Source text of a module, if the DAR bundles it (used only for display, never for analysis). */
  private val sources: Map[String, Array[String]] = {
    val zip = new ZipFile(path)
    try {
      zip.entries().asScala.filter(_.getName.endsWith(".daml")).map { e =>
        e.getName -> scala.io.Source.fromInputStream(zip.getInputStream(e), "UTF-8").getLines().toArray
      }.toMap
    } finally zip.close()
  }

  /** Full source text of the location's line range (display only). */
  def sourceLines(loc: Ref.Location): Option[String] =
    if (loc.packageId != mainId) None
    else {
      val suffix = "/" + loc.module.dottedName.replace('.', '/') + ".daml"
      sources.collectFirst { case (p, lines) if p.endsWith(suffix) || p == suffix.drop(1) => lines }.map { lines =>
        val from = loc.start._1
        val to = math.min(loc.end._1, from + 200)
        (from to to).filter(i => i >= 0 && i < lines.length).map(i => lines(i)).mkString("\n")
      }
    }

  /** Display fallback: the lines of `choice <name>` in the bundled source of a module. */
  def choiceSource(module: String, choice: String): Option[(Int, String)] = {
    val suffix = "/" + module.replace('.', '/') + ".daml"
    sources.collectFirst { case (p, lines) if p.endsWith(suffix) || p == suffix.drop(1) => lines }.flatMap { lines =>
      val re = ("""^\s*(nonconsuming\s+|preconsuming\s+|postconsuming\s+)?choice\s+""" + java.util.regex.Pattern.quote(choice) + """\b.*""").r
      val start = lines.indexWhere(l => re.matches(l))
      if (start < 0) None
      else {
        val indent = lines(start).takeWhile(_ == ' ').length
        val body = lines.drop(start + 1).takeWhile(l => l.trim.isEmpty || l.takeWhile(_ == ' ').length > indent)
        Some((start + 1, (lines(start) +: body.take(200)).mkString("\n").replaceAll("\\s+$", "")))
      }
    }
  }

  def isStdlib(pid: Ref.PackageId): Boolean =
    packages.get(pid).exists { p =>
      val n = p.metadata.name.toString
      n == "daml-prim" || n == "daml-stdlib" || n.startsWith("ghc-") || n.startsWith("daml-stdlib") ||
      n == "daml-script" || n.startsWith("daml-prim")
    }

  def lookupValue(ref: Ref.ValueRef): Option[Ast.GenDValue[Ast.Expr]] =
    packages.get(ref.packageId)
      .flatMap(_.modules.get(ref.qualifiedName.module))
      .flatMap(_.definitions.get(ref.qualifiedName.name))
      .collect { case d: Ast.GenDValue[Ast.Expr] @unchecked => d }
}

/**
 * Canonical, version-independent rendering of Daml-LF expressions.
 *
 *  - source locations are dropped;
 *  - the package's own ID is replaced by "self", dependency IDs by the dependency's name;
 *  - calls to values in non-library packages are replaced by a hash of the called value's
 *    canonical body, so a change inside a helper function changes every caller, while
 *    renumbered compiler-generated helpers ($$sc_...) do not;
 *  - calls to the standard library stay as names;
 *  - with followDeps = false, calls into dependency packages stay as names too, so only the
 *    package's own code is compared. With followDeps = true, dependency code is hashed as well.
 */
final class Canon(pv: PackageVersion, followDeps: Boolean) {
  private val valueHashes = mutable.Map.empty[Ref.ValueRef, String]
  private val inProgress = mutable.Set.empty[Ref.ValueRef]

  def ident(id: Ref.Identifier): String = {
    val pkg =
      if (id.packageId == pv.mainId) "self"
      else pv.packages.get(id.packageId).map(_.metadata.name.toString).getOrElse(id.packageId.toString.take(12))
    s"$pkg:${id.qualifiedName}"
  }

  def isUserValue(ref: Ref.ValueRef): Boolean = !pv.isStdlib(ref.packageId)

  /** Generated helpers have names starting with '$'; user helpers have source names. */
  def isNamedHelper(ref: Ref.ValueRef): Boolean = isUserValue(ref) && !ref.qualifiedName.name.toString.startsWith("$")

  def valueHash(ref: Ref.ValueRef): String =
    valueHashes.get(ref) match {
      case Some(h) => h
      case None if inProgress(ref) => s"rec(${ident(ref)})"
      case None =>
        inProgress += ref
        val body = pv.lookupValue(ref).map(d => show(d.body)).getOrElse(s"missing(${ident(ref)})")
        inProgress -= ref
        val h = Canon.sha(body)
        valueHashes(ref) = h
        h
    }

  def show(x: Any): String = x match {
    case Ast.ELocation(_, e) => show(e)
    // Call-stack records carry the package version and line numbers of the call site:
    // they change on every release and every edit above them, and never affect authority.
    case Ast.ERecCon(tc, _) if Canon.isSrcLoc(tc.tycon) => "SrcLoc"
    case Ast.EVal(ref) =>
      if (!isUserValue(ref)) s"std(${ref.qualifiedName})"
      else if (followDeps || ref.packageId == pv.mainId) s"call(${valueHash(ref)})"
      else s"dep(${ident(ref)})"
    case id: Ref.Identifier => ident(id)
    case _: Ref.Location => ""
    case s: String => s
    case o: Option[_] => o.map(show).getOrElse("None")
    case it: Iterable[_] => it.map(show).mkString("[", ",", "]")
    case p: Product if p.productArity == 0 => p.productPrefix
    case p: Product => p.productIterator.map(show).mkString(p.productPrefix + "(", ",", ")")
    case other => tryIterable(other).getOrElse(other.toString)
  }

  /** Daml's ImmArray / FrontStack are not Scala collections. */
  private def tryIterable(x: Any): Option[String] =
    try {
      val m = x.getClass.getMethod("toSeq")
      Some(m.invoke(x).asInstanceOf[Seq[Any]].map(show).mkString("[", ",", "]"))
    } catch { case _: NoSuchMethodException => None }

  def hash(x: Any): String = Canon.sha(show(x))
}

object Canon {
  def isSrcLoc(id: Ref.Identifier): Boolean =
    id.qualifiedName.module.dottedName == "GHC.Stack.Types" && id.qualifiedName.name.dottedName == "SrcLoc"

  def sha(s: String): String =
    MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8")).take(8).map("%02x".format(_)).mkString
}

/** Facts reachable from an expression: ledger effects, guards, named helpers, first source location. */
final case class Facts(
    effects: Set[String],
    guards: Int,
    helpers: Set[Ref.ValueRef],
    firstLoc: Option[Ref.Location]
)

final class Walker(pv: PackageVersion, canon: Canon) {
  private val guardNames = "(?i).*(assert|abort|error|fail|throw|ensure).*".r

  def facts(e: Any): Facts = {
    val effects = mutable.Set.empty[String]
    val helpers = mutable.Set.empty[Ref.ValueRef]
    var guards = 0
    var firstLoc: Option[Ref.Location] = None
    val visited = mutable.Set.empty[Ref.ValueRef]

    def go(x: Any): Unit = x match {
      case Ast.ELocation(loc, body) =>
        if (firstLoc.isEmpty && loc.packageId == pv.mainId) firstLoc = Some(loc)
        go(body)
      case Ast.EVal(ref) =>
        if (canon.isUserValue(ref)) {
          if (canon.isNamedHelper(ref)) helpers += ref
          if (!visited(ref)) { visited += ref; pv.lookupValue(ref).foreach(d => go(d.body)) }
        } else if (guardNames.matches(ref.qualifiedName.name.toString)) guards += 1
      case Ast.EBuiltinFun(f) if f.toString.contains("Error") || f.toString.contains("Fail") => guards += 1
      case _: Ast.EThrow => guards += 1; x.asInstanceOf[Product].productIterator.foreach(go)
      case Ast.UpdateCreate(t, arg) => effects += s"create ${canon.ident(t)}"; go(arg)
      case Ast.UpdateCreateInterface(i, arg) => effects += s"create via ${canon.ident(i)}"; go(arg)
      case Ast.UpdateExercise(t, c, cid, arg) => effects += s"exercise ${canon.ident(t)}.$c"; go(cid); go(arg)
      case u: Ast.UpdateExerciseInterface => effects += s"exercise ${canon.ident(u.interfaceId)}.${u.choice}"; u.productIterator.foreach(go)
      case u: Ast.UpdateExerciseByKey => effects += s"exercise-by-key ${canon.ident(u.templateId)}.${u.choice}"; u.productIterator.foreach(go)
      case Ast.UpdateFetchTemplate(t, cid) => effects += s"fetch ${canon.ident(t)}"; go(cid)
      case Ast.UpdateFetchInterface(i, cid) => effects += s"fetch ${canon.ident(i)}"; go(cid)
      case _: Ref.Location => ()
      case o: Option[_] => o.foreach(go)
      case it: Iterable[_] => it.foreach(go)
      case p: Product => p.productIterator.foreach(go)
      case other =>
        try other.getClass.getMethod("toSeq").invoke(other).asInstanceOf[Seq[Any]].foreach(go)
        catch { case _: NoSuchMethodException => () }
    }

    go(e)
    Facts(effects.toSet, guards, helpers.toSet, firstLoc)
  }
}

/** Small line diff (LCS) for showing what changed between two source snippets. */
object LineDiff {
  def apply(a: String, b: String, context: Int = 1): Option[String] = {
    val (x, y) = (a.split("\n", -1).toVector, b.split("\n", -1).toVector)
    val l = Array.ofDim[Int](x.length + 1, y.length + 1)
    for (i <- x.indices.reverse; j <- y.indices.reverse)
      l(i)(j) = if (x(i) == y(j)) l(i + 1)(j + 1) + 1 else math.max(l(i + 1)(j), l(i)(j + 1))
    val ops = Vector.newBuilder[(Char, String)]
    var (i, j) = (0, 0)
    while (i < x.length || j < y.length) {
      if (i < x.length && j < y.length && x(i) == y(j)) { ops += ((' ', x(i))); i += 1; j += 1 }
      else if (j < y.length && (i == x.length || l(i)(j + 1) >= l(i + 1)(j))) { ops += (('+', y(j))); j += 1 }
      else { ops += (('-', x(i))); i += 1 }
    }
    val all = ops.result()
    val changed = all.indices.filter(k => all(k)._1 != ' ')
    if (changed.isEmpty) None
    else {
      val keep = changed.flatMap(k => (k - context) to (k + context)).filter(all.indices.contains).toSet
      val out = new StringBuilder
      var last = -2
      all.indices.filter(keep).foreach { k =>
        if (k != last + 1 && last >= 0) out ++= "...\n"
        out ++= s"${all(k)._1} ${all(k)._2}\n"
        last = k
      }
      Some(out.toString.stripLineEnd)
    }
  }
}
