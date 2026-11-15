# dar-vet: splice-wallet 0.1.1 -> 0.1.2

Old package ID `e276113de450c2bb4bf8cf2d2991dd0b397fe7fe8103fe1b5377b8ab6622edec`, new package ID `c162e08a4ec0428bfa870b6d9040989e575c74199c3a80558c62e03196dd5146`.

**1 findings:** 0 HIGH, 0 MEDIUM, 1 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.1 -> 0.1.2, Splice.Wallet.Install:42:

```text
(source text identical: the difference comes from code this function calls)
```

