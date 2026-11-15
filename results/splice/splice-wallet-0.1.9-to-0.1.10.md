# dar-vet: splice-wallet 0.1.9 -> 0.1.10

Old package ID `940b9fd181c0cf358dd8c38934a8c908d2a3ee5a4728f72481b4de6720f3f8d5`, new package ID `9824ffa414d5803d17efafd75c69c1dd53591ac5197d07048b24dbac1720a462`.

**2 findings:** 0 HIGH, 1 MEDIUM, 1 LOW.

## MEDIUM - ensure-changed - `Splice.Wallet.TransferOffer:TransferOffer`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.9 -> 0.1.10, Splice.Wallet.TransferOffer:48 -> Splice.Wallet.TransferOffer:48:

```diff
+     ensure
+       DA.Text.length description < 280 &&
+       DA.Text.length trackingId < 280
-     ensure (DA.Text.length description < 280)
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.Wallet.Install:52:

```text
(source text identical: the difference comes from code this function calls)
```

