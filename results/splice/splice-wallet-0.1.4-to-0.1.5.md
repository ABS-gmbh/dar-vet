# dar-vet: splice-wallet 0.1.4 -> 0.1.5

Old package ID `141dad2d33b6410b8e1c35a0c4f8f76cb691e4d9a4410ce89f33f373855317e1`, new package ID `614b525a50c624062d851ce7df5bdb90ddfa0d6871c486cb6e2c7b694bfbce59`.

**1 findings:** 0 HIGH, 0 MEDIUM, 1 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.4 -> 0.1.5, Splice.Wallet.Install:45:

```text
(source text identical: the difference comes from code this function calls)
```

