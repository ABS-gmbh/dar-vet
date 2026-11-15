# dar-vet: splice-amulet 0.1.9 -> 0.1.10

Old package ID `a5b055492fb8f08b2e7bc0fc94da6da50c39c2e1d7f24cd5ea8db12fc87c1332`, new package ID `17e51d96dbe9e41e7e5f621861419905206c12aeef77956c0a0da14714c1fa62`.

**25 findings:** 2 HIGH, 9 MEDIUM, 14 LOW.

## HIGH - new-choice - `Splice.AmuletRules:ExternalPartySetupProposal, choice `ExternalPartySetupProposal_Reject``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: none.

Version 0.1.10, Splice.AmuletRules:1382:

```daml
    choice ExternalPartySetupProposal_Reject : ExternalPartySetupProposal_RejectResult
      with
        reason : Text
      controller user
      do return ExternalPartySetupProposal_RejectResult with dummyArg = ()
```

## HIGH - new-choice - `Splice.AmuletRules:ExternalPartySetupProposal, choice `ExternalPartySetupProposal_Withdraw``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: none.

Version 0.1.10, Splice.AmuletRules:1389:

```daml
    choice ExternalPartySetupProposal_Withdraw : ExternalPartySetupProposal_WithdrawResult
      with
        reason : Text
      controller validator
      do return ExternalPartySetupProposal_WithdrawResult with dummyArg = ()
```

## MEDIUM - ensure-changed - `Splice.Amulet:Amulet`

Ensure clause changed.

- The change is inside the helper function(s) Splice.Fees:validExpiringAmount, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.Fees:118 -> Splice.Fees:131:

```diff
+ validExpiringAmount e =
+   e.initialAmount > 0.0 &&
+   e.ratePerRound.rate >= 0.0 &&
+   e.createdAt >= Round 0
- validExpiringAmount e = e.initialAmount > 0.0
```

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, Splice.Issuance:validIssuanceConfig, Splice.Issuance:validIssuanceCurve, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.AmuletConfig:55 -> Splice.AmuletConfig:56:

```diff
+ validAmuletConfig AmuletConfig
+     -- intentional pattern match to avoid forgetting checking a field
+     { transferConfig
+     , issuanceCurve
+     , decentralizedSynchronizer
+     , tickDuration
+     , packageConfig
+     , transferPreapprovalFee
+     , featuredAppActivityMarkerAmount}
+   =
+     validTransferConfig transferConfig &&
+     validIssuanceCurve issuanceCurve &&
+     validAmuletDecentralizedSynchronizerConfig decentralizedSynchronizer &&
+     tickDuration > days 0 &&
+     validPackageConfig packageConfig &&
+     optional True (>= 0.0) transferPreapprovalFee &&
+     optional True (>= 0.0) featuredAppActivityMarkerAmount
- validAmuletConfig AmuletConfig{..} =
-     tickDuration > days 0
-   && validIssuanceCurve issuanceCurve
-   && validAmuletDecentralizedSynchronizerConfig decentralizedSynchronizer
```

Version 0.1.9 -> 0.1.10, Splice.Issuance:39 -> Splice.Issuance:39:

```diff
      && appRewardPercentage >= 0.0
+     && (validatorRewardPercentage + appRewardPercentage) <= 1.0
      && validatorRewardCap >= 0.0
```

Version 0.1.9 -> 0.1.10, Splice.Issuance:36:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Ledger effects changed. Added: exercise self:Splice.Amulet:UnclaimedActivityRecord.Archive, fetch self:Splice.Amulet:UnclaimedActivityRecord. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Expiry:doesLockExpireBeforeAmulet, Splice.Expiry:estimateOpenRoundCreatedAt, Splice.Fees:chargeSteppedRate, Splice.Fees:expiringAmount, Splice.Fees:goChargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:657:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1076:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:691 -> Splice.AmuletRules:700:

```diff
            nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
            TextMap.empty
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:967:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1549:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:802 -> Splice.AmuletRules:815:

```diff
        changeToHoldingFeesRate = 0.0
+       totalUnclaimedActivityRecordAmount = Some 0.0
  
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount + coupon.amount * miningRound.issuancePerValidatorRewardCoupon
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
...
  
+     summarizeAndConsumeInput _round s (InputUnclaimedActivityRecord unclaimedActivityRecordCid) = do
+       unclaimedActivityRecord <- fetchAndArchive forOwner unclaimedActivityRecordCid
+       assertWithinDeadline "UnclaimedActivityRecord.expiresAt" unclaimedActivityRecord.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount =
+           (+ unclaimedActivityRecord.amount) <$> s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:773:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:955 -> Splice.AmuletRules:990:

```diff
        + inp.totalValidatorRewardAmount
+       + fromOptional 0.0 inp.totalUnclaimedActivityRecordAmount
        + inp.totalValidatorFaucetAmount
...
        inputValidatorRewardAmount = inp.totalValidatorRewardAmount + inp.totalValidatorFaucetAmount
+       inputUnclaimedActivityRecordAmount = inp.totalUnclaimedActivityRecordAmount
        inputValidatorFaucetAmount = Some inp.totalValidatorFaucetAmount
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:187 -> Splice.Expiry:187:

```diff
        expiringRound = addToBoundedRound r (RelRound 2)
+       optAmuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
+   in case optAmuletExpiresAt of
+     None -> False -- as the amulet is already expired
+     Some amuletExpiresAt -> lock.expiresAt `boundedLessTime` amuletExpiresAt
-       amuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
-   in lock.expiresAt `boundedLessTime` amuletExpiresAt
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:162 -> Splice.Expiry:162:

```diff
  estimateOpenRoundCreatedAt tickDuration currentMiningRound (Singleton targetRound)
+   | currentMiningRound.round >= targetRound = None
+   | otherwise =
-   | currentMiningRound.round >= targetRound = error $ "Target round has to be after or equal to the current OpenMiningRound"
-   | otherwise = do
      let relRound = subRound targetRound currentMiningRound.round
...
          relTime = convertMicrosecondsToRelTime <$> relMicros
+     in Some (addBoundedRelTime currentMiningRound.opensAt relTime)
+ estimateOpenRoundCreatedAt _ _ AfterMaxBound = Some AfterMaxBound
-     addBoundedRelTime currentMiningRound.opensAt relTime
- estimateOpenRoundCreatedAt _ _ AfterMaxBound = AfterMaxBound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:89:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.Fees:131 -> Splice.Fees:147:

```diff
+ expiringAmount ratePerRound initialAmount createdAt
+   | validExpiringAmount amount = amount
+   | otherwise = error $ "Invalid ExpiringAmount: " <> show amount
+   where
+     amount = ExpiringAmount with initialAmount; createdAt; ratePerRound
- expiringAmount ratePerRound initialAmount createdAt =
-   ExpiringAmount with initialAmount; createdAt; ratePerRound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:90 -> Splice.Fees:101:

```diff
  goChargeSteppedRate (remainder, currentRate, rate) [] = rate + remainder * currentRate
+ goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps)
+   | step <= 0.0 = error $ "SteppedRate step must be positive: " <> show (step, steppedRate)
+   | otherwise =
+       let remainder' = remainder - step
+           rate' = min remainder step * currentRate + rate
+       in goChargeSteppedRate (remainder', steppedRate, rate') steps
- goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps) =
-   let remainder' = remainder - step
-       rate' = min remainder step * currentRate + rate
-   in goChargeSteppedRate (remainder', steppedRate, rate') steps
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Ledger effects changed. Added: exercise self:Splice.Amulet:UnclaimedActivityRecord.Archive, fetch self:Splice.Amulet:UnclaimedActivityRecord. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Expiry:doesLockExpireBeforeAmulet, Splice.Expiry:estimateOpenRoundCreatedAt, Splice.Fees:chargeSteppedRate, Splice.Fees:expiringAmount, Splice.Fees:goChargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:657:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1541:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1076:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:691 -> Splice.AmuletRules:700:

```diff
            nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
            TextMap.empty
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:967:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1549:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:802 -> Splice.AmuletRules:815:

```diff
        changeToHoldingFeesRate = 0.0
+       totalUnclaimedActivityRecordAmount = Some 0.0
  
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount + coupon.amount * miningRound.issuancePerValidatorRewardCoupon
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
...
  
+     summarizeAndConsumeInput _round s (InputUnclaimedActivityRecord unclaimedActivityRecordCid) = do
+       unclaimedActivityRecord <- fetchAndArchive forOwner unclaimedActivityRecordCid
+       assertWithinDeadline "UnclaimedActivityRecord.expiresAt" unclaimedActivityRecord.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount =
+           (+ unclaimedActivityRecord.amount) <$> s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:773:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:955 -> Splice.AmuletRules:990:

```diff
        + inp.totalValidatorRewardAmount
+       + fromOptional 0.0 inp.totalUnclaimedActivityRecordAmount
        + inp.totalValidatorFaucetAmount
...
        inputValidatorRewardAmount = inp.totalValidatorRewardAmount + inp.totalValidatorFaucetAmount
+       inputUnclaimedActivityRecordAmount = inp.totalUnclaimedActivityRecordAmount
        inputValidatorFaucetAmount = Some inp.totalValidatorFaucetAmount
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:187 -> Splice.Expiry:187:

```diff
        expiringRound = addToBoundedRound r (RelRound 2)
+       optAmuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
+   in case optAmuletExpiresAt of
+     None -> False -- as the amulet is already expired
+     Some amuletExpiresAt -> lock.expiresAt `boundedLessTime` amuletExpiresAt
-       amuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
-   in lock.expiresAt `boundedLessTime` amuletExpiresAt
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:162 -> Splice.Expiry:162:

```diff
  estimateOpenRoundCreatedAt tickDuration currentMiningRound (Singleton targetRound)
+   | currentMiningRound.round >= targetRound = None
+   | otherwise =
-   | currentMiningRound.round >= targetRound = error $ "Target round has to be after or equal to the current OpenMiningRound"
-   | otherwise = do
      let relRound = subRound targetRound currentMiningRound.round
...
          relTime = convertMicrosecondsToRelTime <$> relMicros
+     in Some (addBoundedRelTime currentMiningRound.opensAt relTime)
+ estimateOpenRoundCreatedAt _ _ AfterMaxBound = Some AfterMaxBound
-     addBoundedRelTime currentMiningRound.opensAt relTime
- estimateOpenRoundCreatedAt _ _ AfterMaxBound = AfterMaxBound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:89:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.Fees:131 -> Splice.Fees:147:

```diff
+ expiringAmount ratePerRound initialAmount createdAt
+   | validExpiringAmount amount = amount
+   | otherwise = error $ "Invalid ExpiringAmount: " <> show amount
+   where
+     amount = ExpiringAmount with initialAmount; createdAt; ratePerRound
- expiringAmount ratePerRound initialAmount createdAt =
-   ExpiringAmount with initialAmount; createdAt; ratePerRound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:90 -> Splice.Fees:101:

```diff
  goChargeSteppedRate (remainder, currentRate, rate) [] = rate + remainder * currentRate
+ goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps)
+   | step <= 0.0 = error $ "SteppedRate step must be positive: " <> show (step, steppedRate)
+   | otherwise =
+       let remainder' = remainder - step
+           rate' = min remainder step * currentRate + rate
+       in goChargeSteppedRate (remainder', steppedRate, rate') steps
- goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps) =
-   let remainder' = remainder - step
-       rate' = min remainder step * currentRate + rate
-   in goChargeSteppedRate (remainder', steppedRate, rate') steps
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Ledger effects changed. Added: exercise self:Splice.Amulet:UnclaimedActivityRecord.Archive, fetch self:Splice.Amulet:UnclaimedActivityRecord. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Expiry:doesLockExpireBeforeAmulet, Splice.Expiry:estimateOpenRoundCreatedAt, Splice.Fees:chargeSteppedRate, Splice.Fees:expiringAmount, Splice.Fees:goChargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:657:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1541:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1076:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:691 -> Splice.AmuletRules:700:

```diff
            nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
            TextMap.empty
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:967:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1549:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:802 -> Splice.AmuletRules:815:

```diff
        changeToHoldingFeesRate = 0.0
+       totalUnclaimedActivityRecordAmount = Some 0.0
  
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount + coupon.amount * miningRound.issuancePerValidatorRewardCoupon
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
...
  
+     summarizeAndConsumeInput _round s (InputUnclaimedActivityRecord unclaimedActivityRecordCid) = do
+       unclaimedActivityRecord <- fetchAndArchive forOwner unclaimedActivityRecordCid
+       assertWithinDeadline "UnclaimedActivityRecord.expiresAt" unclaimedActivityRecord.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount =
+           (+ unclaimedActivityRecord.amount) <$> s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:773:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:955 -> Splice.AmuletRules:990:

```diff
        + inp.totalValidatorRewardAmount
+       + fromOptional 0.0 inp.totalUnclaimedActivityRecordAmount
        + inp.totalValidatorFaucetAmount
...
        inputValidatorRewardAmount = inp.totalValidatorRewardAmount + inp.totalValidatorFaucetAmount
+       inputUnclaimedActivityRecordAmount = inp.totalUnclaimedActivityRecordAmount
        inputValidatorFaucetAmount = Some inp.totalValidatorFaucetAmount
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:187 -> Splice.Expiry:187:

```diff
        expiringRound = addToBoundedRound r (RelRound 2)
+       optAmuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
+   in case optAmuletExpiresAt of
+     None -> False -- as the amulet is already expired
+     Some amuletExpiresAt -> lock.expiresAt `boundedLessTime` amuletExpiresAt
-       amuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
-   in lock.expiresAt `boundedLessTime` amuletExpiresAt
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:162 -> Splice.Expiry:162:

```diff
  estimateOpenRoundCreatedAt tickDuration currentMiningRound (Singleton targetRound)
+   | currentMiningRound.round >= targetRound = None
+   | otherwise =
-   | currentMiningRound.round >= targetRound = error $ "Target round has to be after or equal to the current OpenMiningRound"
-   | otherwise = do
      let relRound = subRound targetRound currentMiningRound.round
...
          relTime = convertMicrosecondsToRelTime <$> relMicros
+     in Some (addBoundedRelTime currentMiningRound.opensAt relTime)
+ estimateOpenRoundCreatedAt _ _ AfterMaxBound = Some AfterMaxBound
-     addBoundedRelTime currentMiningRound.opensAt relTime
- estimateOpenRoundCreatedAt _ _ AfterMaxBound = AfterMaxBound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:89:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.Fees:131 -> Splice.Fees:147:

```diff
+ expiringAmount ratePerRound initialAmount createdAt
+   | validExpiringAmount amount = amount
+   | otherwise = error $ "Invalid ExpiringAmount: " <> show amount
+   where
+     amount = ExpiringAmount with initialAmount; createdAt; ratePerRound
- expiringAmount ratePerRound initialAmount createdAt =
-   ExpiringAmount with initialAmount; createdAt; ratePerRound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:90 -> Splice.Fees:101:

```diff
  goChargeSteppedRate (remainder, currentRate, rate) [] = rate + remainder * currentRate
+ goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps)
+   | step <= 0.0 = error $ "SteppedRate step must be positive: " <> show (step, steppedRate)
+   | otherwise =
+       let remainder' = remainder - step
+           rate' = min remainder step * currentRate + rate
+       in goChargeSteppedRate (remainder', steppedRate, rate') steps
- goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps) =
-   let remainder' = remainder - step
-       rate' = min remainder step * currentRate + rate
-   in goChargeSteppedRate (remainder', steppedRate, rate') steps
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Ledger effects changed. Added: exercise self:Splice.Amulet:UnclaimedActivityRecord.Archive, fetch self:Splice.Amulet:UnclaimedActivityRecord. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Expiry:doesLockExpireBeforeAmulet, Splice.Expiry:estimateOpenRoundCreatedAt, Splice.Fees:chargeSteppedRate, Splice.Fees:expiringAmount, Splice.Fees:goChargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:657:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1076:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:691 -> Splice.AmuletRules:700:

```diff
            nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
            TextMap.empty
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:967:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:802 -> Splice.AmuletRules:815:

```diff
        changeToHoldingFeesRate = 0.0
+       totalUnclaimedActivityRecordAmount = Some 0.0
  
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount + coupon.amount * miningRound.issuancePerValidatorRewardCoupon
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
...
  
+     summarizeAndConsumeInput _round s (InputUnclaimedActivityRecord unclaimedActivityRecordCid) = do
+       unclaimedActivityRecord <- fetchAndArchive forOwner unclaimedActivityRecordCid
+       assertWithinDeadline "UnclaimedActivityRecord.expiresAt" unclaimedActivityRecord.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount =
+           (+ unclaimedActivityRecord.amount) <$> s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:773:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:955 -> Splice.AmuletRules:990:

```diff
        + inp.totalValidatorRewardAmount
+       + fromOptional 0.0 inp.totalUnclaimedActivityRecordAmount
        + inp.totalValidatorFaucetAmount
...
        inputValidatorRewardAmount = inp.totalValidatorRewardAmount + inp.totalValidatorFaucetAmount
+       inputUnclaimedActivityRecordAmount = inp.totalUnclaimedActivityRecordAmount
        inputValidatorFaucetAmount = Some inp.totalValidatorFaucetAmount
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:187 -> Splice.Expiry:187:

```diff
        expiringRound = addToBoundedRound r (RelRound 2)
+       optAmuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
+   in case optAmuletExpiresAt of
+     None -> False -- as the amulet is already expired
+     Some amuletExpiresAt -> lock.expiresAt `boundedLessTime` amuletExpiresAt
-       amuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
-   in lock.expiresAt `boundedLessTime` amuletExpiresAt
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:162 -> Splice.Expiry:162:

```diff
  estimateOpenRoundCreatedAt tickDuration currentMiningRound (Singleton targetRound)
+   | currentMiningRound.round >= targetRound = None
+   | otherwise =
-   | currentMiningRound.round >= targetRound = error $ "Target round has to be after or equal to the current OpenMiningRound"
-   | otherwise = do
      let relRound = subRound targetRound currentMiningRound.round
...
          relTime = convertMicrosecondsToRelTime <$> relMicros
+     in Some (addBoundedRelTime currentMiningRound.opensAt relTime)
+ estimateOpenRoundCreatedAt _ _ AfterMaxBound = Some AfterMaxBound
-     addBoundedRelTime currentMiningRound.opensAt relTime
- estimateOpenRoundCreatedAt _ _ AfterMaxBound = AfterMaxBound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:89:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.Fees:131 -> Splice.Fees:147:

```diff
+ expiringAmount ratePerRound initialAmount createdAt
+   | validExpiringAmount amount = amount
+   | otherwise = error $ "Invalid ExpiringAmount: " <> show amount
+   where
+     amount = ExpiringAmount with initialAmount; createdAt; ratePerRound
- expiringAmount ratePerRound initialAmount createdAt =
-   ExpiringAmount with initialAmount; createdAt; ratePerRound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:90 -> Splice.Fees:101:

```diff
  goChargeSteppedRate (remainder, currentRate, rate) [] = rate + remainder * currentRate
+ goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps)
+   | step <= 0.0 = error $ "SteppedRate step must be positive: " <> show (step, steppedRate)
+   | otherwise =
+       let remainder' = remainder - step
+           rate' = min remainder step * currentRate + rate
+       in goChargeSteppedRate (remainder', steppedRate, rate') steps
- goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps) =
-   let remainder' = remainder - step
-       rate' = min remainder step * currentRate + rate
-   in goChargeSteppedRate (remainder', steppedRate, rate') steps
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Ledger effects changed. Added: exercise self:Splice.Amulet:UnclaimedActivityRecord.Archive, fetch self:Splice.Amulet:UnclaimedActivityRecord. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Expiry:doesLockExpireBeforeAmulet, Splice.Expiry:estimateOpenRoundCreatedAt, Splice.Fees:chargeSteppedRate, Splice.Fees:expiringAmount, Splice.Fees:goChargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:657:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1541:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1076:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:691 -> Splice.AmuletRules:700:

```diff
            nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
            TextMap.empty
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:967:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1549:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:802 -> Splice.AmuletRules:815:

```diff
        changeToHoldingFeesRate = 0.0
+       totalUnclaimedActivityRecordAmount = Some 0.0
  
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount + coupon.amount * miningRound.issuancePerValidatorRewardCoupon
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
...
  
+     summarizeAndConsumeInput _round s (InputUnclaimedActivityRecord unclaimedActivityRecordCid) = do
+       unclaimedActivityRecord <- fetchAndArchive forOwner unclaimedActivityRecordCid
+       assertWithinDeadline "UnclaimedActivityRecord.expiresAt" unclaimedActivityRecord.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount =
+           (+ unclaimedActivityRecord.amount) <$> s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
...
          totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
          totalValidatorFaucetAmount =
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:773:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:955 -> Splice.AmuletRules:990:

```diff
        + inp.totalValidatorRewardAmount
+       + fromOptional 0.0 inp.totalUnclaimedActivityRecordAmount
        + inp.totalValidatorFaucetAmount
...
        inputValidatorRewardAmount = inp.totalValidatorRewardAmount + inp.totalValidatorFaucetAmount
+       inputUnclaimedActivityRecordAmount = inp.totalUnclaimedActivityRecordAmount
        inputValidatorFaucetAmount = Some inp.totalValidatorFaucetAmount
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:187 -> Splice.Expiry:187:

```diff
        expiringRound = addToBoundedRound r (RelRound 2)
+       optAmuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
+   in case optAmuletExpiresAt of
+     None -> False -- as the amulet is already expired
+     Some amuletExpiresAt -> lock.expiresAt `boundedLessTime` amuletExpiresAt
-       amuletExpiresAt = estimateOpenRoundCreatedAt tickDuration currentOpenRound expiringRound
-   in lock.expiresAt `boundedLessTime` amuletExpiresAt
```

Version 0.1.9 -> 0.1.10, Splice.Expiry:162 -> Splice.Expiry:162:

```diff
  estimateOpenRoundCreatedAt tickDuration currentMiningRound (Singleton targetRound)
+   | currentMiningRound.round >= targetRound = None
+   | otherwise =
-   | currentMiningRound.round >= targetRound = error $ "Target round has to be after or equal to the current OpenMiningRound"
-   | otherwise = do
      let relRound = subRound targetRound currentMiningRound.round
...
          relTime = convertMicrosecondsToRelTime <$> relMicros
+     in Some (addBoundedRelTime currentMiningRound.opensAt relTime)
+ estimateOpenRoundCreatedAt _ _ AfterMaxBound = Some AfterMaxBound
-     addBoundedRelTime currentMiningRound.opensAt relTime
- estimateOpenRoundCreatedAt _ _ AfterMaxBound = AfterMaxBound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:89:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.Fees:131 -> Splice.Fees:147:

```diff
+ expiringAmount ratePerRound initialAmount createdAt
+   | validExpiringAmount amount = amount
+   | otherwise = error $ "Invalid ExpiringAmount: " <> show amount
+   where
+     amount = ExpiringAmount with initialAmount; createdAt; ratePerRound
- expiringAmount ratePerRound initialAmount createdAt =
-   ExpiringAmount with initialAmount; createdAt; ratePerRound
```

Version 0.1.9 -> 0.1.10, Splice.Fees:90 -> Splice.Fees:101:

```diff
  goChargeSteppedRate (remainder, currentRate, rate) [] = rate + remainder * currentRate
+ goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps)
+   | step <= 0.0 = error $ "SteppedRate step must be positive: " <> show (step, steppedRate)
+   | otherwise =
+       let remainder' = remainder - step
+           rate' = min remainder step * currentRate + rate
+       in goChargeSteppedRate (remainder', steppedRate, rate') steps
- goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps) =
-   let remainder' = remainder - step
-       rate' = min remainder step * currentRate + rate
-   in goChargeSteppedRate (remainder', steppedRate, rate') steps
```

## MEDIUM - ensure-changed - `Splice.DecentralizedSynchronizer:MemberTraffic`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.9 -> 0.1.10, Splice.DecentralizedSynchronizer:69 -> Splice.DecentralizedSynchronizer:70:

```diff
        && migrationId >= 0
+       && not (T.isEmpty memberId)
+       && not (T.isEmpty synchronizerId)
```

## MEDIUM - ensure-changed - `Splice.Round:OpenMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.9 -> 0.1.10, Splice.Round:33:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:Amulet, choice `Amulet_Expire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllOpenRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.Amulet:418:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmulet``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllOpenRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.Amulet:418:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_OwnerExpireLock``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.Amulet:160:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_Unlock``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.Amulet:147:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - template-added - `Splice.Amulet:UnclaimedActivityRecord`

New template. Review it like a new package.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AdvanceOpenMiningRounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:353:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeFees, Splice.AmuletRules:computeOutputFee, Splice.Fees:chargeSteppedRate, Splice.Fees:goChargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:651:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:657:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.Fees:89:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.9 -> 0.1.10, Splice.Fees:90 -> Splice.Fees:101:

```diff
  goChargeSteppedRate (remainder, currentRate, rate) [] = rate + remainder * currentRate
+ goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps)
+   | step <= 0.0 = error $ "SteppedRate step must be positive: " <> show (step, steppedRate)
+   | otherwise =
+       let remainder' = remainder - step
+           rate' = min remainder step * currentRate + rate
+       in goChargeSteppedRate (remainder', steppedRate, rate') steps
- goChargeSteppedRate (remainder, currentRate, rate) ((step, steppedRate) :: steps) =
-   let remainder' = remainder - step
-       rate' = min remainder step * currentRate + rate
-   in goChargeSteppedRate (remainder', steppedRate, rate') steps
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:611:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:219:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Mint``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:253 -> Splice.AmuletRules:256:

```diff
        do
+         require "isDevNet flag is true" isDevNet
          openRound <- fetchReferenceData (ForDso with dso) openRound
```

## LOW - body-changed - `Splice.AmuletRules:ExternalPartySetupProposal, choice `ExternalPartySetupProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1323 -> Splice.AmuletRules:1364:

```diff
        do
+         assertWithinDeadline "ExternalPartySetupProposal.preapprovalExpiresAt" preapprovalExpiresAt
          validatorRightCid <- create ValidatorRight with
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Send``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.AmuletRules:1443:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.ExternalPartyAmuletRules:TransferCommand, choice `TransferCommand_Send``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.ExternalPartyAmuletRules:129:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_RecordValidatorLivenessActivity``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.9 -> 0.1.10, Splice.ValidatorLicense:74:

```text
(source text identical: the difference comes from code this function calls)
```

