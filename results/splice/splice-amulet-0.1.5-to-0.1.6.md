# dar-vet: splice-amulet 0.1.5 -> 0.1.6

Old package ID `b4867a47abbfa2d15482f22c9c50516f0af14036287f299342f5d336391e4997`, new package ID `979ec710c3ae3a05cb44edf8461a9b4d7dd2053add95664f94fc89e5f18df80f`.

**11 findings:** 2 HIGH, 1 MEDIUM, 8 LOW.

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:Amulet, create self:Splice.Amulet:AppRewardCoupon, create self:Splice.Amulet:LockedAmulet, create self:Splice.Amulet:ValidatorRewardCoupon, create self:Splice.AmuletRules:ExternalPartySetupProposal, exercise self:Splice.Amulet:Amulet.Archive, exercise self:Splice.Amulet:AppRewardCoupon.Archive, exercise self:Splice.Amulet:SvRewardCoupon.SvRewardCoupon_ArchiveAsBeneficiary, exercise self:Splice.Amulet:ValidatorRewardCoupon.ValidatorRewardCoupon_ArchiveAsValidator, exercise self:Splice.Round:OpenMiningRound.OpenMiningRound_Fetch, exercise self:Splice.ValidatorLicense:ValidatorFaucetCoupon.Archive, exercise self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.Archive, fetch self:Splice.Amulet:Amulet, fetch self:Splice.Amulet:AppRewardCoupon, fetch self:Splice.Amulet:FeaturedAppRight, fetch self:Splice.Amulet:SvRewardCoupon, fetch self:Splice.Amulet:ValidatorRewardCoupon, fetch self:Splice.Amulet:ValidatorRight, fetch self:Splice.Round:IssuingMiningRound, fetch self:Splice.Round:OpenMiningRound, fetch self:Splice.ValidatorLicense:ValidatorFaucetCoupon, fetch self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.

Version 0.1.6, Splice.AmuletRules:112:

```daml
    nonconsuming choice AmuletRules_CreateExternalPartySetupProposal : AmuletRules_CreateExternalPartySetupProposalResult
      -- ^ Propose to host an external party
      -- The provider pre-pays the fees for the creation of a TransferPreapproval contract
      -- on behalf of the external party when exercising this choice
      with
        context : PaymentTransferContext
        inputs : [TransferInput]
        user : Party
        validator : Party
        preapprovalExpiresAt : Time
      controller validator
      do
        now <- getTime
        require "preapprovalExpiresAt is not in the past" (preapprovalExpiresAt > now)
        let configUsd = getValueAsOf now configSchedule
        (amuletPaid, _) <- computeTransferPreapprovalFee (preapprovalExpiresAt `subTime` now) configUsd context.context dso
        transferResult <- chargeTransferPreapprovalFee validator amuletPaid inputs context dso
        proposalCid <- create ExternalPartySetupProposal with
          user
          validator
          dso
          createdAt = now
          preapprovalExpiresAt
        return AmuletRules_CreateExternalPartySetupProposalResult with ..
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:Amulet, create self:Splice.Amulet:AppRewardCoupon, create self:Splice.Amulet:LockedAmulet, create self:Splice.Amulet:ValidatorRewardCoupon, create self:Splice.AmuletRules:TransferPreapproval, exercise self:Splice.Amulet:Amulet.Archive, exercise self:Splice.Amulet:AppRewardCoupon.Archive, exercise self:Splice.Amulet:SvRewardCoupon.SvRewardCoupon_ArchiveAsBeneficiary, exercise self:Splice.Amulet:ValidatorRewardCoupon.ValidatorRewardCoupon_ArchiveAsValidator, exercise self:Splice.Round:OpenMiningRound.OpenMiningRound_Fetch, exercise self:Splice.ValidatorLicense:ValidatorFaucetCoupon.Archive, exercise self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.Archive, fetch self:Splice.Amulet:Amulet, fetch self:Splice.Amulet:AppRewardCoupon, fetch self:Splice.Amulet:FeaturedAppRight, fetch self:Splice.Amulet:SvRewardCoupon, fetch self:Splice.Amulet:ValidatorRewardCoupon, fetch self:Splice.Amulet:ValidatorRight, fetch self:Splice.Round:IssuingMiningRound, fetch self:Splice.Round:OpenMiningRound, fetch self:Splice.ValidatorLicense:ValidatorFaucetCoupon, fetch self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.

Version 0.1.6, Splice.AmuletRules:137:

```daml
    nonconsuming choice AmuletRules_CreateTransferPreapproval : AmuletRules_CreateTransferPreapprovalResult
      -- ^ Pre-approve incoming amulet transfers
      with
        context : PaymentTransferContext
        inputs : [TransferInput]
        receiver : Party
        provider : Party
        expiresAt : Time
      controller provider, receiver
      do
        now <- getTime
        require "expiresAt is not in the past" (expiresAt > now)
        let configUsd = getValueAsOf now configSchedule
        (amuletPaid, _) <- computeTransferPreapprovalFee (expiresAt `subTime` now) configUsd context.context dso
        transferResult <- chargeTransferPreapprovalFee provider amuletPaid inputs context dso
        transferPreapprovalCid <- create TransferPreapproval with
          receiver
          provider
          dso
          validFrom = now
          lastRenewedAt = now
          expiresAt
        return AmuletRules_CreateTransferPreapprovalResult with ..
```

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.5 -> 0.1.6, Splice.AmuletRules:87:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.5 -> 0.1.6, Splice.AmuletRules:992:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.5 -> 0.1.6, Splice.AmuletRules:666:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.5 -> 0.1.6, Splice.AmuletRules:226:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.5 -> 0.1.6, Splice.AmuletRules:992:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.5 -> 0.1.6, Splice.AmuletRules:666:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - template-added - `Splice.AmuletRules:ExternalPartySetupProposal`

New template. Review it like a new package.

## LOW - template-added - `Splice.AmuletRules:TransferPreapproval`

New template. Review it like a new package.

## LOW - template-added - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules`

New template. Review it like a new package.

## LOW - template-added - `Splice.ExternalPartyAmuletRules:TransferCommand`

New template. Review it like a new package.

## LOW - template-added - `Splice.ExternalPartyAmuletRules:TransferCommandCounter`

New template. Review it like a new package.

