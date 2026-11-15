# dar-vet: splice-wallet 0.1.7 -> 0.1.8

Old package ID `d9a91419b27aa27002a883334015a892c35075967b07008a446bc4b5073051ec`, new package ID `882f6438ef69a4fbbe36e32e020d98f3eb1982ac484686e3ca1e2577d677626a`.

**1 findings:** 0 HIGH, 0 MEDIUM, 1 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.7 -> 0.1.8, Splice.Wallet.Install:46:

```text
(source text identical: the difference comes from code this function calls)
```

