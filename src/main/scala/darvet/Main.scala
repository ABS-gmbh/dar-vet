package darvet

import java.nio.file.{Files, Paths}

object Main {
  private val usage =
    """dar-vet - what a Daml package upgrade changes about who can do what
      |
      |Usage: dar-vet OLD.dar NEW.dar [--json FILE] [--fail-on high|medium|low]
      |
      |Compares the main package of two DARs (two versions of one package) and reports
      |authority changes: controllers, new choices, interface instances, choice observers,
      |signatories/observers/keys, ensure clauses, removed checks and ledger effects.
      |Analysis uses Daml-LF only; bundled source is used just to show snippets.
      |""".stripMargin

  def main(args: Array[String]): Unit = {
    val (files, opts) = parse(args.toList)
    if (files.length != 2) { System.err.println(usage); sys.exit(64) }
    val oldPv = new PackageVersion(files(0))
    val newPv = new PackageVersion(files(1))
    if (oldPv.name != newPv.name)
      System.err.println(s"warning: comparing different packages (${oldPv.name} vs ${newPv.name})")

    val findings = Diff.compare(oldPv, newPv)
    print(Report.markdown(oldPv, newPv, findings))
    opts.get("json").foreach(p => Files.writeString(Paths.get(p), Report.json(oldPv, newPv, findings)))
    opts.get("fail-on").map(Severity.parse).foreach { min =>
      if (findings.exists(_.severity.rank >= min.rank)) sys.exit(2)
    }
  }

  private def parse(args: List[String]): (List[String], Map[String, String]) = args match {
    case Nil => (Nil, Map.empty)
    case flag :: value :: rest if flag.startsWith("--") =>
      val (f, o) = parse(rest); (f, o + (flag.drop(2) -> value))
    case file :: rest =>
      val (f, o) = parse(rest); (file :: f, o)
  }
}

object Report {
  def markdown(o: PackageVersion, n: PackageVersion, fs: List[Finding]): String = {
    val sb = new StringBuilder
    sb ++= s"# dar-vet: ${o.name} ${o.version} -> ${n.version}\n\n"
    sb ++= s"Old package ID `${o.mainId}`, new package ID `${n.mainId}`.\n\n"
    val counts = Severity.all.map(s => s"${fs.count(_.severity == s)} ${s.label}").mkString(", ")
    sb ++= s"**${fs.size} findings:** $counts.\n\n"
    if (fs.isEmpty) sb ++= "No authority-relevant changes found.\n"
    fs.foreach { f =>
      val where = f.template + f.choice.map(c => s", choice `$c`").getOrElse("")
      sb ++= s"## ${f.severity.label} - ${f.kind} - `${where}`\n\n${f.message}\n\n"
      f.notes.foreach(nt => sb ++= s"- $nt\n")
      if (f.notes.nonEmpty) sb ++= "\n"
      f.snippets.foreach { s =>
        sb ++= s"Version ${s.version}, ${s.location}:\n\n```${s.lang}\n${s.code}\n```\n\n"
      }
    }
    sb.toString
  }

  private def q(s: String): String =
    "\"" + s.flatMap {
      case '"' => "\\\""; case '\\' => "\\\\"; case '\n' => "\\n"; case '\t' => "\\t"
      case c if c < ' ' => f"\\u${c.toInt}%04x"; case c => c.toString
    } + "\""

  def json(o: PackageVersion, n: PackageVersion, fs: List[Finding]): String = {
    val items = fs.map { f =>
      val snips = f.snippets.map(s => s"""{"version":${q(s.version)},"location":${q(s.location)},"code":${q(s.code)}}""").mkString(",")
      s"""{"severity":${q(f.severity.label)},"kind":${q(f.kind)},"template":${q(f.template)},""" +
        s""""choice":${f.choice.map(q).getOrElse("null")},"message":${q(f.message)},""" +
        s""""notes":[${f.notes.map(q).mkString(",")}],"snippets":[$snips]}"""
    }
    s"""{"package":${q(n.name)},"old":{"version":${q(o.version)},"packageId":${q(o.mainId)}},""" +
      s""""new":{"version":${q(n.version)},"packageId":${q(n.mainId)}},"findings":[${items.mkString(",")}]}""" + "\n"
  }
}
