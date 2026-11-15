# dar-vet: splice-wallet 0.1.3 -> 0.1.4

Old package ID `2c35bb4f5084ea66db59717d21750bfd64c43147ef5fd5166615092d592a6917`, new package ID `141dad2d33b6410b8e1c35a0c4f8f76cb691e4d9a4410ce89f33f373855317e1`.

**1 findings:** 0 HIGH, 0 MEDIUM, 1 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.3 -> 0.1.4, Splice.Wallet.Install:45:

```text
(source text identical: the difference comes from code this function calls)
```

