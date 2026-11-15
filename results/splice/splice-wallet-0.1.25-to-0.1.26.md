# dar-vet: splice-wallet 0.1.25 -> 0.1.26

Old package ID `916f16ea9413ba72d9ae660c9d0741f1078fc27b82b61e4b9db6a991dc47a848`, new package ID `5838793a7f11fbc16159065094823fb662120184ede48922dff092958dae6857`.

**4 findings:** 0 HIGH, 0 MEDIUM, 4 LOW.

## LOW - dependency-behaviour-changed - `(package)`

1 expressions are unchanged in this package but call dependency code that changed.

- Changed dependencies: daml-stdlib 3.5.2 -> 3.4.11, splice-amulet 0.1.24 -> 0.1.25, splice-token-standard-utils 2.0.0 -> 2.0.1, splice-util 0.1.8 -> 0.1.9, splice-wallet-payments 0.1.24 -> 0.1.25.
- Affected: Splice.Wallet.TransferOffer:AcceptedTransferOffer AcceptedTransferOffer_Complete (body).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeSynchronizerFees, Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.25 -> 0.1.26, Splice.Wallet.Install:50:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_FeaturedAppRights_SelfGrant``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.25 -> 0.1.26, Splice.Wallet.Install:584 -> Splice.Wallet.Install:584:

```diff
          amuletRulesCid : ContractId AmuletRules
+         activityWeight : Optional Decimal
+         appKind : Optional FeaturedAppKind
+         opsParties : Optional [Party]
        controller validatorParty
+       do require "opsParties has at most 10 entries" (optional 0 length opsParties <= 10)
+          result <- exercise amuletRulesCid $ AmuletRules_DevNet_FeatureApp with
-       do result <- exercise amuletRulesCid $ AmuletRules_DevNet_FeatureApp with
             provider = endUserParty
+            activityWeight
+            appKind
+            opsParties
-            activityWeight = None
           return WalletAppInstall_FeaturedAppRights_SelfGrantResult with
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.25 -> 0.1.26, Splice.Wallet.MintingDelegation:22:

```text
(source text identical: the difference comes from code this function calls)
```

