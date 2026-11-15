# dar-vet: splice-wallet 0.1.14 -> 0.1.15

Old package ID `690c1d47bac06db419db344d59a7a30c53fa3f5d961943fe1782cfc6c78794d8`, new package ID `fd57252dda29e3ce90028114c91b521cb661df5a9d6e87c41a9e91518215fa5b`.

**2 findings:** 0 HIGH, 0 MEDIUM, 2 LOW.

## LOW - dependency-behaviour-changed - `(package)`

1 expressions are unchanged in this package but call dependency code that changed.

- Changed dependencies: splice-amulet 0.1.14 -> 0.1.15, splice-util 0.1.4 -> 0.1.5, splice-wallet-payments 0.1.14 -> 0.1.15.
- Affected: Splice.Wallet.TransferOffer:AcceptedTransferOffer AcceptedTransferOffer_Complete (body).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.Wallet.Install:52:

```text
(source text identical: the difference comes from code this function calls)
```

