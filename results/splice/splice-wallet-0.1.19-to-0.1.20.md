# dar-vet: splice-wallet 0.1.19 -> 0.1.20

Old package ID `1d8317b1e476c03ea2a85bed8435e5c182abe501db58350009187fa839ab2cca`, new package ID `e485c2eb59cca556e0e2ecfaaef45346dd0ba64f9976a3957938d814f846c78c`.

**4 findings:** 1 HIGH, 0 MEDIUM, 3 LOW.

## HIGH - new-choice - `Splice.Wallet.MintingDelegation:MintingDelegation, choice `MintingDelegation_AssignAndMint``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_Transfer, exercise splice-api-reward-assignment-v1:Splice.Api.RewardAssignmentV1:RewardCoupon.RewardCoupon_AssignBeneficiaries.

Version 0.1.20, Splice.Wallet.MintingDelegation:81:

```daml
    nonconsuming choice MintingDelegation_AssignAndMint : MintingDelegation_AssignAndMintResult
      with
        couponCid : ContractId Api.RewardAssignmentV1.RewardCoupon
          -- ^ Primary coupon to exercise assign on.
        assignBeneficiaries : Api.RewardAssignmentV1.RewardCoupon_AssignBeneficiaries
          -- ^ Assignment parameters: additionalCoupons, newBeneficiaries, extraArgs.
        inputs : [TransferInput]
          -- ^ Additional inputs to the transfer.
        context : PaymentTransferContext
          -- ^ Transfer context including amulet rules.
      controller delegate
      do
        assertWithinDeadline "expiresAt" expiresAt

        -- Assign reward coupons to new beneficiaries
        assignResult <- exercise couponCid assignBeneficiaries

        -- Find reward coupons assigned to the delegation's beneficiary and mint along with other inputs
        let myRewardInputs =
              [ InputRewardCouponV2 (fromInterfaceContractId @RewardCouponV2 cid)
              | (party, cids) <- assignResult.newBeneficiariesCouponCids
              , party == beneficiary
              , cid <- cids
              ]

        transferResult <- exercise context.amuletRules AmuletRules_Transfer with
          transfer = Transfer with
            sender = beneficiary
            provider = delegate
            inputs = inputs ++ myRewardInputs
            outputs = []
            beneficiaries = None
          context = context.context with featuredAppRight = None
          expectedDso = Some dso

        pure MintingDelegation_AssignAndMintResult with
          assignResult
          transferResult
```

## LOW - dependency-behaviour-changed - `(package)`

1 expressions are unchanged in this package but call dependency code that changed.

- Changed dependencies: splice-amulet 0.1.18 -> 0.1.19, splice-wallet-payments 0.1.18 -> 0.1.19.
- Affected: Splice.Wallet.TransferOffer:AcceptedTransferOffer AcceptedTransferOffer_Complete (body).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeSynchronizerFees, Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.Wallet.Install:42:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.Wallet.MintingDelegation:22:

```text
(source text identical: the difference comes from code this function calls)
```

