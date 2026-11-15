# dar-vet: splice-dso-governance 0.1.17 -> 0.1.18

Old package ID `a0ae8e3d9897f163b56e7299af0cae86313f0b1ff10264d1fa73462c67c1f8a9`, new package ID `136484875714fcf24c24858e1d573ac756524eeb607c26999fca3311d6eea953`.

**3 findings:** 1 HIGH, 1 MEDIUM, 1 LOW.

## HIGH - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ClaimExpiredRewards``

Ledger effects changed. Added: create splice-amulet:Splice.Amulet:UnclaimedReward, exercise splice-amulet:Splice.Amulet:AppRewardCoupon.AppRewardCoupon_DsoExpire, exercise splice-amulet:Splice.Amulet:SvRewardCoupon.SvRewardCoupon_DsoExpire, exercise splice-amulet:Splice.Amulet:ValidatorRewardCoupon.ValidatorRewardCoupon_DsoExpire, exercise splice-amulet:Splice.ValidatorLicense:ValidatorFaucetCoupon.ValidatorFaucetCoupon_DsoExpire, exercise splice-amulet:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.ValidatorLivenessActivityRecord_DsoExpire, fetch splice-amulet:Splice.Round:ClosedMiningRound. Removed: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_ClaimExpiredRewards.

- The choice is non-consuming, so it can be repeated: each call now creates a contract.
- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.17 -> 0.1.18, Splice.DsoRules:1247 -> Splice.DsoRules:1247:

```diff
        do
+         -- TODO(#2025) Deprecate AmuletRules_ClaimExpiredRewards. We did not do that in the same change
+         -- as inlining the definition here so don't need to bump splice-amulet which also requires validators to upgrade.
+         let AmuletRules_ClaimExpiredRewards{..} = choiceArg
+         -- retrieve round
+         closedRound <- fetchReferenceData (ForDso with dso) closedRoundCid
+ 
+         -- expire validator rewards and compute their unclaimed rewards
+         expiredValidatorRewards <- forA validatorRewardCouponCids $ \validatorRewardCouponCid -> do
+           expireResult <- exercise validatorRewardCouponCid ValidatorRewardCoupon_DsoExpire with closedRoundCid
+           return expireResult.amount
+ 
+         let validatorRewards = sum expiredValidatorRewards
+ 
+         -- expire validator faucet coupons and compute their unclaimed rewards
+         forA_ (fromOptional [] optValidatorFaucetCouponCids) $ \validatorFaucetCouponCid -> do
+           exercise validatorFaucetCouponCid ValidatorFaucetCoupon_DsoExpire with closedRoundCid
+ 
+         let numValidatorFaucetCoupons = optional 0.0 (intToDecimal . length) optValidatorFaucetCouponCids
+ 
+         -- expire validator liveness activity records and compute their unclaimed rewards
+         forA_ (fromOptional [] optValidatorLivenessActivityRecordCids) $ \validatorLivenessActivityRecordCid -> do
+           exercise validatorLivenessActivityRecordCid  ValidatorLivenessActivityRecord_DsoExpire with closedRoundCid
+ 
+         let numValidatorLivenessActivityRecords = optional 0.0 (intToDecimal . length) optValidatorLivenessActivityRecordCids
+ 
+         -- expire app rewards and compute their unclaimed rewards
+         expiredAppRewards <- forA appCouponCids $ \appCouponCid -> do
+           expireResult <- exercise appCouponCid AppRewardCoupon_DsoExpire with closedRoundCid
+           return (expireResult.featured, expireResult.amount)
+ 
+         let featuredAppRewards = sum [ amount | (featured, amount) <- expiredAppRewards, featured ]
+         let unfeaturedAppRewards = sum [ amount | (featured, amount) <- expiredAppRewards, not featured ]
+ 
+         -- expire validator rewards and compute their unclaimed rewards
+         expiredSvRewards <- forA svRewardCouponCids $ \svRewardCouponCid -> do
+           expireResult <- exercise svRewardCouponCid SvRewardCoupon_DsoExpire with closedRoundCid
+           return expireResult.weight
+ 
+         let svRewardsWeight = sum expiredSvRewards
+ 
+         -- create unclaimed reward for the total
+         let amount =
+               validatorRewards * closedRound.issuancePerValidatorRewardCoupon +
+               (numValidatorFaucetCoupons + numValidatorLivenessActivityRecords) * getClosedMiningRoundIssuancePerValidatorFaucetCoupon closedRound +
+               featuredAppRewards * closedRound.issuancePerFeaturedAppRewardCoupon +
+               unfeaturedAppRewards * closedRound.issuancePerUnfeaturedAppRewardCoupon +
+               intToDecimal svRewardsWeight * closedRound.issuancePerSvRewardCoupon
+         unclaimedRewardCid <- if (amount <= 0.0)
+               then pure None
+               else Some <$> create UnclaimedReward with dso; amount
-         _ <- getAndValidateSvParty this sv
-         result <- exercise amuletRulesCid choiceArg
          return DsoRules_ClaimExpiredRewardsResult with
+           unclaimedReward = unclaimedRewardCid
-           unclaimedReward = result.unclaimedRewardCid
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_ClaimExpiredRewards``

Fewer checks in the choice body (assertions or aborts): 3 before, 2 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.17 -> 0.1.18, Splice.DsoRules:1247 -> Splice.DsoRules:1247:

```diff
        do
+         -- TODO(#2025) Deprecate AmuletRules_ClaimExpiredRewards. We did not do that in the same change
+         -- as inlining the definition here so don't need to bump splice-amulet which also requires validators to upgrade.
+         let AmuletRules_ClaimExpiredRewards{..} = choiceArg
+         -- retrieve round
+         closedRound <- fetchReferenceData (ForDso with dso) closedRoundCid
+ 
+         -- expire validator rewards and compute their unclaimed rewards
+         expiredValidatorRewards <- forA validatorRewardCouponCids $ \validatorRewardCouponCid -> do
+           expireResult <- exercise validatorRewardCouponCid ValidatorRewardCoupon_DsoExpire with closedRoundCid
+           return expireResult.amount
+ 
+         let validatorRewards = sum expiredValidatorRewards
+ 
+         -- expire validator faucet coupons and compute their unclaimed rewards
+         forA_ (fromOptional [] optValidatorFaucetCouponCids) $ \validatorFaucetCouponCid -> do
+           exercise validatorFaucetCouponCid ValidatorFaucetCoupon_DsoExpire with closedRoundCid
+ 
+         let numValidatorFaucetCoupons = optional 0.0 (intToDecimal . length) optValidatorFaucetCouponCids
+ 
+         -- expire validator liveness activity records and compute their unclaimed rewards
+         forA_ (fromOptional [] optValidatorLivenessActivityRecordCids) $ \validatorLivenessActivityRecordCid -> do
+           exercise validatorLivenessActivityRecordCid  ValidatorLivenessActivityRecord_DsoExpire with closedRoundCid
+ 
+         let numValidatorLivenessActivityRecords = optional 0.0 (intToDecimal . length) optValidatorLivenessActivityRecordCids
+ 
+         -- expire app rewards and compute their unclaimed rewards
+         expiredAppRewards <- forA appCouponCids $ \appCouponCid -> do
+           expireResult <- exercise appCouponCid AppRewardCoupon_DsoExpire with closedRoundCid
+           return (expireResult.featured, expireResult.amount)
+ 
+         let featuredAppRewards = sum [ amount | (featured, amount) <- expiredAppRewards, featured ]
+         let unfeaturedAppRewards = sum [ amount | (featured, amount) <- expiredAppRewards, not featured ]
+ 
+         -- expire validator rewards and compute their unclaimed rewards
+         expiredSvRewards <- forA svRewardCouponCids $ \svRewardCouponCid -> do
+           expireResult <- exercise svRewardCouponCid SvRewardCoupon_DsoExpire with closedRoundCid
+           return expireResult.weight
+ 
+         let svRewardsWeight = sum expiredSvRewards
+ 
+         -- create unclaimed reward for the total
+         let amount =
+               validatorRewards * closedRound.issuancePerValidatorRewardCoupon +
+               (numValidatorFaucetCoupons + numValidatorLivenessActivityRecords) * getClosedMiningRoundIssuancePerValidatorFaucetCoupon closedRound +
+               featuredAppRewards * closedRound.issuancePerFeaturedAppRewardCoupon +
+               unfeaturedAppRewards * closedRound.issuancePerUnfeaturedAppRewardCoupon +
+               intToDecimal svRewardsWeight * closedRound.issuancePerSvRewardCoupon
+         unclaimedRewardCid <- if (amount <= 0.0)
+               then pure None
+               else Some <$> create UnclaimedReward with dso; amount
-         _ <- getAndValidateSvParty this sv
-         result <- exercise amuletRulesCid choiceArg
          return DsoRules_ClaimExpiredRewardsResult with
+           unclaimedReward = unclaimedRewardCid
-           unclaimedReward = result.unclaimedRewardCid
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AllocateUnallocatedUnclaimedActivityRecord``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.17 -> 0.1.18, Splice.DsoRules:1452:

```text
(source text identical: the difference comes from code this function calls)
```

