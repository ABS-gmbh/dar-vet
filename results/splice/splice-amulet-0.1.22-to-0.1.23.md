# dar-vet: splice-amulet 0.1.22 -> 0.1.23

Old package ID `fb10433a48c24f30076a7aee03a3e314b7ab02fe9a22e2069e3af92d2b6ac88a`, new package ID `8fe7573f5535dc5b910a1f24d7d980da0e10ab38f8d29cb397146119bbfb7b3a`.

**17 findings:** 0 HIGH, 3 MEDIUM, 14 LOW.

## MEDIUM - ensure-changed - `Splice.Amulet:DevelopmentFundCoupon`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.22 -> 0.1.23, Splice.Amulet:552 -> Splice.Amulet:575:

```diff
+     ensure amount > 0.0 && optional True (< expiresAt) mintAfter
-     ensure amount > 0.0
```

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, Splice.AmuletConfig:validTransferConfig, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.AmuletConfig:134 -> Splice.AmuletConfig:144:

```diff
      , transferPreapprovalFee
+     , featuredAppActivityMarkerAmount
+     , rewardConfig
+     , minDevelopmentFundMintingDelay
+     }
-     , featuredAppActivityMarkerAmount}
    =
...
      optional True (>= 0.0) featuredAppActivityMarkerAmount &&
+     featuredAppActivityMarkerAmount == Some transferConfig.extraFeaturedAppRewardAmount &&
+     optional True validRewardConfig rewardConfig &&
+     optional True (>= days 0) minDevelopmentFundMintingDelay
-     featuredAppActivityMarkerAmount == Some transferConfig.extraFeaturedAppRewardAmount
```

Version 0.1.22 -> 0.1.23, Splice.AmuletConfig:154 -> Splice.AmuletConfig:169:

```diff
      lockHolderFee == FixedFee 0.0 &&
-     lockHolderFee == FixedFee 0.0 &&
      transferFee.initialRate == 0.0 &&
```

## MEDIUM - ensure-changed - `Splice.ExternalPartyConfigState:ExternalPartyConfigState`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.22 -> 0.1.23, Splice.AmuletConfig:68 -> Splice.AmuletConfig:69:

```diff
+   deriving (Eq, Show, Serializable)
-   deriving (Eq, Show)
```

## LOW - body-changed - `Splice.Amulet.RewardAccountingV2:ProcessRewardsV2, choice `ProcessRewardsV2_ProcessBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet.CryptoHash:hashText, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.Amulet.CryptoHash:101:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:Amulet, choice `Amulet_ExpireV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.Amulet:648:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmuletV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.Amulet:648:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AllocateDevelopmentFundCoupon``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:815 -> Splice.AmuletRules:842:

```diff
          fundManager : Party
+         mintAfter : Optional Time
        controller fundManager
...
  
+         require ("fund manager is not blacklisted: " <> show fundManager) $
+           fundManager `notElem` getDevelopmentFundManagerBlacklist configUsd
+ 
+         -- sanity check that mintAfter is in the future
+         forA_ mintAfter $ assertWithinDeadline "mintAfter"
+ 
+         whenSome configUsd.minDevelopmentFundMintingDelay $ \minDelay ->
+           case mintAfter of
+             None -> abort "DevelopmentFundCoupon cannot be allocated without a mintAfter time, as a minimum development fund minting delay is configured"
+             Some mintAfterTime ->
+               assertWithinDeadline "mintAfter - minDevelopmentFundMintingDelay"
+                   (mintAfterTime `addRelTime` negate minDelay)
+ 
          -- Verify the amount and create a leftover UnclaimedDevelopmentFundCoupon if needed
...
          developmentFundCouponCid <- create DevelopmentFundCoupon with
+           dso; beneficiary; fundManager; amount; expiresAt; reason; mintAfter
-           dso; beneficiary; fundManager; amount; expiresAt; reason
  
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BootstrapExternalPartyConfigState``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:bootstrapExternalPartyConfigState, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:2333 -> Splice.AmuletRules:2390:

```diff
          rewardCalculationVersion = (.mintingVersion) <$> validatedRounds.latestUsableRound.rewardConfig
+         amuletSwitchOverTimes = config.amuletSwitchOverTimes
        configState1 = configState0 with
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1121 -> Splice.AmuletRules:1164:

```diff
+ executeTransfer eventLogCid config context dso developmentFundManagerBlacklist t = do
- executeTransfer eventLogCid config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist t
-   csum <- summarizeAndValidateContext eventLogCid context dso t
    executeTransfer' config csum dso t
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:2222 -> Splice.AmuletRules:2278:

```diff
+ splitAndBurn eventLogCid sender amount inputs context dso developmentFundManagerBlacklist usage = do
- splitAndBurn eventLogCid sender amount inputs context dso usage = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
          issueValidatorRewards = False
+   transferResult <- executeTransfer eventLogCid rewardsConfig context dso developmentFundManagerBlacklist Transfer with
-   transferResult <- executeTransfer eventLogCid rewardsConfig context dso Transfer with
      sender
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1337 -> Splice.AmuletRules:1386:

```diff
        coupon <- fetchAndArchive forOwner couponCid
+       developmentFundManagerBlacklist <- case csum.developmentFundManagerBlacklist of
+         None -> abort "DevelopmentFundCoupons.developmentFundManagerBlacklist is not defined"
+         Some blacklist -> pure blacklist
+       require ("DevelopmentFundCoupon.fundManager is not blacklisted: " <> show coupon.fundManager)
+         (coupon.fundManager `notElem` developmentFundManagerBlacklist)
        assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       whenSome coupon.mintAfter $ \m -> do
+         assertDeadlineExceeded "DevelopmentFundCoupon.mintAfter" m
        return TransferInputsSummary with
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1261 -> Splice.AmuletRules:1305:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
- summarizeAndValidateContext eventLogCid context dso tf = do
    -- check that the current mining round is open
...
      eventLogCid = Some eventLogCid
+     developmentFundManagerBlacklist = Some developmentFundManagerBlacklist
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1121 -> Splice.AmuletRules:1164:

```diff
+ executeTransfer eventLogCid config context dso developmentFundManagerBlacklist t = do
- executeTransfer eventLogCid config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist t
-   csum <- summarizeAndValidateContext eventLogCid context dso t
    executeTransfer' config csum dso t
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:2273 -> Splice.AmuletRules:2329:

```diff
        (amuletPaid, _) <- computeTransferPreapprovalFee paidExtension configUsd context.context dso
+       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso
+         (getDevelopmentFundManagerBlacklist configUsd) reason
-       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso reason
        pure (amuletPaid, transferResult, meta)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:2222 -> Splice.AmuletRules:2278:

```diff
+ splitAndBurn eventLogCid sender amount inputs context dso developmentFundManagerBlacklist usage = do
- splitAndBurn eventLogCid sender amount inputs context dso usage = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
          issueValidatorRewards = False
+   transferResult <- executeTransfer eventLogCid rewardsConfig context dso developmentFundManagerBlacklist Transfer with
-   transferResult <- executeTransfer eventLogCid rewardsConfig context dso Transfer with
      sender
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1337 -> Splice.AmuletRules:1386:

```diff
        coupon <- fetchAndArchive forOwner couponCid
+       developmentFundManagerBlacklist <- case csum.developmentFundManagerBlacklist of
+         None -> abort "DevelopmentFundCoupons.developmentFundManagerBlacklist is not defined"
+         Some blacklist -> pure blacklist
+       require ("DevelopmentFundCoupon.fundManager is not blacklisted: " <> show coupon.fundManager)
+         (coupon.fundManager `notElem` developmentFundManagerBlacklist)
        assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       whenSome coupon.mintAfter $ \m -> do
+         assertDeadlineExceeded "DevelopmentFundCoupon.mintAfter" m
        return TransferInputsSummary with
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1261 -> Splice.AmuletRules:1305:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
- summarizeAndValidateContext eventLogCid context dso tf = do
    -- check that the current mining round is open
...
      eventLogCid = Some eventLogCid
+     developmentFundManagerBlacklist = Some developmentFundManagerBlacklist
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1121 -> Splice.AmuletRules:1164:

```diff
+ executeTransfer eventLogCid config context dso developmentFundManagerBlacklist t = do
- executeTransfer eventLogCid config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist t
-   csum <- summarizeAndValidateContext eventLogCid context dso t
    executeTransfer' config csum dso t
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:2273 -> Splice.AmuletRules:2329:

```diff
        (amuletPaid, _) <- computeTransferPreapprovalFee paidExtension configUsd context.context dso
+       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso
+         (getDevelopmentFundManagerBlacklist configUsd) reason
-       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso reason
        pure (amuletPaid, transferResult, meta)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:2222 -> Splice.AmuletRules:2278:

```diff
+ splitAndBurn eventLogCid sender amount inputs context dso developmentFundManagerBlacklist usage = do
- splitAndBurn eventLogCid sender amount inputs context dso usage = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
          issueValidatorRewards = False
+   transferResult <- executeTransfer eventLogCid rewardsConfig context dso developmentFundManagerBlacklist Transfer with
-   transferResult <- executeTransfer eventLogCid rewardsConfig context dso Transfer with
      sender
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1337 -> Splice.AmuletRules:1386:

```diff
        coupon <- fetchAndArchive forOwner couponCid
+       developmentFundManagerBlacklist <- case csum.developmentFundManagerBlacklist of
+         None -> abort "DevelopmentFundCoupons.developmentFundManagerBlacklist is not defined"
+         Some blacklist -> pure blacklist
+       require ("DevelopmentFundCoupon.fundManager is not blacklisted: " <> show coupon.fundManager)
+         (coupon.fundManager `notElem` developmentFundManagerBlacklist)
        assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       whenSome coupon.mintAfter $ \m -> do
+         assertDeadlineExceeded "DevelopmentFundCoupon.mintAfter" m
        return TransferInputsSummary with
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1261 -> Splice.AmuletRules:1305:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
- summarizeAndValidateContext eventLogCid context dso tf = do
    -- check that the current mining round is open
...
      eventLogCid = Some eventLogCid
+     developmentFundManagerBlacklist = Some developmentFundManagerBlacklist
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:336:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:907:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1121 -> Splice.AmuletRules:1164:

```diff
+ executeTransfer eventLogCid config context dso developmentFundManagerBlacklist t = do
- executeTransfer eventLogCid config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist t
-   csum <- summarizeAndValidateContext eventLogCid context dso t
    executeTransfer' config csum dso t
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1337 -> Splice.AmuletRules:1386:

```diff
        coupon <- fetchAndArchive forOwner couponCid
+       developmentFundManagerBlacklist <- case csum.developmentFundManagerBlacklist of
+         None -> abort "DevelopmentFundCoupons.developmentFundManagerBlacklist is not defined"
+         Some blacklist -> pure blacklist
+       require ("DevelopmentFundCoupon.fundManager is not blacklisted: " <> show coupon.fundManager)
+         (coupon.fundManager `notElem` developmentFundManagerBlacklist)
        assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       whenSome coupon.mintAfter $ \m -> do
+         assertDeadlineExceeded "DevelopmentFundCoupon.mintAfter" m
        return TransferInputsSummary with
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1261 -> Splice.AmuletRules:1305:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
- summarizeAndValidateContext eventLogCid context dso tf = do
    -- check that the current mining round is open
...
      eventLogCid = Some eventLogCid
+     developmentFundManagerBlacklist = Some developmentFundManagerBlacklist
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UpdateExternalPartyConfigStates``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:968 -> Splice.AmuletRules:1009:

```diff
            rewardCalculationVersion = (.mintingVersion) <$> validatedRounds.latestUsableRound.rewardConfig
+           amuletSwitchOverTimes = config.amuletSwitchOverTimes
          pure AmuletRules_UpdateExternalPartyConfigStatesResult with ..
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1121 -> Splice.AmuletRules:1164:

```diff
+ executeTransfer eventLogCid config context dso developmentFundManagerBlacklist t = do
- executeTransfer eventLogCid config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist t
-   csum <- summarizeAndValidateContext eventLogCid context dso t
    executeTransfer' config csum dso t
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:2273 -> Splice.AmuletRules:2329:

```diff
        (amuletPaid, _) <- computeTransferPreapprovalFee paidExtension configUsd context.context dso
+       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso
+         (getDevelopmentFundManagerBlacklist configUsd) reason
-       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso reason
        pure (amuletPaid, transferResult, meta)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:2222 -> Splice.AmuletRules:2278:

```diff
+ splitAndBurn eventLogCid sender amount inputs context dso developmentFundManagerBlacklist usage = do
- splitAndBurn eventLogCid sender amount inputs context dso usage = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
          issueValidatorRewards = False
+   transferResult <- executeTransfer eventLogCid rewardsConfig context dso developmentFundManagerBlacklist Transfer with
-   transferResult <- executeTransfer eventLogCid rewardsConfig context dso Transfer with
      sender
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1337 -> Splice.AmuletRules:1386:

```diff
        coupon <- fetchAndArchive forOwner couponCid
+       developmentFundManagerBlacklist <- case csum.developmentFundManagerBlacklist of
+         None -> abort "DevelopmentFundCoupons.developmentFundManagerBlacklist is not defined"
+         Some blacklist -> pure blacklist
+       require ("DevelopmentFundCoupon.fundManager is not blacklisted: " <> show coupon.fundManager)
+         (coupon.fundManager `notElem` developmentFundManagerBlacklist)
        assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       whenSome coupon.mintAfter $ \m -> do
+         assertDeadlineExceeded "DevelopmentFundCoupon.mintAfter" m
        return TransferInputsSummary with
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1261 -> Splice.AmuletRules:1305:

```diff
+ summarizeAndValidateContext eventLogCid context dso developmentFundManagerBlacklist tf = do
- summarizeAndValidateContext eventLogCid context dso tf = do
    -- check that the current mining round is open
...
      eventLogCid = Some eventLogCid
+     developmentFundManagerBlacklist = Some developmentFundManagerBlacklist
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeExternalPartyTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateExternalPartyContext, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1171:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1337 -> Splice.AmuletRules:1386:

```diff
        coupon <- fetchAndArchive forOwner couponCid
+       developmentFundManagerBlacklist <- case csum.developmentFundManagerBlacklist of
+         None -> abort "DevelopmentFundCoupons.developmentFundManagerBlacklist is not defined"
+         Some blacklist -> pure blacklist
+       require ("DevelopmentFundCoupon.fundManager is not blacklisted: " <> show coupon.fundManager)
+         (coupon.fundManager `notElem` developmentFundManagerBlacklist)
        assertWithinDeadline "DevelopmentFundCoupon.expiresAt" coupon.expiresAt
+       whenSome coupon.mintAfter $ \m -> do
+         assertDeadlineExceeded "DevelopmentFundCoupon.mintAfter" m
        return TransferInputsSummary with
```

Version 0.1.22 -> 0.1.23, Splice.AmuletRules:1299 -> Splice.AmuletRules:1344:

```diff
      eventLogCid = Some (toInterfaceContractId context.externalPartyConfigState)
+     -- External party context cannot be used with `DevelopmentFundCoupon` as of now. But to ensure
+     -- the blacklist is still enforced in the future once we add support, we set it to `None` here
+     -- to show that the list is unknown in the context.
+     developmentFundManagerBlacklist = None
```

