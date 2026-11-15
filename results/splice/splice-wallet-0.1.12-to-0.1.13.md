# dar-vet: splice-wallet 0.1.12 -> 0.1.13

Old package ID `b30bb727552cf6b624dbc9a5ff95f6c158e0a654e2e9c5c27bcfe3f5d0f9ada2`, new package ID `eb6e01efacc3397e23c6be8b9be7db4bf37672211974d69e24b48980e2f98b7e`.

**1 findings:** 0 HIGH, 0 MEDIUM, 1 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.12 -> 0.1.13, Splice.Wallet.Install:52:

```text
(source text identical: the difference comes from code this function calls)
```

