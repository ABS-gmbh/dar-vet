# dar-vet: splice-amulet 0.1.4 -> 0.1.5

Old package ID `a36ef8888fb44caae13d96341ce1fabd84fc9e2e7b209bbc3caabb48b6be1668`, new package ID `b4867a47abbfa2d15482f22c9c50516f0af14036287f299342f5d336391e4997`.

**7 findings:** 1 HIGH, 5 MEDIUM, 1 LOW.

## HIGH - new-choice - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_RecordValidatorLivenessActivity``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.ValidatorLicense:ValidatorLicense, create self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord, fetch self:Splice.Round:OpenMiningRound.

Version 0.1.5, Splice.ValidatorLicense:75:

```daml
    choice ValidatorLicense_RecordValidatorLivenessActivity : ValidatorLicense_RecordValidatorLivenessActivityResult
      with
        openRoundCid : ContractId OpenMiningRound
      controller validator
      do
        now <- getTime
        openRound <- fetchReferenceData (ForDso with dso) openRoundCid
        require "OpenRound is open" (openRound.opensAt <= now)

        newFaucetState <- case faucetState of
          None -> return $ Some FaucetState with
            firstReceivedFor = openRound.round
            lastReceivedFor = openRound.round
            numCouponsMissed = 0

          Some state -> do
            require "Not yet received" (state.lastReceivedFor < openRound.round)
            return $ Some FaucetState with
              firstReceivedFor = state.firstReceivedFor
              lastReceivedFor = openRound.round
              numCouponsMissed = state.numCouponsMissed + (openRound.round.number - state.lastReceivedFor.number - 1)

        licenseCid <- create this with faucetState = newFaucetState; lastActiveAt = Some now

        couponCid <- create ValidatorLivenessActivityRecord with
          dso
          validator
          round = openRound.round

        return ValidatorLicense_RecordValidatorLivenessActivityResult with ..
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Ledger effects changed. Added: exercise self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.Archive, fetch self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:summarizeAndConsumeInputs, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.4 -> 0.1.5, Splice.AmuletRules:942:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.4 -> 0.1.5, Splice.AmuletRules:616:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.4 -> 0.1.5, Splice.AmuletRules:706 -> Splice.AmuletRules:713:

```diff
  
+     summarizeAndConsumeInput _round s (InputValidatorLivenessActivityRecord recordCid) = do
+       record <- fetchAndArchive forOwner recordCid
+       -- compute balance change
+       miningRound <- getIssuingMiningRound csum record.round
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalValidatorFaucetAmount =
+           s.totalValidatorFaucetAmount + getIssuingMiningRoundIssuancePerValidatorFaucetCoupon miningRound
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+ 
      summarizeAndConsumeInput _round s (ExtTransferInput _dummyUnitField optInputValidatorFaucetCoupon) = do
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ClaimExpiredRewards``

Ledger effects changed. Added: exercise self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.ValidatorLivenessActivityRecord_DsoExpire. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.4 -> 0.1.5, Splice.AmuletRules:426 -> Splice.AmuletRules:426:

```diff
          optValidatorFaucetCouponCids : Optional [ContractId ValidatorFaucetCoupon]
+         optValidatorLivenessActivityRecordCids : Optional [ContractId ValidatorLivenessActivityRecord]
        controller dso
...
  
+         -- expire validator liveness activity records and compute their unclaimed rewards
+         forA_ (fromOptional [] optValidatorLivenessActivityRecordCids) $ \validatorLivenessActivityRecordCid -> do
+           exercise validatorLivenessActivityRecordCid  ValidatorLivenessActivityRecord_DsoExpire with closedRoundCid
+ 
+         let numValidatorLivenessActivityRecords = optional 0.0 (intToDecimal . length) optValidatorLivenessActivityRecordCids
+ 
          -- expire app rewards and compute their unclaimed rewards
...
                validatorRewards * closedRound.issuancePerValidatorRewardCoupon +
+               (numValidatorFaucetCoupons + numValidatorLivenessActivityRecords) * getClosedMiningRoundIssuancePerValidatorFaucetCoupon closedRound +
-               numValidatorFaucetCoupons * getClosedMiningRoundIssuancePerValidatorFaucetCoupon closedRound +
                featuredAppRewards * closedRound.issuancePerFeaturedAppRewardCoupon +
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Ledger effects changed. Added: exercise self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.Archive, fetch self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:summarizeAndConsumeInputs, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.4 -> 0.1.5, Splice.AmuletRules:942:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.4 -> 0.1.5, Splice.AmuletRules:616:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.4 -> 0.1.5, Splice.AmuletRules:706 -> Splice.AmuletRules:713:

```diff
  
+     summarizeAndConsumeInput _round s (InputValidatorLivenessActivityRecord recordCid) = do
+       record <- fetchAndArchive forOwner recordCid
+       -- compute balance change
+       miningRound <- getIssuingMiningRound csum record.round
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         totalAppRewardAmount = s.totalAppRewardAmount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalValidatorFaucetAmount =
+           s.totalValidatorFaucetAmount + getIssuingMiningRoundIssuancePerValidatorFaucetCoupon miningRound
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+ 
      summarizeAndConsumeInput _round s (ExtTransferInput _dummyUnitField optInputValidatorFaucetCoupon) = do
```

## MEDIUM - effects-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_ReceiveFaucetCoupon``

Ledger effects changed. Added: none. Removed: create self:Splice.ValidatorLicense:ValidatorFaucetCoupon, create self:Splice.ValidatorLicense:ValidatorLicense, fetch self:Splice.Round:OpenMiningRound.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.4 -> 0.1.5, Splice.ValidatorLicense:61 -> Splice.ValidatorLicense:67:

```diff
        do
+         error $ "ValidatorLicense_ReceiveFaucetCoupon is deprecated. Please use ValidatorLicense_RecordValidatorLivenessActivity instead."
-         now <- getTime
-         openRound <- fetchReferenceData (ForDso with dso) openRoundCid
-         require "OpenRound is open" (openRound.opensAt <= now)
- 
-         newFaucetState <- case faucetState of
-           None -> return $ Some FaucetState with
-             firstReceivedFor = openRound.round
-             lastReceivedFor = openRound.round
-             numCouponsMissed = 0
- 
-           Some state -> do
-             require "Not yet received" (state.lastReceivedFor < openRound.round)
-             return $ Some FaucetState with
-               firstReceivedFor = state.firstReceivedFor
-               lastReceivedFor = openRound.round
-               numCouponsMissed = state.numCouponsMissed + (openRound.round.number - state.lastReceivedFor.number - 1)
- 
-         licenseCid <- create this with faucetState = newFaucetState; lastActiveAt = Some now
- 
-         couponCid <- create ValidatorFaucetCoupon with
-           dso
-           validator
-           round = openRound.round
- 
-         return ValidatorLicense_ReceiveFaucetCouponResult with ..
```

## MEDIUM - guard-removed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_ReceiveFaucetCoupon``

Fewer checks in the choice body (assertions or aborts): 4 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.4 -> 0.1.5, Splice.ValidatorLicense:61 -> Splice.ValidatorLicense:67:

```diff
        do
+         error $ "ValidatorLicense_ReceiveFaucetCoupon is deprecated. Please use ValidatorLicense_RecordValidatorLivenessActivity instead."
-         now <- getTime
-         openRound <- fetchReferenceData (ForDso with dso) openRoundCid
-         require "OpenRound is open" (openRound.opensAt <= now)
- 
-         newFaucetState <- case faucetState of
-           None -> return $ Some FaucetState with
-             firstReceivedFor = openRound.round
-             lastReceivedFor = openRound.round
-             numCouponsMissed = 0
- 
-           Some state -> do
-             require "Not yet received" (state.lastReceivedFor < openRound.round)
-             return $ Some FaucetState with
-               firstReceivedFor = state.firstReceivedFor
-               lastReceivedFor = openRound.round
-               numCouponsMissed = state.numCouponsMissed + (openRound.round.number - state.lastReceivedFor.number - 1)
- 
-         licenseCid <- create this with faucetState = newFaucetState; lastActiveAt = Some now
- 
-         couponCid <- create ValidatorFaucetCoupon with
-           dso
-           validator
-           round = openRound.round
- 
-         return ValidatorLicense_ReceiveFaucetCouponResult with ..
```

## LOW - template-added - `Splice.ValidatorLicense:ValidatorLivenessActivityRecord`

New template. Review it like a new package.

