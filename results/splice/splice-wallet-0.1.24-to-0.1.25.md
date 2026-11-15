# dar-vet: splice-wallet 0.1.24 -> 0.1.25

Old package ID `d42261314d98cc04bf50bcc58930af98b9f33aeed11339465925de697d3ba66e`, new package ID `916f16ea9413ba72d9ae660c9d0741f1078fc27b82b61e4b9db6a991dc47a848`.

**3 findings:** 0 HIGH, 1 MEDIUM, 2 LOW.

## MEDIUM - effects-changed - `Splice.Wallet.MintingDelegation:MintingDelegation, choice `MintingDelegation_AssignAndMint``

Ledger effects changed. Added: fetch splice-api-reward-assignment-v1:Splice.Api.RewardAssignmentV1:RewardCoupon. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.Wallet.MintingDelegation:81 -> Splice.Wallet.MintingDelegation:81:

```diff
  
+         _ <- fetchCheckedInterface (ForOwner with dso; owner = beneficiary) couponCid
+ 
          -- Assign reward coupons to new beneficiaries
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.Wallet.Install:50:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.Wallet.MintingDelegation:22:

```text
(source text identical: the difference comes from code this function calls)
```

