# dar-vet: splice-dso-governance 0.1.6 -> 0.1.7

Old package ID `4e7653cfbf7ca249de4507aca9cd3b91060e5489042a522c589d3c4199580cd8`, new package ID `d406eba1132d464605f4dae3edf8cf5ecbbb34bd8edef0e047e7e526d328718c`.

**3 findings:** 0 HIGH, 2 MEDIUM, 1 LOW.

## MEDIUM - ensure-changed - `Splice.DSO.SvState:SvNodeState`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.6 -> 0.1.7, Splice.DSO.SvState:66:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.6 -> 0.1.7, Splice.DSO.DecentralizedSynchronizer:87:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SetSynchronizerNodeConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.6 -> 0.1.7, Splice.DsoRules:516:

```text
(source text identical: the difference comes from code this function calls)
```

