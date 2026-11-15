# dar-vet: splice-amulet 0.1.14 -> 0.1.15

Old package ID `3ca1343ab26b453d38c8adb70dca5f1ead8440c42b59b68f070786955cbf9ec1`, new package ID `67fac2f853bce8dbf0b9817bb5ba7c59f10e8120b7c808696f7010e5f0c8a791`.

**25 findings:** 3 HIGH, 8 MEDIUM, 14 LOW.

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AllocateDevelopmentFundCoupon``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:DevelopmentFundCoupon, create self:Splice.Amulet:UnclaimedDevelopmentFundCoupon, exercise self:Splice.Amulet:UnclaimedDevelopmentFundCoupon.Archive, fetch self:Splice.Amulet:UnclaimedDevelopmentFundCoupon.

Version 0.1.15, Splice.AmuletRules:617:

```daml
    nonconsuming choice AmuletRules_AllocateDevelopmentFundCoupon : AmuletRules_AllocateDevelopmentFundCouponResult
      -- ^ Allows the Development Fund manager to allocate a specified amount from a
      -- collection of UnclaimedDevelopmentFundCoupons to a beneficiary, generating a
      -- DevelopmentFundCoupon and producing a leftover unclaimed coupon if applicable.
      with
        unclaimedDevelopmentFundCouponCids : [ContractId UnclaimedDevelopmentFundCoupon]
        beneficiary : Party
        amount : Decimal
        expiresAt : Time
        reason : Text
        fundManager : Party
      controller fundManager
      do
        -- Verify expiry
        assertWithinDeadline "DevelopmentFundCoupon.expiresAt" expiresAt

        -- Verify fundManager
        configUsd <- getValueAsOfLedgerTime configSchedule
        case configUsd.optDevelopmentFundManager of
          None -> abort "DevelopmentFundCoupon cannot be allocated without a Development Fund manager configured"
          Some developmentFundManager ->
            require ("controller is the configured Development Fund manager: " <> show developmentFundManager) $
              developmentFundManager == fundManager

        -- Verify the amount and create a leftover UnclaimedDevelopmentFundCoupon if needed
        totalUnclaimedDevelopmentFundCoupons <- sum <$> forA unclaimedDevelopmentFundCouponCids \cid -> do
          coupon <- fetchAndArchive (ForDso with dso) cid
          pure coupon.amount
        let leftover = totalUnclaimedDevelopmentFundCoupons - amount
        optUnclaimedDevelopmentFundCouponCid <-
          if | leftover < 0.0 -> abort $ "insufficient amount to cover the requested allocation: " <> show (negate leftover)
             | leftover == 0.0 -> pure None
             | otherwise -> Some <$> create UnclaimedDevelopmentFundCoupon with dso; amount = leftover

        -- record development fund coupon contract
        developmentFundCouponCid <- create DevelopmentFundCoupon with
          dso; beneficiary; fundManager; amount; expiresAt; reason

        return AmuletRules_AllocateDevelopmentFundCouponResult with
          developmentFundCouponCid
          optUnclaimedDevelopmentFundCouponCid
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeUnclaimedDevelopmentFundCoupons``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:UnclaimedDevelopmentFundCoupon, exercise self:Splice.Amulet:UnclaimedDevelopmentFundCoupon.Archive, fetch self:Splice.Amulet:UnclaimedDevelopmentFundCoupon.

Version 0.1.15, Splice.AmuletRules:597:

```daml
    nonconsuming choice AmuletRules_MergeUnclaimedDevelopmentFundCoupons : AmuletRules_MergeUnclaimedDevelopmentFundCouponsResult
      -- ^ Batch merge of unclaimed development fund coupons
      with
        unclaimedDevelopmentFundCouponCids : [ContractId UnclaimedDevelopmentFundCoupon]
      controller dso
      do
        require "More than one unclaimed development fund coupon contract" (length unclaimedDevelopmentFundCouponCids > 1)

        -- archive all given coupons
        archivedAmounts <- forA unclaimedDevelopmentFundCouponCids $ \unclaimedDevelopmentFundCouponCid -> do
          unclaimedDevelopmentFundCoupon <- fetchAndArchive (ForDso with dso) unclaimedDevelopmentFundCouponCid
          pure unclaimedDevelopmentFundCoupon.amount

        -- create a new unclaimed development fund coupon over the total
        unclaimedDevelopmentFundCouponCid <- create UnclaimedDevelopmentFundCoupon with
          dso
          amount = sum archivedAmounts

        return AmuletRules_MergeUnclaimedDevelopmentFundCouponsResult with ..
```

## HIGH - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_StartIssuing``

Ledger effects changed. Added: create self:Splice.Amulet:UnclaimedDevelopmentFundCoupon. Removed: none.

- The choice is non-consuming, so it can be repeated: each call now creates a contract.
- The change is inside the helper function(s) Splice.Issuance:computeIssuingRoundParameters, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.Issuance:86 -> Splice.Issuance:92:

```diff
        issuancePerValidatorFaucetCoupon = validatorFaucetIssuance.issuancePerCoupon
+       optAmuletsToIssueToDevelopmentFund = Some amuletsToIssueToDevelopmentFund
    where
+     developmentFundPercentage = fromOptional 0.05 config.optDevelopmentFundPercentage
+ 
      amuletsToIssueToSvs =
+       adjustedAmuletsToIssueInRound
+         - validatorRewardIssuance.rewardsToIssue
+         - unfeaturedAppIssuance.rewardsToIssue
-       amuletsToIssueInRound - validatorRewardIssuance.rewardsToIssue - unfeaturedAppIssuance.rewardsToIssue
  
...
  
+     amuletsToIssueToDevelopmentFund = amuletsToIssueInRound * developmentFundPercentage
+     adjustedAmuletsToIssueInRound = amuletsToIssueInRound - amuletsToIssueToDevelopmentFund
+ 
      validatorRewardIssuance = computeIssuanceTranche
+       (adjustedAmuletsToIssueInRound * config.validatorRewardPercentage)
-       (amuletsToIssueInRound * config.validatorRewardPercentage)
        config.validatorRewardCap
...
      unfeaturedAppIssuance = computeIssuanceTranche
+       (adjustedAmuletsToIssueInRound * config.appRewardPercentage)
-       (amuletsToIssueInRound * config.appRewardPercentage)
        config.unfeaturedAppRewardCap
```

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, Splice.Issuance:validIssuanceConfig, Splice.Issuance:validIssuanceCurve, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.AmuletConfig:58:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.Issuance:39 -> Splice.Issuance:41:

```diff
      && getValidatorFaucetCap this >= 0.0
+     && optional True (\pct -> pct >= 0.0 && pct <= 1.0) optDevelopmentFundPercentage
```

Version 0.1.14 -> 0.1.15, Splice.Issuance:38:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Ledger effects changed. Added: exercise self:Splice.Amulet:DevelopmentFundCoupon.Archive, fetch self:Splice.Amulet:DevelopmentFundCoupon. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeSynchronizerFees, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1256:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1204:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:720 -> Splice.AmuletRules:797:

```diff
            optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
+           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
            TextMap.empty
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1683:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:835 -> Splice.AmuletRules:916:

```diff
      forOwner = ForOwner with dso; owner = sender
+     forDso = ForDso with dso
  
...
        totalUnclaimedActivityRecordAmount = Some 0.0
+       totalDevelopmentFundAmount = Some 0.0
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate - amulet.amount.ratePerRound.rate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
        -- we must and do use the validator right to archive the coupon of the user
+       coupon <- fetchButArchiveLater forDso couponCid
-       coupon <- fetchButArchiveLater (ForDso with dso) couponCid
        do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
+     summarizeAndConsumeInput _round s (InputDevelopmentFundCoupon couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = (+ coupon.amount) <$> s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:874:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1010 -> Splice.AmuletRules:1115:

```diff
        + inp.totalSvRewardAmount
+       + fromOptional 0.0 inp.totalDevelopmentFundAmount
        - totalOutputAmount - sum outputFees
...
        inputSvRewardAmount = inp.totalSvRewardAmount
+       inputDevelopmentFundAmount = inp.totalDevelopmentFundAmount
        holdingFees = inp.totalHoldingFees
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Ledger effects changed. Added: exercise self:Splice.Amulet:DevelopmentFundCoupon.Archive, fetch self:Splice.Amulet:DevelopmentFundCoupon. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1675:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1204:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:720 -> Splice.AmuletRules:797:

```diff
            optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
+           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
            TextMap.empty
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1683:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:835 -> Splice.AmuletRules:916:

```diff
      forOwner = ForOwner with dso; owner = sender
+     forDso = ForDso with dso
  
...
        totalUnclaimedActivityRecordAmount = Some 0.0
+       totalDevelopmentFundAmount = Some 0.0
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate - amulet.amount.ratePerRound.rate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
        -- we must and do use the validator right to archive the coupon of the user
+       coupon <- fetchButArchiveLater forDso couponCid
-       coupon <- fetchButArchiveLater (ForDso with dso) couponCid
        do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
+     summarizeAndConsumeInput _round s (InputDevelopmentFundCoupon couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = (+ coupon.amount) <$> s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:874:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1010 -> Splice.AmuletRules:1115:

```diff
        + inp.totalSvRewardAmount
+       + fromOptional 0.0 inp.totalDevelopmentFundAmount
        - totalOutputAmount - sum outputFees
...
        inputSvRewardAmount = inp.totalSvRewardAmount
+       inputDevelopmentFundAmount = inp.totalDevelopmentFundAmount
        holdingFees = inp.totalHoldingFees
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Ledger effects changed. Added: exercise self:Splice.Amulet:DevelopmentFundCoupon.Archive, fetch self:Splice.Amulet:DevelopmentFundCoupon. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1675:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1204:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:720 -> Splice.AmuletRules:797:

```diff
            optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
+           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
            TextMap.empty
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1683:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:835 -> Splice.AmuletRules:916:

```diff
      forOwner = ForOwner with dso; owner = sender
+     forDso = ForDso with dso
  
...
        totalUnclaimedActivityRecordAmount = Some 0.0
+       totalDevelopmentFundAmount = Some 0.0
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate - amulet.amount.ratePerRound.rate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
        -- we must and do use the validator right to archive the coupon of the user
+       coupon <- fetchButArchiveLater forDso couponCid
-       coupon <- fetchButArchiveLater (ForDso with dso) couponCid
        do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
+     summarizeAndConsumeInput _round s (InputDevelopmentFundCoupon couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = (+ coupon.amount) <$> s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:874:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1010 -> Splice.AmuletRules:1115:

```diff
        + inp.totalSvRewardAmount
+       + fromOptional 0.0 inp.totalDevelopmentFundAmount
        - totalOutputAmount - sum outputFees
...
        inputSvRewardAmount = inp.totalSvRewardAmount
+       inputDevelopmentFundAmount = inp.totalDevelopmentFundAmount
        holdingFees = inp.totalHoldingFees
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Ledger effects changed. Added: exercise self:Splice.Amulet:DevelopmentFundCoupon.Archive, fetch self:Splice.Amulet:DevelopmentFundCoupon. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1204:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:720 -> Splice.AmuletRules:797:

```diff
            optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
+           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
            TextMap.empty
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:835 -> Splice.AmuletRules:916:

```diff
      forOwner = ForOwner with dso; owner = sender
+     forDso = ForDso with dso
  
...
        totalUnclaimedActivityRecordAmount = Some 0.0
+       totalDevelopmentFundAmount = Some 0.0
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate - amulet.amount.ratePerRound.rate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
        -- we must and do use the validator right to archive the coupon of the user
+       coupon <- fetchButArchiveLater forDso couponCid
-       coupon <- fetchButArchiveLater (ForDso with dso) couponCid
        do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
+     summarizeAndConsumeInput _round s (InputDevelopmentFundCoupon couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = (+ coupon.amount) <$> s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:874:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1010 -> Splice.AmuletRules:1115:

```diff
        + inp.totalSvRewardAmount
+       + fromOptional 0.0 inp.totalDevelopmentFundAmount
        - totalOutputAmount - sum outputFees
...
        inputSvRewardAmount = inp.totalSvRewardAmount
+       inputDevelopmentFundAmount = inp.totalDevelopmentFundAmount
        holdingFees = inp.totalHoldingFees
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Ledger effects changed. Added: exercise self:Splice.Amulet:DevelopmentFundCoupon.Archive, fetch self:Splice.Amulet:DevelopmentFundCoupon. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1675:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1204:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:720 -> Splice.AmuletRules:797:

```diff
            optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
+           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
            TextMap.empty
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1683:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:835 -> Splice.AmuletRules:916:

```diff
      forOwner = ForOwner with dso; owner = sender
+     forDso = ForDso with dso
  
...
        totalUnclaimedActivityRecordAmount = Some 0.0
+       totalDevelopmentFundAmount = Some 0.0
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate - amulet.amount.ratePerRound.rate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
        -- we must and do use the validator right to archive the coupon of the user
+       coupon <- fetchButArchiveLater forDso couponCid
-       coupon <- fetchButArchiveLater (ForDso with dso) couponCid
        do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
  
+     summarizeAndConsumeInput _round s (InputDevelopmentFundCoupon couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = (+ coupon.amount) <$> s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:874:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:1010 -> Splice.AmuletRules:1115:

```diff
        + inp.totalSvRewardAmount
+       + fromOptional 0.0 inp.totalDevelopmentFundAmount
        - totalOutputAmount - sum outputFees
...
        inputSvRewardAmount = inp.totalSvRewardAmount
+       inputDevelopmentFundAmount = inp.totalDevelopmentFundAmount
        holdingFees = inp.totalHoldingFees
```

## MEDIUM - ensure-changed - `Splice.Round:OpenMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.14 -> 0.1.15, Splice.Round:33:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.Round:SummarizingMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.14 -> 0.1.15, Splice.Round:52:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:Amulet, choice `Amulet_Expire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllOpenRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.Amulet:488:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - template-added - `Splice.Amulet:DevelopmentFundCoupon`

New template. Review it like a new package.

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmulet``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllOpenRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.Amulet:488:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_OwnerExpireLock``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.Amulet:171:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_Unlock``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.Amulet:158:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - template-added - `Splice.Amulet:UnclaimedDevelopmentFundCoupon`

New template. Review it like a new package.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AdvanceOpenMiningRounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:393:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ClaimExpiredRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:518:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeFees, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:748:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:708:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:255:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Mint``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:292:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.AmuletRules:668:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_RecordValidatorLivenessActivity``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.14 -> 0.1.15, Splice.ValidatorLicense:74:

```text
(source text identical: the difference comes from code this function calls)
```

