# Prototype results (15 November 2026)

Tools: Daml SDK 3.7.1, Canton 3.7.1 sandbox, Daml-LF reader `com.daml:daml-lf-archive` 3.7.3.

## 1. The SDK upgrade check misses changes made inside helper functions

[examples/coverage](examples/coverage) changes six things between v1 and v2 of one template, then builds v2 with the normal upgrade check:

| Change in v2 | SDK warning | dar-vet |
|---|---|---|
| Controller changed inside a helper function (`approver` returns `issuer` instead of `owner`) | none | HIGH controller-changed, names `approver` |
| New choice `Reclaim` (issuer takes the asset) | none | HIGH new-choice |
| Non-consuming choice `Peek` now creates a contract | none | HIGH effects-changed |
| Assertion removed from `Split` | none | MEDIUM guard-removed |
| Choice observers of `Ping` changed (written directly) | warning | MEDIUM choice-observers-changed |
| Recipient changed inside a helper (`payoutTo`) | none | LOW body-changed, names `payoutTo` |

SDK 3.7 also warns when a controller is changed directly in the choice ([examples/controller-change](examples/controller-change)).

## 2. The new rules apply to existing contracts

[examples/controller-change](examples/controller-change), run on a Canton 3.7.1 sandbox with `run-demo.sh`:

1. Only v1 on the ledger: the issuer creates an asset; the issuer's attempt to transfer it fails (only the owner may).
2. v2 uploaded (and vetted by default on upload): the issuer transfers **the same contract** to another party, without the owner.

During testing we also saw that once v2 was vetted, a command written against the v1 template ran with v2's rules.

## 3. Splice release history

All consecutive release pairs of five Splice packages (splice-amulet, splice-dso-governance, splice-wallet, splice-util, splice-validator-lifecycle): **101 pairs**, 203 seconds on a laptop, about 2 seconds per pair. Reports: [results/splice](results/splice), table: [results/splice/summary.md](results/splice/summary.md).

| Finding | Count | SDK warns? |
|---|---|---|
| HIGH new-choice | 77 | no |
| HIGH new-interface-instance | 17 | separate warning |
| HIGH controller-changed | 36 | yes, all 36 were written directly in the choice |
| HIGH effects-changed | 7 | no (5 of them inside helper functions) |
| MEDIUM effects-changed | 97 | no (52 inside helper functions) |
| MEDIUM ensure-changed | 78 | only if written directly (10 inside helpers) |
| MEDIUM guard-removed (heuristic) | 37 | no (16 inside helpers) |
| MEDIUM other | 11 | partly |
| LOW | 407 | - |

**Noise check.** 27 of the 101 pairs have identical bundled source. They produce no HIGH or MEDIUM findings and 13 LOW findings, all from recursive helper functions.

**Examples worth reading:**
- `splice-dso-governance` 0.1.12 -> 0.1.13: 24 choices of `DsoRules` moved from `controller dsoDelegate` (fixed in the contract) to `controller sv` (a party passed by the caller, validated in the body). Intended design change; the SDK warns about each one.
- `splice-amulet` 0.1.14 -> 0.1.15: the non-consuming choice `AmuletRules_MiningRound_StartIssuing` now creates `UnclaimedDevelopmentFundCoupon` contracts. The change sits in the helper `computeIssuingRoundParameters`, which now takes 5% of each round's issuance for the Development Fund. No SDK warning.

**Not measured yet:** precision and recall against a labelled set. The examples above were checked by hand; the counts are raw tool output.
