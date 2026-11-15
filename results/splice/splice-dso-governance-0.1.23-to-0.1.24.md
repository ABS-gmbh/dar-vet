# dar-vet: splice-dso-governance 0.1.23 -> 0.1.24

Old package ID `0c94a036ac5168a1dee26b435838e062f0d2f47d6eac49303978228ae559edb9`, new package ID `4974c654485d4ecaa6b5caf8ef3c2679efa8195c4b50d4965a8fff1b72e8efa4`.

**11 findings:** 0 HIGH, 5 MEDIUM, 6 LOW.

## MEDIUM - ensure-changed - `Splice.DSO.SvState:SvNodeState`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.23 -> 0.1.24, Splice.DSO.SvState:66:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.23 -> 0.1.24, Splice.DSO.DecentralizedSynchronizer:80:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.23 -> 0.1.24, Splice.DsoRules:364:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:DsoRules`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.23 -> 0.1.24, Splice.DsoRules:503:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.23 -> 0.1.24, Splice.DsoRules:403:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.23 -> 0.1.24, Splice.DsoRules:763:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.DsoRules:1689:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.DsoRules:1689:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.23 -> 0.1.24, Splice.DsoRules:1469:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.23 -> 0.1.24, Splice.DsoRules:952:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SetSynchronizerNodeConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.23 -> 0.1.24, Splice.DsoRules:620:

```text
(source text identical: the difference comes from code this function calls)
```

