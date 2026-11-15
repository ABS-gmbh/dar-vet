# dar-vet: splice-dso-governance 0.1.21 -> 0.1.22

Old package ID `2d306cfe8cdb3daf2d21f84dfecc3e2f26a41504e58fe25cb7fe5cc65683d11f`, new package ID `5c28530209b9ab37c5f187132cd826709bb18b0efe28411488ab750870414738`.

**1 findings:** 0 HIGH, 0 MEDIUM, 1 LOW.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.21 -> 0.1.22, Splice.DsoRules:653:

```text
(source text identical: the difference comes from code this function calls)
```

