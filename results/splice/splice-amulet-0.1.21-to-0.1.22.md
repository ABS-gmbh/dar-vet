# dar-vet: splice-amulet 0.1.21 -> 0.1.22

Old package ID `73e9ffdb6b0bc19a5f67372b118103926da11547ab9109eccae47e4e4cc35d6f`, new package ID `fb10433a48c24f30076a7aee03a3e314b7ab02fe9a22e2069e3af92d2b6ac88a`.

**11 findings:** 1 HIGH, 3 MEDIUM, 7 LOW.

## HIGH - new-choice - `Splice.Amulet:FeaturedAppRight, choice `FeaturedAppRight_Update``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:FeaturedAppRight.

Version 0.1.22, Splice.Amulet:293:

```daml
    choice FeaturedAppRight_Update : FeaturedAppRight_UpdateResult
      with
        reason : Text
        newActivityWeight : Decimal
      controller dso
      do
        featuredAppRightCid <- create this with
          -- use `None` for the default to maximize SCU compatibility
          activityWeight = if newActivityWeight == defaultAppActivityWeight then None else Some newActivityWeight
        return FeaturedAppRight_UpdateResult with
          featuredAppRightCid
```

## MEDIUM - ensure-changed - `Splice.Amulet:FeaturedAppRight`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.21 -> 0.1.22, Splice.Amulet:276 -> Splice.Amulet:291:

```diff
+     ensure 0.0 <= fromOptional defaultAppActivityWeight activityWeight
- template FeaturedAppRight with
```

## MEDIUM - observers-changed - `Splice.AmuletAllocation:AmuletAllocation`

Observer expression changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.21 -> 0.1.22, Splice.AmuletAllocation:53 -> Splice.AmuletAllocation:53:

```diff
+     observer TSU.executorsFromSettlementInfoV1 allocation.settlement
-     observer allocation.settlement.executor
```

## MEDIUM - guard-removed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

Fewer checks in the choice body (assertions or aborts): 24 before, 23 now.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeExternalPartyTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:summarizeAndValidateExternalPartyContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1630:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1128:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1137:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1294 -> Splice.AmuletRules:1299:

```diff
  
+   -- check the featured app right, if we use app markers
+   featuredAppProvider <-
+     if useFeaturedAppMarkers externalPartyConfigState.rewardCalculationVersion
+       then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
+         pure tf.provider
+       else pure None
-   -- check the featured app right, if present
-   featuredAppProvider <- forA context.featuredAppRight $ \featuredAppRightCid -> do
-     requireMatchingContract featuredAppRightCid (FeaturedAppRight with dso; provider = tf.provider)
-     pure tf.provider
  
```

## LOW - body-changed - `Splice.Amulet.RewardAccountingV2:ProcessRewardsV2, choice `ProcessRewardsV2_ProcessBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet.CryptoHash:hashText, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.21 -> 0.1.22, Splice.Amulet.CryptoHash:101:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1630:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1121:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1137:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:2222:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1260 -> Splice.AmuletRules:1261:

```diff
  
+   -- check the featured app right, if we use app markers
+   let rewardVersion = (.mintingVersion) <$> openRound.rewardConfig
+   featuredAppProvider <-
+     if useFeaturedAppMarkers rewardVersion
+       then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
+         pure tf.provider
+       else pure None
-   -- check the featured app right, if present
-   featuredAppProvider <- forA context.featuredAppRight $ \featuredAppRightCid -> do
-     requireMatchingContract featuredAppRightCid (FeaturedAppRight with dso; provider = tf.provider)
-     pure tf.provider
  
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1630:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1121:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1137:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:2273:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:2222:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1260 -> Splice.AmuletRules:1261:

```diff
  
+   -- check the featured app right, if we use app markers
+   let rewardVersion = (.mintingVersion) <$> openRound.rewardConfig
+   featuredAppProvider <-
+     if useFeaturedAppMarkers rewardVersion
+       then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
+         pure tf.provider
+       else pure None
-   -- check the featured app right, if present
-   featuredAppProvider <- forA context.featuredAppRight $ \featuredAppRightCid -> do
-     requireMatchingContract featuredAppRightCid (FeaturedAppRight with dso; provider = tf.provider)
-     pure tf.provider
  
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1630:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1121:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1137:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:2273:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:2222:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1260 -> Splice.AmuletRules:1261:

```diff
  
+   -- check the featured app right, if we use app markers
+   let rewardVersion = (.mintingVersion) <$> openRound.rewardConfig
+   featuredAppProvider <-
+     if useFeaturedAppMarkers rewardVersion
+       then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
+         pure tf.provider
+       else pure None
-   -- check the featured app right, if present
-   featuredAppProvider <- forA context.featuredAppRight $ \featuredAppRightCid -> do
-     requireMatchingContract featuredAppRightCid (FeaturedAppRight with dso; provider = tf.provider)
-     pure tf.provider
  
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_FeatureApp``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:393 -> Splice.AmuletRules:393:

```diff
          provider : Party
+         activityWeight : Optional Decimal
        controller provider
...
          require "isDevNet flag is true" isDevNet
+         featuredAppRightCid <- create FeaturedAppRight with dso; provider; activityWeight
-         featuredAppRightCid <- create FeaturedAppRight with dso; provider
          return AmuletRules_DevNet_FeatureAppResult with ..
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1630:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1121:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1137:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1260 -> Splice.AmuletRules:1261:

```diff
  
+   -- check the featured app right, if we use app markers
+   let rewardVersion = (.mintingVersion) <$> openRound.rewardConfig
+   featuredAppProvider <-
+     if useFeaturedAppMarkers rewardVersion
+       then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
+         pure tf.provider
+       else pure None
-   -- check the featured app right, if present
-   featuredAppProvider <- forA context.featuredAppRight $ \featuredAppRightCid -> do
-     requireMatchingContract featuredAppRightCid (FeaturedAppRight with dso; provider = tf.provider)
-     pure tf.provider
  
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1630:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1121:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1137:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:2273:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:2222:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.21 -> 0.1.22, Splice.AmuletRules:1260 -> Splice.AmuletRules:1261:

```diff
  
+   -- check the featured app right, if we use app markers
+   let rewardVersion = (.mintingVersion) <$> openRound.rewardConfig
+   featuredAppProvider <-
+     if useFeaturedAppMarkers rewardVersion
+       then forA context.featuredAppRight $ \featuredAppRightCid -> do
+         _ <- fetchReferenceData (ForOwner with dso; owner = tf.provider) featuredAppRightCid
+         pure tf.provider
+       else pure None
-   -- check the featured app right, if present
-   featuredAppProvider <- forA context.featuredAppRight $ \featuredAppRightCid -> do
-     requireMatchingContract featuredAppRightCid (FeaturedAppRight with dso; provider = tf.provider)
-     pure tf.provider
  
```

