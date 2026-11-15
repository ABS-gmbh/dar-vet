# dar-vet: splice-amulet 0.1.18 -> 0.1.19

Old package ID `a31be0483f3175647053f28965a4e6d97e3dbc433ea2338be303fae69bbcff6a`, new package ID `90987abecbcb1d004b063ddfe3b4b5d46cf3814ce89114a86c8cd75ff3cb8a4b`.

**30 findings:** 6 HIGH, 10 MEDIUM, 14 LOW.

## HIGH - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AdvanceOpenMiningRounds``

Ledger effects changed. Added: create self:Splice.Amulet.RewardAccountingV2:CalculateRewardsV2. Removed: none.

- The choice is non-consuming, so it can be repeated: each call now creates a contract.
- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:431 -> Splice.AmuletRules:455:

```diff
  
+         -- create the right state to start summarizing the round
-         -- archive and create the rounds
          newSummarizingRound <- create SummarizingMiningRound with
...
              tickDuration = roundToArchive.tickDuration
+             trafficPrice = roundToArchive.trafficPrice
+             rewardConfig = roundToArchive.rewardConfig
+ 
+         -- trigger off-ledger reward calculation if needed
+         forA_ roundToArchive.rewardConfig $ \rewardConfig -> do
+           when (useTrafficBasedAppRewards (Some rewardConfig.mintingVersion)) $ do
+             void $ create CalculateRewardsV2 with
+               dso
+               round = roundToArchive.round
+               roundClosedAt = now
+               rewardCouponTimeToLive = rewardConfig.rewardCouponTimeToLive
+               dryRun = False
+ 
+           when (useTrafficBasedAppRewards rewardConfig.dryRunVersion) $ do
+             void $ create CalculateRewardsV2 with
+               dso
+               round = roundToArchive.round
+               roundClosedAt = now
+               rewardCouponTimeToLive = rewardConfig.rewardCouponTimeToLive
+               dryRun = True
+ 
+         -- create the new open round
          newOpenRound <- do
...
                newOpenRoundIssuingFor = latestRound.issuingFor + latestRound.tickDuration
+               rewardConfig = configUsd.rewardConfig
            create OpenMiningRound with
...
                tickDuration
+               rewardConfig
+               trafficPrice =
+                 if isSome rewardConfig
+                   then Some configUsd.decentralizedSynchronizer.fees.extraTrafficPrice
+                   else None -- Keep OpenMiningRound downgradable to prior versions for as long as possible
          return AmuletRules_AdvanceOpenMiningRoundsResult with
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ArchiveDryRunRewardAccountingV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise self:Splice.Amulet.RewardAccountingV2:CalculateRewardsV2.Archive, exercise self:Splice.Amulet.RewardAccountingV2:ProcessRewardsV2.Archive, fetch self:Splice.Amulet.RewardAccountingV2:CalculateRewardsV2, fetch self:Splice.Amulet.RewardAccountingV2:ProcessRewardsV2.

Version 0.1.19, Splice.AmuletRules:732:

```daml
    nonconsuming choice AmuletRules_ArchiveDryRunRewardAccountingV2 : AmuletRules_ArchiveDryRunRewardAccountingV2Result
      with
        calculateRewardsCids : [ContractId CalculateRewardsV2]
        processRewardsCids : [ContractId ProcessRewardsV2]
      controller dso
      do
        forA_ calculateRewardsCids $ \calculateRewardsCid -> do
          state <- fetchAndArchive (ForDso with dso) calculateRewardsCid
          require "CalculateRewardsV2 is in dry-run state" (state.dryRun)
        forA_ processRewardsCids $ \processRewardsCid -> do
          state <- fetchAndArchive (ForDso with dso) processRewardsCid
          require "ProcessRewardsV2 is in dry-run state" (state.dryRun)
        return AmuletRules_ArchiveDryRunRewardAccountingV2Result {}
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ClaimExpiredRewardsV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:UnclaimedReward, exercise self:Splice.Amulet:RewardCouponV2.Archive, fetch self:Splice.Amulet:RewardCouponV2.

Version 0.1.19, Splice.AmuletRules:668:

```daml
    nonconsuming choice AmuletRules_ClaimExpiredRewardsV2 : AmuletRules_ClaimExpiredRewardsV2Result
      with
        rewardCouponCids : [ContractId RewardCouponV2]
        expiryObservers : [Party]
      observer expiryObservers
      controller dso
      do
        require "at least one coupon" (not (null rewardCouponCids))

        -- archive coupons
        coupons <- forA rewardCouponCids $ \cid -> do
          coupon <- fetchAndArchive (ForDso with dso) cid
          assertDeadlineExceeded "coupon.expiresAt" coupon.expiresAt
          return coupon
        let actualExpiryObservers = concat [ coupon.provider :: optionalToList coupon.beneficiary | coupon <- coupons, coupon.providerIsObserver ]
        require "expiryObservers match coupons" (sort expiryObservers == dedupSort actualExpiryObservers)

        -- create unclaimed reward for the total
        let amount = sum [ coupon.amount | coupon <- coupons ]
        when (amount > 0.0) $
          void $ create UnclaimedReward with dso; amount
        return AmuletRules_ClaimExpiredRewardsV2Result {}
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_StartProcessingRewardsV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet.RewardAccountingV2:ProcessRewardsV2, exercise self:Splice.Amulet.RewardAccountingV2:CalculateRewardsV2.Archive, fetch self:Splice.Amulet.RewardAccountingV2:CalculateRewardsV2.

Version 0.1.19, Splice.AmuletRules:691:

```daml
    nonconsuming choice AmuletRules_StartProcessingRewardsV2 : AmuletRules_StartProcessingRewardsV2Result
      with
        calculateRewardsCid : ContractId CalculateRewardsV2
        batchHash : CryptoHash.Hash
      controller dso
      do
        calculateRewards <- fetchAndArchive (ForDso with dso) calculateRewardsCid
        create ProcessRewardsV2 with
          dso
          round = calculateRewards.round
          roundClosedAt = calculateRewards.roundClosedAt
          dryRun = calculateRewards.dryRun
          rewardCouponTimeToLive = calculateRewards.rewardCouponTimeToLive
          batchHash

        return AmuletRules_StartProcessingRewardsV2Result {}
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UnhideRewardCouponsV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:RewardCouponV2, exercise self:Splice.Amulet:RewardCouponV2.Archive, fetch self:Splice.Amulet:RewardCouponV2.

Version 0.1.19, Splice.AmuletRules:709:

```daml
    nonconsuming choice AmuletRules_UnhideRewardCouponsV2 : AmuletRules_UnhideRewardCouponsV2Result
      with
        rewardCouponCids : [ContractId RewardCouponV2]
        providers : [Party]
      observer providers
      controller dso
      do
        require "at least one coupon" (not (null rewardCouponCids))

        -- unhide coupons
        actualProviders <- forA rewardCouponCids $ \cid -> do
          coupon <- fetchAndArchive (ForDso with dso) cid
          require "provider is no observer on the coupon" (not (coupon.providerIsObserver))
          assertWithinDeadline "coupon.expiresAt" coupon.expiresAt
          create coupon with providerIsObserver = True
          pure coupon.provider

        -- check specified providers match actual providers of the coupons
        require "providers match coupons" (dedupSort providers == dedupSort actualProviders)

        return AmuletRules_UnhideRewardCouponsV2Result {}
```

## HIGH - controller-changed - `Splice.Round:OpenMiningRound, choice `OpenMiningRound_Fetch``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.18 -> 0.1.19, Splice.Round:41:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletConfig:117:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Ledger effects changed. Added: exercise self:Splice.Amulet:RewardCouponV2.Archive, fetch self:Splice.Amulet:RewardCouponV2. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeSynchronizerFees, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:issueRewards, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1444 -> Splice.AmuletRules:1609:

```diff
  computeSynchronizerFees dso validator trafficAmount amuletRules context = do
+   contextMiningRound <- fetchPublicReferenceData (ForDso dso) context.openMiningRound (OpenMiningRound_Fetch validator)
    -- compute traffic cost in USD
    configUsd <- getValueAsOfLedgerTime amuletRules.configSchedule
+   let extraTrafficPrice =
+         fromOptional (configUsd.decentralizedSynchronizer.fees.extraTrafficPrice) contextMiningRound.trafficPrice
-   let extraTrafficPrice = configUsd.decentralizedSynchronizer.fees.extraTrafficPrice
    let trafficCostUsd = intToDecimal trafficAmount / 1e6 * extraTrafficPrice
    -- compute traffic cost in Amulet
-   contextMiningRound <- fetchPublicReferenceData (ForDso dso) context.openMiningRound (OpenMiningRound_Fetch validator)
    let trafficCostAmulet = trafficCostUsd / contextMiningRound.amuletPrice
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1554:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1092:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1108:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1367 -> Splice.AmuletRules:1530:

```diff
+ issueRewards config csum provider beneficiaries
+   | useFeaturedAppMarkers csum.rewardCalculationVersion = do
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+           when featured $
+             forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+               void $ create FeaturedAppActivityMarker
+                 with
+                   dso = csum.dso
+                   provider
+                   beneficiary = beneficiary
+                   weight = weight
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
+     | otherwise = pure ()
- issueRewards config csum provider beneficiaries = do
-     if config.issueAppRewards
-       then do
-         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-         validateAppRewardBeneficiaries beneficiaries'
-         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-         when featured $
-           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
-             void $ create FeaturedAppActivityMarker
-               with
-                 dso = csum.dso
-                 provider
-                 beneficiary = beneficiary
-                 weight = weight
-       else do
-         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:2088:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1115 -> Splice.AmuletRules:1261:

```diff
  
+     summarizeAndConsumeInput _round s (InputRewardCouponV2 couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "RewardCouponV2.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         -- Note: in the current implementation RewardCouponV2 is only used for app rewards.
+         -- This may change in the future. As part of such a change we'll adjust the attribution done here.
+         totalAppRewardAmount = s.totalAppRewardAmount + coupon.amount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1051 -> Splice.AmuletRules:1195:

```diff
      amuletPrice = openRound.amuletPrice
+     rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Ledger effects changed. Added: exercise self:Splice.Amulet:RewardCouponV2.Archive, fetch self:Splice.Amulet:RewardCouponV2. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:issueRewards, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:2080:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1554:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1092:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1108:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1367 -> Splice.AmuletRules:1530:

```diff
+ issueRewards config csum provider beneficiaries
+   | useFeaturedAppMarkers csum.rewardCalculationVersion = do
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+           when featured $
+             forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+               void $ create FeaturedAppActivityMarker
+                 with
+                   dso = csum.dso
+                   provider
+                   beneficiary = beneficiary
+                   weight = weight
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
+     | otherwise = pure ()
- issueRewards config csum provider beneficiaries = do
-     if config.issueAppRewards
-       then do
-         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-         validateAppRewardBeneficiaries beneficiaries'
-         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-         when featured $
-           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
-             void $ create FeaturedAppActivityMarker
-               with
-                 dso = csum.dso
-                 provider
-                 beneficiary = beneficiary
-                 weight = weight
-       else do
-         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:2088:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1115 -> Splice.AmuletRules:1261:

```diff
  
+     summarizeAndConsumeInput _round s (InputRewardCouponV2 couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "RewardCouponV2.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         -- Note: in the current implementation RewardCouponV2 is only used for app rewards.
+         -- This may change in the future. As part of such a change we'll adjust the attribution done here.
+         totalAppRewardAmount = s.totalAppRewardAmount + coupon.amount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1051 -> Splice.AmuletRules:1195:

```diff
      amuletPrice = openRound.amuletPrice
+     rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Ledger effects changed. Added: exercise self:Splice.Amulet:RewardCouponV2.Archive, fetch self:Splice.Amulet:RewardCouponV2. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:issueRewards, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:2080:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1554:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1092:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1108:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1367 -> Splice.AmuletRules:1530:

```diff
+ issueRewards config csum provider beneficiaries
+   | useFeaturedAppMarkers csum.rewardCalculationVersion = do
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+           when featured $
+             forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+               void $ create FeaturedAppActivityMarker
+                 with
+                   dso = csum.dso
+                   provider
+                   beneficiary = beneficiary
+                   weight = weight
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
+     | otherwise = pure ()
- issueRewards config csum provider beneficiaries = do
-     if config.issueAppRewards
-       then do
-         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-         validateAppRewardBeneficiaries beneficiaries'
-         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-         when featured $
-           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
-             void $ create FeaturedAppActivityMarker
-               with
-                 dso = csum.dso
-                 provider
-                 beneficiary = beneficiary
-                 weight = weight
-       else do
-         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:2088:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1115 -> Splice.AmuletRules:1261:

```diff
  
+     summarizeAndConsumeInput _round s (InputRewardCouponV2 couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "RewardCouponV2.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         -- Note: in the current implementation RewardCouponV2 is only used for app rewards.
+         -- This may change in the future. As part of such a change we'll adjust the attribution done here.
+         totalAppRewardAmount = s.totalAppRewardAmount + coupon.amount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1051 -> Splice.AmuletRules:1195:

```diff
      amuletPrice = openRound.amuletPrice
+     rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Ledger effects changed. Added: exercise self:Splice.Amulet:RewardCouponV2.Archive, fetch self:Splice.Amulet:RewardCouponV2. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:issueRewards, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1554:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1092:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1108:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1367 -> Splice.AmuletRules:1530:

```diff
+ issueRewards config csum provider beneficiaries
+   | useFeaturedAppMarkers csum.rewardCalculationVersion = do
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+           when featured $
+             forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+               void $ create FeaturedAppActivityMarker
+                 with
+                   dso = csum.dso
+                   provider
+                   beneficiary = beneficiary
+                   weight = weight
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
+     | otherwise = pure ()
- issueRewards config csum provider beneficiaries = do
-     if config.issueAppRewards
-       then do
-         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-         validateAppRewardBeneficiaries beneficiaries'
-         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-         when featured $
-           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
-             void $ create FeaturedAppActivityMarker
-               with
-                 dso = csum.dso
-                 provider
-                 beneficiary = beneficiary
-                 weight = weight
-       else do
-         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1115 -> Splice.AmuletRules:1261:

```diff
  
+     summarizeAndConsumeInput _round s (InputRewardCouponV2 couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "RewardCouponV2.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         -- Note: in the current implementation RewardCouponV2 is only used for app rewards.
+         -- This may change in the future. As part of such a change we'll adjust the attribution done here.
+         totalAppRewardAmount = s.totalAppRewardAmount + coupon.amount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1051 -> Splice.AmuletRules:1195:

```diff
      amuletPrice = openRound.amuletPrice
+     rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Ledger effects changed. Added: exercise self:Splice.Amulet:RewardCouponV2.Archive, fetch self:Splice.Amulet:RewardCouponV2. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:issueRewards, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:2080:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1554:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1092:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1108:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1367 -> Splice.AmuletRules:1530:

```diff
+ issueRewards config csum provider beneficiaries
+   | useFeaturedAppMarkers csum.rewardCalculationVersion = do
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+           when featured $
+             forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+               void $ create FeaturedAppActivityMarker
+                 with
+                   dso = csum.dso
+                   provider
+                   beneficiary = beneficiary
+                   weight = weight
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
+     | otherwise = pure ()
- issueRewards config csum provider beneficiaries = do
-     if config.issueAppRewards
-       then do
-         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-         validateAppRewardBeneficiaries beneficiaries'
-         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-         when featured $
-           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
-             void $ create FeaturedAppActivityMarker
-               with
-                 dso = csum.dso
-                 provider
-                 beneficiary = beneficiary
-                 weight = weight
-       else do
-         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:2088:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1115 -> Splice.AmuletRules:1261:

```diff
  
+     summarizeAndConsumeInput _round s (InputRewardCouponV2 couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "RewardCouponV2.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         -- Note: in the current implementation RewardCouponV2 is only used for app rewards.
+         -- This may change in the future. As part of such a change we'll adjust the attribution done here.
+         totalAppRewardAmount = s.totalAppRewardAmount + coupon.amount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1051 -> Splice.AmuletRules:1195:

```diff
      amuletPrice = openRound.amuletPrice
+     rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

Ledger effects changed. Added: exercise self:Splice.Amulet:RewardCouponV2.Archive, fetch self:Splice.Amulet:RewardCouponV2. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeExternalPartyTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:issueRewards, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateExternalPartyContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1554:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1099:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1108:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1367 -> Splice.AmuletRules:1530:

```diff
+ issueRewards config csum provider beneficiaries
+   | useFeaturedAppMarkers csum.rewardCalculationVersion = do
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+           when featured $
+             forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+               void $ create FeaturedAppActivityMarker
+                 with
+                   dso = csum.dso
+                   provider
+                   beneficiary = beneficiary
+                   weight = weight
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
+     | otherwise = pure ()
- issueRewards config csum provider beneficiaries = do
-     if config.issueAppRewards
-       then do
-         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-         validateAppRewardBeneficiaries beneficiaries'
-         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-         when featured $
-           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
-             void $ create FeaturedAppActivityMarker
-               with
-                 dso = csum.dso
-                 provider
-                 beneficiary = beneficiary
-                 weight = weight
-       else do
-         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1115 -> Splice.AmuletRules:1261:

```diff
  
+     summarizeAndConsumeInput _round s (InputRewardCouponV2 couponCid) = do
+       coupon <- fetchAndArchive forOwner couponCid
+       assertWithinDeadline "RewardCouponV2.expiresAt" coupon.expiresAt
+       return TransferInputsSummary with
+         totalAmuletAmount = s.totalAmuletAmount
+         -- Note: in the current implementation RewardCouponV2 is only used for app rewards.
+         -- This may change in the future. As part of such a change we'll adjust the attribution done here.
+         totalAppRewardAmount = s.totalAppRewardAmount + coupon.amount
+         totalValidatorRewardAmount = s.totalValidatorRewardAmount
+         totalUnclaimedActivityRecordAmount = s.totalUnclaimedActivityRecordAmount
+         totalValidatorFaucetAmount = s.totalValidatorFaucetAmount
+         totalSvRewardAmount = s.totalSvRewardAmount
+         totalHoldingFees = s.totalHoldingFees
+         amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero
+         changeToHoldingFeesRate = s.changeToHoldingFeesRate
+         totalDevelopmentFundAmount = s.totalDevelopmentFundAmount
+ 
      summarizeAndConsumeValidatorFaucetInput s couponCid = do
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1082 -> Splice.AmuletRules:1227:

```diff
      openRoundNumber = externalPartyConfigState.holdingFeesOpenRoundNumber
-     amuletPrice = externalPartyConfigState.amuletPrice
      -- no minting for long lived transfers
...
      validatorRights = Map.empty
+     amuletPrice = externalPartyConfigState.amuletPrice
+     rewardCalculationVersion = externalPartyConfigState.rewardCalculationVersion
```

## MEDIUM - ensure-changed - `Splice.ExternalPartyConfigState:ExternalPartyConfigState`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.18 -> 0.1.19, Splice.ExternalPartyConfigState:17 -> Splice.AmuletConfig:60:

```diff
+   deriving (Eq, Show)
- template ExternalPartyConfigState
```

## MEDIUM - ensure-changed - `Splice.Round:OpenMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.18 -> 0.1.19, Splice.Round:39:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.Round:SummarizingMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.18 -> 0.1.19, Splice.Round:60:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - template-added - `Splice.Amulet.RewardAccountingV2:CalculateRewardsV2`

New template. Review it like a new package.

## LOW - template-added - `Splice.Amulet.RewardAccountingV2:ProcessRewardsV2`

New template. Review it like a new package.

## LOW - body-changed - `Splice.Amulet:Amulet, choice `Amulet_ExpireV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.Amulet:583:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmuletV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.Amulet:583:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - template-added - `Splice.Amulet:RewardCouponV2`

New template. Review it like a new package.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BootstrapExternalPartyConfigState``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:bootstrapExternalPartyConfigState, Splice.AmuletRules:validateOpenMiningRoundTriple, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:1986 -> Splice.AmuletRules:2153:

```diff
          transferConfig = transferConfig
+         rewardCalculationVersion = (.mintingVersion) <$> validatedRounds.latestUsableRound.rewardConfig
        configState1 = configState0 with
```

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:980:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Bootstrap_Rounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:364 -> Splice.AmuletRules:380:

```diff
          let tickDuration = configUsd.tickDuration
+         let rewardConfig = configUsd.rewardConfig
+         let trafficPrice =
+               if isSome rewardConfig
+                 then Some configUsd.decentralizedSynchronizer.fees.extraTrafficPrice
+                 else None -- Keep OpenMiningRound downgradable to prior versions for as long as possible
          let nr0 = fromOptional 0 initialRound
...
            dso; round = Round nr0; amuletPrice; opensAt = opensAt0; targetClosesAt = targetClosesAt0; issuingFor = issuingFor0; transferConfigUsd ; tickDuration ; issuanceConfig = issuanceConfig0
+           trafficPrice; rewardConfig
          newestOpen <- create OpenMiningRound with
            dso; round = Round nr1; amuletPrice; opensAt = opensAt1; targetClosesAt = targetClosesAt1; issuingFor = issuingFor1; transferConfigUsd ; tickDuration ; issuanceConfig = issuanceConfig1
+           trafficPrice; rewardConfig
          last <- create OpenMiningRound with
            dso; round = Round nr2; amuletPrice; opensAt = opensAt2; targetClosesAt = targetClosesAt2; issuingFor = issuingFor2; transferConfigUsd ; tickDuration ; issuanceConfig = issuanceConfig2
+           trafficPrice; rewardConfig
  
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:746 -> Splice.AmuletRules:877:

```diff
        controller dso
+       do
+         now <- getTime
+         markers <- mapA (fetchAndArchive (ForDso dso)) markerCids
+         let groupedMarkers = Map.fromListWithR (+) (map (\m -> ((m.provider, m.beneficiary), m.weight)) markers)
+         round <- fetchReferenceData (ForDso dso) openMiningRoundCid
+         require ("mining round is open: " <> show round) (round.opensAt <= now)
+         let configUsd = getValueAsOf now configSchedule
+         if useTrafficBasedAppRewards ((.mintingVersion) <$> round.rewardConfig)
+           then
+             -- Markers may still be created for rounds that use traffic-based app rewards
+             -- ==> just archive the markers.
+             pure AmuletRules_ConvertFeaturedAppActivityMarkersResult with appRewardCouponCids = []
+           else do
+             -- If the amount is not set or is <= 0 we just archive the marker contracts.
+             markerCids <- forA (optionalToList configUsd.featuredAppActivityMarkerAmount) $ \amountUsd -> do
+               let amountAmulet = amountUsd / round.amuletPrice
+               if amountAmulet > 0.0
+                 then
+                   forA (Map.toList groupedMarkers) $ \((provider, beneficiary), weight) ->
+                     create AppRewardCoupon with
+                       dso
+                       provider
+                       beneficiary = Some beneficiary
+                       featured = True
+                       round = round.round
+                       amount = amountAmulet * weight
+                 else pure []
+             pure AmuletRules_ConvertFeaturedAppActivityMarkersResult with appRewardCouponCids = concat markerCids
-       do now <- getTime
-          markers <- mapA (fetchAndArchive (ForDso dso)) markerCids
-          let groupedMarkers = Map.fromListWithR (+) (map (\m -> ((m.provider, m.beneficiary), m.weight)) markers)
-          round <- fetchReferenceData (ForDso dso) openMiningRoundCid
-          require ("mining round is open: " <> show round) (round.opensAt <= now)
-          let configUsd = getValueAsOf now configSchedule
-          -- If the amount is not set or is <= 0 we just archive the marker contracts.
-          markerCids <- forA (optionalToList configUsd.featuredAppActivityMarkerAmount) $ \amountUsd -> do
-            let amountAmulet = amountUsd / round.amuletPrice
-            if amountAmulet > 0.0
-              then
-                forA (Map.toList groupedMarkers) $ \((provider, beneficiary), weight) ->
-                  create AppRewardCoupon with
-                    dso
-                    provider
-                    beneficiary = Some beneficiary
-                    featured = True
-                    round = round.round
-                    amount = amountAmulet * weight
-              else pure []
-          pure AmuletRules_ConvertFeaturedAppActivityMarkersResult with appRewardCouponCids = concat markerCids
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:291:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_StartIssuing``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:532:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Mint``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:328:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:837:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UpdateExternalPartyConfigStates``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:validateOpenMiningRoundTriple, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.AmuletRules:980:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_RecordValidatorLivenessActivity``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.ValidatorLicense:74:

```text
(source text identical: the difference comes from code this function calls)
```

