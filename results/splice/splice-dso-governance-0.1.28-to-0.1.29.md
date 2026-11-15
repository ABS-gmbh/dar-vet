# dar-vet: splice-dso-governance 0.1.28 -> 0.1.29

Old package ID `eeb461e9e430d8aaaa232f23058a28c9858d559abd083c391d7ab7e73efb4e13`, new package ID `2b680f34f11be43d65a2dde723f4c9595a0983f63a0bb3455f03bb16c5d9b814`.

**9 findings:** 0 HIGH, 4 MEDIUM, 5 LOW.

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.28 -> 0.1.29, Splice.DSO.DecentralizedSynchronizer:80 -> Splice.DSO.DecentralizedSynchronizer:80:

```diff
+   deriving (Eq, Show, Serializable)
-   deriving (Eq, Show)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.28 -> 0.1.29, Splice.DsoRules:450:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:DsoRules`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.28 -> 0.1.29, Splice.DsoRules:591:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.28 -> 0.1.29, Splice.DsoRules:489:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.28 -> 0.1.29, Splice.DsoRules:851:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.28 -> 0.1.29, Splice.DsoRules:1849:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.28 -> 0.1.29, Splice.DsoRules:1849:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.28 -> 0.1.29, Splice.DsoRules:1629:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.28 -> 0.1.29, Splice.DsoRules:1040:

```text
(source text identical: the difference comes from code this function calls)
```

