# dar-vet: splice-amulet 0.1.24 -> 0.1.25

Old package ID `17b6f7c18efec9022dde39a19039783c68490a722e8a5088b22ee034678171a2`, new package ID `c5203dee918350373a993328e345872113bd5f8c8f0f51967014d813e583ae66`.

**31 findings:** 1 HIGH, 6 MEDIUM, 24 LOW.

## HIGH - new-choice - `Splice.Amulet:FeaturedAppRight, choice `FeaturedAppRight_UpdateConfig``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:FeaturedAppRight.

Version 0.1.25, Splice.Amulet:391:

```daml
    choice FeaturedAppRight_UpdateConfig : FeaturedAppRight_UpdateConfigResult
      with
        baseConfig : FeaturedAppConfig
        newConfig : FeaturedAppConfig
      controller dso
      do
        let configToSet = patch newConfig baseConfig (getFeaturedAppConfig this)
        featuredAppRightCid <- create (setFeaturedAppConfig configToSet this)
        return FeaturedAppRight_UpdateConfigResult with
          featuredAppRightCid
```

## MEDIUM - ensure-changed - `Splice.Amulet:FeaturedAppRight`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.24 -> 0.1.25, Splice.Amulet:313 -> Splice.Amulet:363:

```diff
+     ensure validFeaturedAppRight this
-     ensure 0.0 <= fromOptional defaultAppActivityWeight activityWeight
```

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, Splice.AmuletConfig:validRewardConfig, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletConfig:156:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletConfig:192 -> Splice.AmuletConfig:204:

```diff
    rewardCouponTimeToLive
+   appRewardCouponThreshold
+   extraFeaturedAssetActivityWeight
+   maxExtraFeaturedAssetActivityWeightPerAsset
+   dsoAppActivityWeight
+   ) =
-   appRewardCouponThreshold) =
      batchSize > 0 &&
      rewardCouponTimeToLive > hours 0 &&
+     appRewardCouponThreshold >= 0.0 &&
+     optional True (>= 0.0) extraFeaturedAssetActivityWeight &&
+     optional True (>= 0.0) maxExtraFeaturedAssetActivityWeightPerAsset &&
+     optional True (>= 0.0) dsoAppActivityWeight
-     appRewardCouponThreshold >= 0.0
```

## MEDIUM - ensure-changed - `Splice.ExternalPartyConfigState:ExternalPartyConfigState`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.24 -> 0.1.25, Splice.AmuletConfig:69 -> Splice.AmuletConfig:251:

```diff
+   deriving (Show, Eq, Serializable)
-   deriving (Eq, Show, Serializable)
```

## MEDIUM - ensure-changed - `Splice.Round:IssuingMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.24 -> 0.1.25, Splice.Round:83:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.Round:OpenMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.24 -> 0.1.25, Splice.Round:39:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.Round:SummarizingMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.24 -> 0.1.25, Splice.Round:60:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet.RewardAccountingV2:ProcessRewardsV2, choice `ProcessRewardsV2_ProcessBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet.CryptoHash:hashText, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.Amulet.CryptoHash:101:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:Amulet, choice `Amulet_ExpireV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.Amulet:725:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:FeaturedAppRight, choice `FeaturedAppRight_Update``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.Amulet:315 -> Splice.Amulet:366:

```diff
          reason : Text
+           -- NB: This reason field is not used by the UI and deprecated. The votes themselves already have a reason.
          newActivityWeight : Decimal
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmuletV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.Amulet:725:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AdvanceOpenMiningRounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:510:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BootstrapExternalPartyConfigState``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:bootstrapExternalPartyConfigState, Splice.AmuletRules:validateOpenMiningRoundTriple, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2390 -> Splice.AmuletRules:2471:

```diff
          transferConfig = transferConfig
+         trafficPrice = Some (effectiveTrafficPrice config validatedRounds.latestUsableRound)
+         supportedSynchronizerIds = Some (Set.toList config.decentralizedSynchronizer.requiredSynchronizers)
+         minTrafficTopupAmount = Some config.decentralizedSynchronizer.fees.minTopupAmount
          rewardCalculationVersion = (.mintingVersion) <$> validatedRounds.latestUsableRound.rewardConfig
          amuletSwitchOverTimes = config.amuletSwitchOverTimes
+         governanceLockConfig = config.governanceLockConfig
        configState1 = configState0 with
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1058:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:validateBuyMemberTrafficInputs, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1693:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1187:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1305 -> Splice.AmuletRules:1312:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist sender provider = do
- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
    -- check that the current mining round is open
...
        then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = provider) featuredAppRightCid
+         pure provider
-         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
-         pure tf.provider
        else pure None
...
    validatorRights <- forA (Map.toList context.validatorRights) $ \(user, rightCid) -> do
+       requireMatchingContract rightCid (ValidatorRight with dso; validator = sender; user)
-       requireMatchingContract rightCid (ValidatorRight with dso; validator = tf.sender; user)
        pure (user, rightCid)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1775 -> Splice.AmuletRules:1828:

```diff
+ validateBuyMemberTrafficInputs trafficConfig synchronizerId trafficAmount
+   | not (synchronizerId `elem` trafficConfig.supportedSynchronizerIds) =
- validateBuyMemberTrafficInputs configUsd synchronizerId trafficAmount
-   | not (synchronizerId `Set.member` configUsd.decentralizedSynchronizer.requiredSynchronizers) =
        Left $ mkUnknownSynchronizerFailure synchronizerId
+   | trafficAmount < trafficConfig.minTrafficTopupAmount =
+       Left $ mkInsufficientTopupAmountFailure trafficAmount trafficConfig.minTrafficTopupAmount
-   | trafficAmount < configUsd.decentralizedSynchronizer.fees.minTopupAmount =
-       Left $ mkInsufficientTopupAmountFailure trafficAmount configUsd.decentralizedSynchronizer.fees.minTopupAmount
    | otherwise = Right ()
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:950:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2335:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1693:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1187:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2410:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2278 -> Splice.AmuletRules:2363:

```diff
  splitAndBurn eventLogCid sender amount inputs context dso developmentFundManagerBlacklist usage = do
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist sender sender
+   splitAndBurn' eventLogCid csum sender amount inputs usage (IssueValidatorRewardCoupon csum.openRoundNumber)
- -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
-   let rewardsConfig = RewardsIssuanceConfig with
-         issueAppRewards = False
-         issueValidatorRewards = False
-   transferResult <- executeTransfer eventLogCid rewardsConfig context dso developmentFundManagerBlacklist Transfer with
-     sender
-     provider = sender
-     inputs
-     outputs =
-       [ TransferOutput with
-           receiver = sender
-           receiverFeeRatio = 0.0
-           amount
-           lock = None
-           meta = Some (reasonToMeta ("split for " <> usage) emptyMetadata)
-       ]
-     beneficiaries = None -- No beneficiaries for self-transfer
-   let [TransferResultAmulet amuletCid] = transferResult.createdAmulets
-   -- burn the split amulet containing the fee amount
-   amulet <- fetchAndArchive (ForOwner with dso; owner = sender) amuletCid
-   -- record event
-   logAmuletBurn dso eventLogCid sender amount usage (toInterfaceContractId amuletCid)
-   -- create validator activity rewards over the whole burn.
-   create ValidatorRewardCoupon with
-     dso
-     round = transferResult.round
-     user = sender
-     amount = amount + sum transferResult.summary.outputFees
-   let amuletAsOfRoundZero = amulet.amount.initialAmount + amulet.amount.ratePerRound.rate * intToDecimal amulet.amount.createdAt.number
-   -- adjust the balance changes to account for the archival. This primarily matters for the tx log parsers in scan and the wallet.
-   let result = transferResult with
-         meta = None -- the calling choice will take care of setting the metadata on its own result
-         summary.balanceChanges =
-           Map.insertWith
-             (+)
-             sender
-             (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
-             transferResult.summary.balanceChanges
-   let burnAmount = totalBurnFromSummary transferResult.summary + amount
-   let meta = simpleHoldingTxMeta TxKind_Burn (Some usage) (Some burnAmount) <>
-              fromOptional mempty transferResult.meta -- overwrite so we keep the reward input metadata
-   pure (result, meta)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1305 -> Splice.AmuletRules:1312:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist sender provider = do
- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
    -- check that the current mining round is open
...
        then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = provider) featuredAppRightCid
+         pure provider
-         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
-         pure tf.provider
        else pure None
...
    validatorRights <- forA (Map.toList context.validatorRights) $ \(user, rightCid) -> do
+       requireMatchingContract rightCid (ValidatorRight with dso; validator = sender; user)
-       requireMatchingContract rightCid (ValidatorRight with dso; validator = tf.sender; user)
        pure (user, rightCid)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2335:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1693:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1187:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2410:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2278 -> Splice.AmuletRules:2363:

```diff
  splitAndBurn eventLogCid sender amount inputs context dso developmentFundManagerBlacklist usage = do
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist sender sender
+   splitAndBurn' eventLogCid csum sender amount inputs usage (IssueValidatorRewardCoupon csum.openRoundNumber)
- -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
-   let rewardsConfig = RewardsIssuanceConfig with
-         issueAppRewards = False
-         issueValidatorRewards = False
-   transferResult <- executeTransfer eventLogCid rewardsConfig context dso developmentFundManagerBlacklist Transfer with
-     sender
-     provider = sender
-     inputs
-     outputs =
-       [ TransferOutput with
-           receiver = sender
-           receiverFeeRatio = 0.0
-           amount
-           lock = None
-           meta = Some (reasonToMeta ("split for " <> usage) emptyMetadata)
-       ]
-     beneficiaries = None -- No beneficiaries for self-transfer
-   let [TransferResultAmulet amuletCid] = transferResult.createdAmulets
-   -- burn the split amulet containing the fee amount
-   amulet <- fetchAndArchive (ForOwner with dso; owner = sender) amuletCid
-   -- record event
-   logAmuletBurn dso eventLogCid sender amount usage (toInterfaceContractId amuletCid)
-   -- create validator activity rewards over the whole burn.
-   create ValidatorRewardCoupon with
-     dso
-     round = transferResult.round
-     user = sender
-     amount = amount + sum transferResult.summary.outputFees
-   let amuletAsOfRoundZero = amulet.amount.initialAmount + amulet.amount.ratePerRound.rate * intToDecimal amulet.amount.createdAt.number
-   -- adjust the balance changes to account for the archival. This primarily matters for the tx log parsers in scan and the wallet.
-   let result = transferResult with
-         meta = None -- the calling choice will take care of setting the metadata on its own result
-         summary.balanceChanges =
-           Map.insertWith
-             (+)
-             sender
-             (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
-             transferResult.summary.balanceChanges
-   let burnAmount = totalBurnFromSummary transferResult.summary + amount
-   let meta = simpleHoldingTxMeta TxKind_Burn (Some usage) (Some burnAmount) <>
-              fromOptional mempty transferResult.meta -- overwrite so we keep the reward input metadata
-   pure (result, meta)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1305 -> Splice.AmuletRules:1312:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist sender provider = do
- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
    -- check that the current mining round is open
...
        then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = provider) featuredAppRightCid
+         pure provider
-         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
-         pure tf.provider
        else pure None
...
    validatorRights <- forA (Map.toList context.validatorRights) $ \(user, rightCid) -> do
+       requireMatchingContract rightCid (ValidatorRight with dso; validator = sender; user)
-       requireMatchingContract rightCid (ValidatorRight with dso; validator = tf.sender; user)
        pure (user, rightCid)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_FeatureApp``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:420 -> Splice.AmuletRules:417:

```diff
          activityWeight : Optional Decimal
+         appKind : Optional FeaturedAppKind
+         opsParties : Optional [Party]
        controller provider
...
          require "isDevNet flag is true" isDevNet
+         featuredAppRightCid <- create FeaturedAppRight with dso; provider; activityWeight; appKind; opsParties
-         featuredAppRightCid <- create FeaturedAppRight with dso; provider; activityWeight
          return AmuletRules_DevNet_FeatureAppResult with ..
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:333:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_Close``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:636:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_StartIssuing``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:588 -> Splice.AmuletRules:587:

```diff
          -- record unclaimed reward contract
+         let unclaimedAppRewards = fromOptional params.unclaimedAppRewards summary.optUnclaimedAppRewards
+         let totalUnclaimedRewards = params.unclaimedValidatorRewards + unclaimedAppRewards + params.unclaimedSvRewards
-         let totalUnclaimedRewards = params.unclaimedValidatorRewards + params.unclaimedAppRewards + params.unclaimedSvRewards
          when (totalUnclaimedRewards > 0.0) $
...
            issuancePerValidatorRewardCoupon = params.issuancePerValidatorRewardCoupon
+           issuancePerFeaturedAppRewardCoupon = fromOptional
+             params.issuancePerFeaturedAppRewardCoupon
+             summary.optIssuancePerFeaturedAppRewardCoupon
-           issuancePerFeaturedAppRewardCoupon = params.issuancePerFeaturedAppRewardCoupon
            issuancePerUnfeaturedAppRewardCoupon = params.issuancePerUnfeaturedAppRewardCoupon
...
            optIssuancePerValidatorFaucetCoupon = Some params.issuancePerValidatorFaucetCoupon
+           optIssuancePerFeaturedAssetRewardCoupon = summary.optIssuancePerFeaturedAssetRewardCoupon
  
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Mint``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:372:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:910:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1693:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1164 -> Splice.AmuletRules:1171:

```diff
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist t.sender t.provider
-   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist t
    executeTransfer' config csum dso t
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1187:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1305 -> Splice.AmuletRules:1312:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist sender provider = do
- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
    -- check that the current mining round is open
...
        then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = provider) featuredAppRightCid
+         pure provider
-         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
-         pure tf.provider
        else pure None
...
    validatorRights <- forA (Map.toList context.validatorRights) $ \(user, rightCid) -> do
+       requireMatchingContract rightCid (ValidatorRight with dso; validator = sender; user)
-       requireMatchingContract rightCid (ValidatorRight with dso; validator = tf.sender; user)
        pure (user, rightCid)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UpdateExternalPartyConfigStates``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:validateOpenMiningRoundTriple, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1058:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2335:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1693:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1187:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2410:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:2278 -> Splice.AmuletRules:2363:

```diff
  splitAndBurn eventLogCid sender amount inputs context dso developmentFundManagerBlacklist usage = do
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist sender sender
+   splitAndBurn' eventLogCid csum sender amount inputs usage (IssueValidatorRewardCoupon csum.openRoundNumber)
- -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
-   let rewardsConfig = RewardsIssuanceConfig with
-         issueAppRewards = False
-         issueValidatorRewards = False
-   transferResult <- executeTransfer eventLogCid rewardsConfig context dso developmentFundManagerBlacklist Transfer with
-     sender
-     provider = sender
-     inputs
-     outputs =
-       [ TransferOutput with
-           receiver = sender
-           receiverFeeRatio = 0.0
-           amount
-           lock = None
-           meta = Some (reasonToMeta ("split for " <> usage) emptyMetadata)
-       ]
-     beneficiaries = None -- No beneficiaries for self-transfer
-   let [TransferResultAmulet amuletCid] = transferResult.createdAmulets
-   -- burn the split amulet containing the fee amount
-   amulet <- fetchAndArchive (ForOwner with dso; owner = sender) amuletCid
-   -- record event
-   logAmuletBurn dso eventLogCid sender amount usage (toInterfaceContractId amuletCid)
-   -- create validator activity rewards over the whole burn.
-   create ValidatorRewardCoupon with
-     dso
-     round = transferResult.round
-     user = sender
-     amount = amount + sum transferResult.summary.outputFees
-   let amuletAsOfRoundZero = amulet.amount.initialAmount + amulet.amount.ratePerRound.rate * intToDecimal amulet.amount.createdAt.number
-   -- adjust the balance changes to account for the archival. This primarily matters for the tx log parsers in scan and the wallet.
-   let result = transferResult with
-         meta = None -- the calling choice will take care of setting the metadata on its own result
-         summary.balanceChanges =
-           Map.insertWith
-             (+)
-             sender
-             (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
-             transferResult.summary.balanceChanges
-   let burnAmount = totalBurnFromSummary transferResult.summary + amount
-   let meta = simpleHoldingTxMeta TxKind_Burn (Some usage) (Some burnAmount) <>
-              fromOptional mempty transferResult.meta -- overwrite so we keep the reward input metadata
-   pure (result, meta)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1305 -> Splice.AmuletRules:1312:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist sender provider = do
- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
    -- check that the current mining round is open
...
        then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = provider) featuredAppRightCid
+         pure provider
-         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
-         pure tf.provider
        else pure None
...
    validatorRights <- forA (Map.toList context.validatorRights) $ \(user, rightCid) -> do
+       requireMatchingContract rightCid (ValidatorRight with dso; validator = sender; user)
-       requireMatchingContract rightCid (ValidatorRight with dso; validator = tf.sender; user)
        pure (user, rightCid)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeExternalPartyTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:summarizeAndValidateExternalPartyContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1693:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1171 -> Splice.AmuletRules:1178:

```diff
  executeExternalPartyTransfer dso context t = do
+   csum <- summarizeAndValidateExternalPartyContext context dso t.provider
-   csum <- summarizeAndValidateExternalPartyContext context dso t
    let rewardsConfig = RewardsIssuanceConfig with
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1187:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.24 -> 0.1.25, Splice.AmuletRules:1344 -> Splice.AmuletRules:1351:

```diff
+ summarizeAndValidateExternalPartyContext context dso provider = do
- summarizeAndValidateExternalPartyContext context dso tf = do
    -- check the referenced ExternalPartyConfigState
...
        then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = provider) featuredAppRightCid
+         pure provider
-         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
-         pure tf.provider
        else pure None
```

## LOW - template-added - `Splice.GovernanceLock.Substitution:GovernanceLockSubstitution`

New template. Review it like a new package.

## LOW - template-added - `Splice.GovernanceLock.Transfer:GovernanceLockTransfer`

New template. Review it like a new package.

## LOW - template-added - `Splice.GovernanceLock:GovernanceLock`

New template. Review it like a new package.

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_RecordValidatorLivenessActivity``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.ValidatorLicense:89:

```text
(source text identical: the difference comes from code this function calls)
```

