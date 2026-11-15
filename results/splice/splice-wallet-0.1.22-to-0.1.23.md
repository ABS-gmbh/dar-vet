# dar-vet: splice-wallet 0.1.22 -> 0.1.23

Old package ID `a78daeeb1a4a2405828b29f8c04814725dbd97ee5a60bd02e839f32518640afa`, new package ID `727c0fb9711d59b506fbb3cff2342e55eb583bb88682a946220525c304feca4a`.

**3 findings:** 0 HIGH, 0 MEDIUM, 3 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.Wallet.Install:49:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_FeaturedAppRights_SelfGrant``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.Wallet.Install:565 -> Splice.Wallet.Install:565:

```diff
        controller validatorParty
+       do result <- exercise amuletRulesCid $ AmuletRules_DevNet_FeatureApp with
+            provider = endUserParty
+            activityWeight = None
-       do result <- exercise amuletRulesCid $ AmuletRules_DevNet_FeatureApp endUserParty
           return WalletAppInstall_FeaturedAppRights_SelfGrantResult with
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.Wallet.MintingDelegation:22:

```text
(source text identical: the difference comes from code this function calls)
```

