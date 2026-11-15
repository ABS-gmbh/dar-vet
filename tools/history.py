#!/usr/bin/env python3
"""Run dar-vet on every consecutive pair of versions of each package in a folder of DARs.

Usage: history.py DAR_DIR OUT_DIR

Writes OUT_DIR/<pkg>-<old>-to-<new>.md and .json, plus OUT_DIR/summary.md.
For the noise check it also records whether the bundled Daml source changed:
when the source is identical, dar-vet should report nothing.
"""
import collections, hashlib, json, os, re, subprocess, sys, zipfile

dar_dir, out_dir = sys.argv[1], sys.argv[2]
os.makedirs(out_dir, exist_ok=True)
bin_ = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "bin", "dar-vet")

groups = collections.defaultdict(list)
for f in os.listdir(dar_dir):
    m = re.match(r"(.+)-(\d+)\.(\d+)\.(\d+)\.dar$", f)
    if m:
        groups[m.group(1)].append((tuple(int(x) for x in m.group(2, 3, 4)), f))

def source_hash(path):
    """Hash of the main package's bundled .daml sources (the DAR's top-level source folder)."""
    with zipfile.ZipFile(path) as z:
        names = sorted(n for n in z.namelist() if n.endswith(".daml"))
        h = hashlib.sha256()
        for n in names:
            h.update(n.split("/", 1)[-1].encode()); h.update(z.read(n))
        return h.hexdigest()

rows = []
for pkg, versions in sorted(groups.items()):
    versions.sort()
    for (va, fa), (vb, fb) in zip(versions, versions[1:]):
        a, b = os.path.join(dar_dir, fa), os.path.join(dar_dir, fb)
        tag = f"{pkg}-{'.'.join(map(str, va))}-to-{'.'.join(map(str, vb))}"
        js = os.path.join(out_dir, tag + ".json")
        r = subprocess.run([bin_, a, b, "--json", js], capture_output=True, text=True)
        if r.returncode not in (0, 2):
            rows.append((pkg, va, vb, None, None, r.stderr.strip()[-300:])); continue
        with open(os.path.join(out_dir, tag + ".md"), "w") as f:
            f.write(r.stdout)
        findings = json.load(open(js))["findings"]
        same_src = source_hash(a) == source_hash(b)
        rows.append((pkg, va, vb, findings, same_src, ""))

def v(t): return ".".join(map(str, t))
with open(os.path.join(out_dir, "summary.md"), "w") as f:
    f.write("| Package | From | To | Source changed | HIGH | MEDIUM | LOW | Kinds |\n|---|---|---|---|---|---|---|---|\n")
    for pkg, va, vb, fs, same, err in rows:
        if fs is None:
            f.write(f"| {pkg} | {v(va)} | {v(vb)} | ? | error | | | {err} |\n"); continue
        c = collections.Counter(x["severity"] for x in fs)
        kinds = ", ".join(f"{k} x{n}" for k, n in sorted(collections.Counter(x["kind"] for x in fs).items()))
        f.write(f"| {pkg} | {v(va)} | {v(vb)} | {'no' if same else 'yes'} | {c['HIGH']} | {c['MEDIUM']} | {c['LOW']} | {kinds} |\n")
print(open(os.path.join(out_dir, "summary.md")).read())
