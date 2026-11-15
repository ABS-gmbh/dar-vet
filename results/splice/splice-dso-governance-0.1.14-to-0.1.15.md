# dar-vet: splice-dso-governance 0.1.14 -> 0.1.15

Old package ID `0d35cac28a2b22b3092b4f242246b040ba39a52b0c5602cf4dce1d87b7cb3b81`, new package ID `8ef106efc4df1812572a24bd3f41218e69580e15606b636b1632223ea08434f9`.

**1 findings:** 0 HIGH, 0 MEDIUM, 1 LOW.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSubscription``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.DsoRules:1197 -> Splice.DsoRules:1197:

```diff
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise subscriptionIdleStateCid choiceArg
```

