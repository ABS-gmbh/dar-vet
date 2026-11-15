# dar-vet

**What does this Daml package upgrade change about who can do what?**

With Smart Contract Upgrade, a new version of a Daml package runs on contracts created under the old version as soon as it is vetted. The new version may change a choice's controller, its observers and its body, add choices and add interface instances. The SDK upgrade check verifies type compatibility. It warns when a controller, observer, signatory or `ensure` expression is written differently. It does not warn about new choices, new interface instances, changed ledger effects, removed checks, or changes made inside the helper functions those expressions call.

dar-vet compares two compiled versions of a package and reports these changes, most severe first. It reads Daml-LF, the artifact that validators vet. Bundled source is only used to show snippets.

Status: **prototype** (November 2026). Apache-2.0.

## Example

```
$ bin/dar-vet cov-1.0.0.dar cov-2.0.0.dar

## HIGH - controller-changed - `Cov:Cov, choice `ViaHelper``
Who can exercise this choice has changed. It applies to contracts created under the old version.
- The change is inside the helper function(s) Cov:approver, so the expression in the choice
  looks unchanged. The SDK upgrade check does not warn about this case.

  - approver issuer owner = owner
  + approver issuer owner = issuer
```

Full output for this example: [examples/coverage](examples/coverage). Reports for 101 Splice releases: [results/splice](results/splice) and [RESULTS.md](RESULTS.md).

## What it reports

| Kind | Severity | SDK upgrade check |
|---|---|---|
| `controller-changed` | HIGH | warns only if written directly in the choice |
| `new-choice` on an existing template | HIGH | no warning |
| `new-interface-instance` | HIGH | separate warning (`template-has-new-interface-instance`) |
| `consuming-changed` | HIGH | rejected |
| `effects-changed` (creates, exercises, fetches); HIGH when a non-consuming choice starts creating contracts | MEDIUM/HIGH | no warning |
| `choice-observers-changed`, `signatories-changed`, `observers-changed`, `key-changed`, `ensure-changed` | MEDIUM | warns only if written directly |
| `guard-removed` (fewer assertions or aborts; heuristic) | MEDIUM | no warning |
| `dependency-behaviour-changed` (own code unchanged, called dependency code changed) | MEDIUM/LOW | no warning |
| `body-changed`, `template-added`, `template-removed`, `choice-removed` | LOW | - |

dar-vet reports facts, not verdicts: a changed controller can be an intended fix.

## How it works

1. Loads both DARs with Digital Asset's Daml-LF reader (`com.daml:daml-lf-archive`).
2. Puts every template, choice and helper function into a canonical form: source locations, package IDs, call-stack records and compiler-generated helper names (`$$sc_...`) are removed, and calls into the package's own functions are replaced by a hash of the called function. A change deep inside a helper therefore changes every expression that calls it, and an untouched function compares equal across releases.
3. Compares controllers, observers, signatories, keys, `ensure`, choice bodies and interface instances, and walks choice bodies for ledger effects and checks.
4. For each change, names the helper functions whose code changed and shows a line diff from the bundled source.

## Usage

Requires Java 21+ and sbt.

```bash
bin/dar-vet OLD.dar NEW.dar [--json report.json] [--fail-on high|medium|low]
```

`--fail-on` returns exit code 2 if a finding at or above that severity exists, for CI gates and vetting scripts.

To run over a release history: `python3 tools/history.py DAR_DIR OUT_DIR`.

## Reproduce the ledger demo

[examples/controller-change](examples/controller-change) shows on a real Canton sandbox that after a v2 upload, the issuer can transfer an asset created under v1, which v1 did not allow:

```bash
dpm sandbox            # in one terminal
cd examples/controller-change && ./run-demo.sh
```

## Known limitations

- Prototype: no SARIF output, no `dpm` component, no policy file yet.
- Recursive helper functions can produce LOW `body-changed` noise (13 findings across 27 Splice releases with unchanged source).
- `guard-removed` counts assertion and abort calls; moving a check into another choice is reported as a removal.
- Templates and choices are matched by name; renames show as removed plus added.
