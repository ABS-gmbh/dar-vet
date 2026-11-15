package darvet

import com.digitalasset.daml.lf.data.Ref
import com.digitalasset.daml.lf.language.Ast

sealed abstract class Severity(val rank: Int, val label: String)
object Severity {
  case object High extends Severity(3, "HIGH")
  case object Medium extends Severity(2, "MEDIUM")
  case object Low extends Severity(1, "LOW")
  val all: List[Severity] = List(High, Medium, Low)
  def parse(s: String): Severity = all.find(_.label.equalsIgnoreCase(s)).getOrElse(sys.error(s"unknown severity $s"))
}

final case class Snippet(version: String, location: String, code: String, lang: String = "daml")

final case class Finding(
    severity: Severity,
    kind: String,
    template: String,
    choice: Option[String],
    message: String,
    notes: List[String] = Nil,
    snippets: List[Snippet] = Nil
)

/** One side of the comparison: a package version with its canonicaliser and walker. */
final class Side(val pv: PackageVersion) {
  /** Compares the package's own code; calls into dependencies stay as names. */
  val canon = new Canon(pv, followDeps = false)
  /** Also hashes dependency code, to tell when behaviour changed only through a dependency. */
  val deep = new Canon(pv, followDeps = true)
  val walker = new Walker(pv, canon)

  type TemplateKey = String
  val templates: Map[TemplateKey, Ast.Template] =
    pv.main.modules.toList.flatMap { case (mn, m) =>
      m.templates.toList.map { case (tn, t) => s"$mn:$tn" -> t }
    }.toMap

  def helperName(ref: Ref.ValueRef): String = s"${ref.qualifiedName}"

  def choiceSnippet(templateKey: String, choice: String): Option[Snippet] = {
    val module = templateKey.split(':').head
    pv.choiceSource(module, choice).map { case (line, code) => Snippet(pv.version, s"$module:$line", code) }
  }

  def snippet(loc: Option[Ref.Location]): Option[Snippet] =
    loc.flatMap { l =>
      pv.sourceLines(l).map(code => Snippet(pv.version, s"${l.module}:${l.start._1 + 1}", code))
    }
}

object Diff {

  def compare(oldPv: PackageVersion, newPv: PackageVersion): List[Finding] = {
    val o = new Side(oldPv)
    val n = new Side(newPv)
    val out = List.newBuilder[Finding]
    // (template, choice, aspect) whose own code is unchanged but whose dependency code changed.
    val viaDeps = List.newBuilder[(String, String, String)]
    def depOnly(tk: String, where: String, aspect: String, oe: Any, ne: Any): Unit =
      if (o.canon.hash(oe) == n.canon.hash(ne) && o.deep.hash(oe) != n.deep.hash(ne)) viaDeps += ((tk, where, aspect))

    /** Named helpers reachable from `e` whose body differs between the two versions. */
    def changedHelpers(oe: Any, ne: Any): List[(String, Option[Snippet], Option[Snippet])] = {
      val oh = o.walker.facts(oe).helpers.map(r => o.helperName(r) -> r).toMap
      val nh = n.walker.facts(ne).helpers.map(r => n.helperName(r) -> r).toMap
      (oh.keySet intersect nh.keySet).toList.sorted.flatMap { k =>
        val (or, nr) = (oh(k), nh(k))
        if (o.canon.valueHash(or) == n.canon.valueHash(nr)) None
        else {
          val os = oldPv.lookupValue(or).flatMap(d => o.snippet(o.walker.facts(d.body).firstLoc))
          val ns = newPv.lookupValue(nr).flatMap(d => n.snippet(n.walker.facts(d.body).firstLoc))
          Some((k, os, ns))
        }
      }
    }

    /** Explains a change of an expression: inside named helpers (no SDK warning) or direct. */
    def explain(oe: Any, ne: Any, warnedBySdk: Boolean, tk: String = "", choice: Option[String] = None): (List[String], List[Snippet]) = {
      val helpers = changedHelpers(oe, ne)
      // A helper that is itself only called by other changed helpers is still reported: every one is a place to review.
      if (helpers.nonEmpty) {
        val names = helpers.map(_._1).mkString(", ")
        val note =
          s"The change is inside the helper function(s) $names, so the expression in the choice looks unchanged. " +
            "The SDK upgrade check does not warn about this case."
        (List(note), helpers.flatMap { case (_, os, ns) => diffSnippet(os, ns) })
      } else {
        val note =
          if (warnedBySdk) "Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed)."
          else "Direct change. The SDK upgrade check does not warn about this."
        val byLoc = o.snippet(o.walker.facts(oe).firstLoc).toList ++ n.snippet(n.walker.facts(ne).firstLoc).toList
        val byName = choice.toList.flatMap(c => o.choiceSnippet(tk, c).toList ++ n.choiceSnippet(tk, c).toList)
        val pick = if (byName.nonEmpty) byName else byLoc
        val shown = pick match {
          case List(a, b) => diffSnippet(Some(a), Some(b))
          case other => other
        }
        (List(note), shown)
      }
    }

    /** One diff snippet when both sides are known and differ in text, else the raw snippets. */
    def diffSnippet(os: Option[Snippet], ns: Option[Snippet]): List[Snippet] = (os, ns) match {
      case (Some(a), Some(b)) =>
        LineDiff(a.code, b.code) match {
          case Some(d) => List(Snippet(s"${a.version} -> ${b.version}", s"${a.location} -> ${b.location}", d, "diff"))
          case None => List(Snippet(s"${a.version} -> ${b.version}", b.location,
            "(source text identical: the difference comes from code this function calls)", "text"))
        }
      case _ => os.toList ++ ns.toList
    }

    // Templates removed or added.
    (o.templates.keySet diff n.templates.keySet).toList.sorted.foreach { t =>
      out += Finding(Severity.Low, "template-removed", t, None, "Template no longer present in the new version.")
    }
    (n.templates.keySet diff o.templates.keySet).toList.sorted.foreach { t =>
      out += Finding(Severity.Low, "template-added", t, None, "New template. Review it like a new package.")
    }

    (o.templates.keySet intersect n.templates.keySet).toList.sorted.foreach { tk =>
      val ot = o.templates(tk)
      val nt = n.templates(tk)

      // Interface instances: new ones make the interface's choices exercisable on existing contracts.
      val oImpl = ot.implements.keySet.map(o.canon.ident)
      val nImpl = nt.implements.keySet.map(n.canon.ident)
      (nImpl diff oImpl).toList.sorted.foreach { i =>
        out += Finding(Severity.High, "new-interface-instance", tk, None,
          s"Template now implements interface $i. Its choices can be exercised on contracts created under the old version.")
      }

      // Template-level authority and guards.
      def tmpl(kind: String, sev: Severity, label: String, oe: Any, ne: Any, warned: Boolean): Unit = {
        depOnly(tk, "template", label.toLowerCase, oe, ne)
        if (o.canon.hash(oe) != n.canon.hash(ne)) {
          val (notes, snips) = explain(oe, ne, warned)
          out += Finding(sev, kind, tk, None, s"$label changed.", notes, snips)
        }
      }
      tmpl("signatories-changed", Severity.Medium, "Signatory expression", ot.signatories, nt.signatories, warned = true)
      tmpl("observers-changed", Severity.Medium, "Observer expression", ot.observers, nt.observers, warned = true)
      tmpl("ensure-changed", Severity.Medium, "Ensure clause", ot.precond, nt.precond, warned = true)
      tmpl("key-changed", Severity.Medium, "Contract key", ot.key, nt.key, warned = true)

      // Choices.
      val oc = ot.choices
      val nc = nt.choices
      (nc.keySet diff oc.keySet).toList.map(_.toString).sorted.foreach { cn =>
        val c = nc(Ref.ChoiceName.assertFromString(cn))
        val f = n.walker.facts(c.update)
        val kind = if (c.consuming) "consuming" else "non-consuming"
        out += Finding(Severity.High, "new-choice", tk, Some(cn),
          s"New $kind choice on an existing template. It can be exercised on contracts created under the old version.",
          List(s"Effects: ${if (f.effects.isEmpty) "none" else f.effects.toList.sorted.mkString(", ")}."),
          n.choiceSnippet(tk, cn).toList)
      }
      (oc.keySet diff nc.keySet).toList.map(_.toString).sorted.foreach { cn =>
        out += Finding(Severity.Low, "choice-removed", tk, Some(cn), "Choice no longer present.")
      }

      (oc.keySet intersect nc.keySet).toList.sortBy(_.toString).foreach { cname =>
        val cn = cname.toString
        val (a, b) = (oc(cname), nc(cname))

        if (a.consuming != b.consuming)
          out += Finding(Severity.High, "consuming-changed", tk, Some(cn),
            s"Choice changed from ${if (a.consuming) "consuming" else "non-consuming"} to ${if (b.consuming) "consuming" else "non-consuming"}.")

        depOnly(tk, cn, "controllers", a.controllers, b.controllers)
        depOnly(tk, cn, "choice observers", a.choiceObservers, b.choiceObservers)
        depOnly(tk, cn, "body", a.update, b.update)
        if (o.canon.hash(a.controllers) != n.canon.hash(b.controllers)) {
          val (notes, snips) = explain(a.controllers, b.controllers, warnedBySdk = true, tk, Some(cn))
          out += Finding(Severity.High, "controller-changed", tk, Some(cn),
            "Who can exercise this choice has changed. It applies to contracts created under the old version.", notes, snips)
        }

        if (o.canon.hash(a.choiceObservers) != n.canon.hash(b.choiceObservers)) {
          val (notes, snips) = explain(a.choiceObservers, b.choiceObservers, warnedBySdk = true, tk, Some(cn))
          out += Finding(Severity.Medium, "choice-observers-changed", tk, Some(cn),
            "Who is informed when this choice is exercised has changed.", notes, snips)
        }

        if (o.canon.hash(a.update) != n.canon.hash(b.update)) {
          val fa = o.walker.facts(a.update)
          val fb = n.walker.facts(b.update)
          val added = fb.effects diff fa.effects
          val removed = fa.effects diff fb.effects
          val (notes, snips) = explain(a.update, b.update, warnedBySdk = false, tk, Some(cn))
          var reported = false
          if (added.nonEmpty || removed.nonEmpty) {
            val repeatable = !b.consuming && added.exists(_.startsWith("create"))
            val sev = if (repeatable) Severity.High else Severity.Medium
            val extra = if (repeatable) List("The choice is non-consuming, so it can be repeated: each call now creates a contract.") else Nil
            out += Finding(sev, "effects-changed", tk, Some(cn),
              s"Ledger effects changed. Added: ${fmt(added)}. Removed: ${fmt(removed)}.", extra ++ notes, snips)
            reported = true
          }
          if (fb.guards < fa.guards) {
            out += Finding(Severity.Medium, "guard-removed", tk, Some(cn),
              s"Fewer checks in the choice body (assertions or aborts): ${fa.guards} before, ${fb.guards} now.", notes, snips)
            reported = true
          }
          if (!reported)
            out += Finding(Severity.Low, "body-changed", tk, Some(cn),
              "Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.",
              notes, snips)
        }
      }
    }
    // One finding per release for behaviour that changed only because dependency code changed.
    val deps = viaDeps.result()
    if (deps.nonEmpty) {
      val depVersions = {
        val byName = (pv: PackageVersion) =>
          pv.packages.values.map(p => p.metadata.name.toString -> p.metadata.version.toString).toMap - pv.name
        val (ov, nv) = (byName(oldPv), byName(newPv))
        (ov.keySet intersect nv.keySet).toList.sorted.collect { case k if ov(k) != nv(k) => s"$k ${ov(k)} -> ${nv(k)}" }
      }
      val authority = deps.filter(d => d._3 != "body")
      val sev = if (authority.nonEmpty) Severity.Medium else Severity.Low
      val sample = deps.take(8).map { case (t, c, a) => s"$t $c ($a)" }
      out += Finding(sev, "dependency-behaviour-changed", "(package)", None,
        s"${deps.size} expressions are unchanged in this package but call dependency code that changed" +
          (if (authority.nonEmpty) s", including ${authority.size} that decide authority (controllers, observers, signatories)." else "."),
        List(s"Changed dependencies: ${if (depVersions.isEmpty) "versions equal, code differs" else depVersions.mkString(", ")}.",
          s"Affected: ${sample.mkString("; ")}${if (deps.size > 8) s"; and ${deps.size - 8} more" else ""}.",
          "Run dar-vet on the dependency packages to see what changed there."))
    }
    out.result().sortBy(f => (-f.severity.rank, f.template, f.choice.getOrElse(""), f.kind))
  }

  private def fmt(s: Set[String]): String = if (s.isEmpty) "none" else s.toList.sorted.mkString(", ")
}
