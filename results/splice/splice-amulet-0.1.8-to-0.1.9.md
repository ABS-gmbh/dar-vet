# dar-vet: splice-amulet 0.1.8 -> 0.1.9

Old package ID `511bd3bf23fab4e5171edb22dceabe3061f7faf78a44f8af44f3b87f977c61f6`, new package ID `a5b055492fb8f08b2e7bc0fc94da6da50c39c2e1d7f24cd5ea8db12fc87c1332`.

**47 findings:** 7 HIGH, 9 MEDIUM, 31 LOW.

## HIGH - new-interface-instance - `Splice.Amulet:Amulet`

Template now implements interface splice-api-token-holding-v1:Splice.Api.Token.HoldingV1:Holding. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.Amulet:FeaturedAppRight`

Template now implements interface splice-api-featured-app-v1:Splice.Api.FeaturedAppRightV1:FeaturedAppRight. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.Amulet:LockedAmulet`

Template now implements interface splice-api-token-holding-v1:Splice.Api.Token.HoldingV1:Holding. Its choices can be exercised on contracts created under the old version.

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:AppRewardCoupon, exercise self:Splice.Amulet:FeaturedAppActivityMarker.Archive, fetch self:Splice.Amulet:FeaturedAppActivityMarker, fetch self:Splice.Round:OpenMiningRound.

Version 0.1.9, Splice.AmuletRules:603:

```daml
    nonconsuming choice AmuletRules_ConvertFeaturedAppActivityMarkers : AmuletRules_ConvertFeaturedAppActivityMarkersResult
      with
        markerCids : [ContractId FeaturedAppActivityMarker]
        openMiningRoundCid : ContractId OpenMiningRound
      controller dso
      do now <- getTime
         markers <- mapA (fetchAndArchive (ForDso dso)) markerCids
         let groupedMarkers = Map.fromListWithR (+) (map (\m -> ((m.provider, m.beneficiary), m.weight)) markers)
         round <- fetchReferenceData (ForDso dso) openMiningRoundCid
         require ("mining round is open: " <> show round) (round.opensAt <= now)
         let configUsd = getValueAsOf now configSchedule
         -- If the amount is not set or is <= 0 we just archive the marker contracts.
         markerCids <- forA (optionalToList configUsd.featuredAppActivityMarkerAmount) $ \amountUsd -> do
           let amountAmulet = amountUsd / round.amuletPrice
           if amountAmulet > 0.0
             then
               forA (Map.toList groupedMarkers) $ \((provider, beneficiary), weight) ->
                 create AppRewardCoupon with
                   dso
                   provider
                   beneficiary = Some beneficiary
                   featured = True
                   round = round.round
                   amount = amountAmulet * weight
             else pure []
         pure AmuletRules_ConvertFeaturedAppActivityMarkersResult with appRewardCouponCids = concat markerCids
```

## HIGH - new-interface-instance - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules`

Template now implements interface splice-api-token-allocation-instruction-v1:Splice.Api.Token.AllocationInstructionV1:AllocationFactory. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules`

Template now implements interface splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferFactory. Its choices can be exercised on contracts created under the old version.

## HIGH - controller-changed - `Splice.ExternalPartyAmuletRules:TransferCommand, choice `TransferCommand_Expire``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.8 -> 0.1.9, Splice.ExternalPartyAmuletRules:165 -> Splice.ExternalPartyAmuletRules:202:

```diff
        do require "Controller is a stakeholder" (p `elem` stakeholder this)
+          assertDeadlineExceeded "TransferCommand.expiresAt" expiresAt
-          now <- getTime
-          require "TransferCommand is expired" (expiresAt <= now)
           pure TransferCommand_ExpireResult with
```

## MEDIUM - ensure-changed - `Splice.Amulet:AppRewardCoupon`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.8 -> 0.1.9, Splice.Amulet:290:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - observers-changed - `Splice.Amulet:AppRewardCoupon`

Observer expression changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.8 -> 0.1.9, Splice.Amulet:197 -> Splice.Amulet:294:

```diff
+     observer provider, beneficiary
-     observer provider
```

## MEDIUM - ensure-changed - `Splice.Amulet:LockedAmulet`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.8 -> 0.1.9, Splice.Amulet:123:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_OwnerExpireLock``

Fewer checks in the choice body (assertions or aborts): 3 before, 2 now.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:99:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Ledger effects changed. Added: none. Removed: fetch self:Splice.AmuletRules:AmuletRules.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:dedupOutputLockHolders, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, Splice.Util:fetchPublicReferenceData, Splice.Util:requireMatchingContract, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:648:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1474:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1039:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:879 -> Splice.AmuletRules:921:

```diff
      dedupLockHolders : Party -> TimeLock -> TimeLock
+     dedupLockHolders receiver (TimeLock with expiresAt; holders, optContext) =
-     dedupLockHolders receiver (TimeLock with expiresAt; holders) =
        TimeLock with
...
          holders = dedupSort $ filter (receiver /=) holders
+         optContext
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:663 -> Splice.AmuletRules:691:

```diff
    -- issue rewards for burns
+   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   issueRewards config csum t.sender t.provider summary
    -- record outputs
...
  
+   let senderRetainsControl o =
+         t.sender == o.receiver &&
+         case o.lock of
+           None -> True
+           Some l -> all (t.sender ==) l.holders
+   let (txKind, optSender)
+         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
+         | otherwise = (TxKind_Transfer, Some t.sender)
+   let totalBurn = totalBurnFromSummary summary
+   let rewardInputMeta = Metadata with
+         values =
+           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
+           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
+           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           TextMap.empty
+ 
    return TransferResult with
...
      summary = summary
+     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:795:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:789:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:964 -> Splice.AmuletRules:1005:

```diff
+ issueRewards config csum sender provider beneficiaries summary
- issueRewards config csum sender provider summary
    | usageFees <= 0.0 = pure ()
...
  
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+               totalAmount = usageFees + extraAppReward
+           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create AppRewardCoupon with
+               dso = csum.openRound.dso
+               provider
+               beneficiary = Some beneficiary
+               featured
+               amount = totalAmount * weight
+               round = csum.openRound.round
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-       when (config.issueAppRewards) $
-         void $ create AppRewardCoupon with
-           dso = csum.openRound.dso
-           provider
-           featured
-           amount = usageFees + extraAppReward
-           round = csum.openRound.round
    where
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:889 -> Splice.AmuletRules:932:

```diff
            forA_ lock $ \l -> do
+             assertWithinDeadline "lock.expiresAt" l.expiresAt
-             now <- getTime
-             let lockDuration = subTime l.expiresAt now
-             require "Positive lock duration" (days 0 <= lockDuration)
              require "Fewer than the allowed number of lock holders"
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1402 -> Splice.AmuletRules:1482:

```diff
+ splitAndBurn sender amount inputs context dso usage = do
- splitAndBurn sender amount inputs context dso = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
        ]
+     beneficiaries = None -- No beneficiaries for self-transfer
    let [TransferResultAmulet amuletCid] = transferResult.createdAmulets
...
    -- adjust the balance changes to account for the archival. This primarily matters for the tx log parsers in scan and the wallet.
+   let result = transferResult with
+         meta = None -- the calling choice will take care of setting the metadata on its own result
+         summary.balanceChanges =
+           Map.insertWith
+             (+)
+             sender
+             (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
+             transferResult.summary.balanceChanges
+   let burnAmount = totalBurnFromSummary transferResult.summary + amount
+   let meta = simpleHoldingTxMeta TxKind_Burn (Some usage) (Some burnAmount) <>
+              fromOptional mempty transferResult.meta -- overwrite so we keep the reward input metadata
+   pure (result, meta)
-   return transferResult with
-     summary.balanceChanges =
-       Map.insertWith
-         (+)
-         sender
-         (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
-         transferResult.summary.balanceChanges
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:802:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:716 -> Splice.AmuletRules:760:

```diff
  summarizeAndValidateContext context dso tf = do
-   now <- getTime
    -- check that the current mining round is open
    openRound <- fetchReferenceData (ForDso with dso) context.openMiningRound
+   assertDeadlineExceeded "openRound.opensAt" openRound.opensAt
-   require ("mining round is open: " <> show openRound) (openRound.opensAt <= now)
  
...
      issuingRound <- fetchReferenceData (ForRound with dso; round) issuingRoundCid
+     assertDeadlineExceeded ("issuingRound[" <> show round.number <> "].opensAt") issuingRound.opensAt
-     now <- getTime
-     require "issuing round is open" (issuingRound.opensAt <= now)
      pure (round, issuingRound)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:955:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Send``

Fewer checks in the choice body (assertions or aborts): 3 before, 2 now.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## MEDIUM - ensure-changed - `Splice.ExternalPartyAmuletRules:TransferCommand`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.8 -> 0.1.9, Splice.ExternalPartyAmuletRules:110:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.ExternalPartyAmuletRules:TransferCommand, choice `TransferCommand_Send``

Fewer checks in the choice body (assertions or aborts): 4 before, 3 now.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - dependency-behaviour-changed - `(package)`

1 expressions are unchanged in this package but call dependency code that changed.

- Changed dependencies: daml-stdlib 3.0.0.20240318.12913.0 -> 3.3.0.20250502.13767.0, splice-util 0.1.2 -> 0.1.3.
- Affected: Splice.Amulet:ValidatorRewardCoupon ValidatorRewardCoupon_ArchiveAsValidator (body).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.Amulet:Amulet, choice `Amulet_Expire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllOpenRounds, Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.Amulet:389:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:AppRewardCoupon, choice `AppRewardCoupon_DsoExpire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - template-added - `Splice.Amulet:FeaturedAppActivityMarker`

New template. Review it like a new package.

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmulet``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllOpenRounds, Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.Amulet:389:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_Unlock``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.Amulet:SvRewardCoupon, choice `SvRewardCoupon_DsoExpire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.Amulet:ValidatorRewardCoupon, choice `ValidatorRewardCoupon_DsoExpire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - template-added - `Splice.AmuletAllocation:AmuletAllocation`

New template. Review it like a new package.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AdvanceOpenMiningRounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:computeSynchronizerFees, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:dedupOutputLockHolders, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, Splice.Util:requireMatchingContract, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:648:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1041 -> Splice.AmuletRules:1091:

```diff
    -- compute traffic cost in USD
+   configUsd <- getValueAsOfLedgerTime amuletRules.configSchedule
-   now <- getTime
-   let configUsd = getValueAsOf now amuletRules.configSchedule
    let extraTrafficPrice = configUsd.decentralizedSynchronizer.fees.extraTrafficPrice
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1039:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:879 -> Splice.AmuletRules:921:

```diff
      dedupLockHolders : Party -> TimeLock -> TimeLock
+     dedupLockHolders receiver (TimeLock with expiresAt; holders, optContext) =
-     dedupLockHolders receiver (TimeLock with expiresAt; holders) =
        TimeLock with
...
          holders = dedupSort $ filter (receiver /=) holders
+         optContext
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:663 -> Splice.AmuletRules:691:

```diff
    -- issue rewards for burns
+   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   issueRewards config csum t.sender t.provider summary
    -- record outputs
...
  
+   let senderRetainsControl o =
+         t.sender == o.receiver &&
+         case o.lock of
+           None -> True
+           Some l -> all (t.sender ==) l.holders
+   let (txKind, optSender)
+         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
+         | otherwise = (TxKind_Transfer, Some t.sender)
+   let totalBurn = totalBurnFromSummary summary
+   let rewardInputMeta = Metadata with
+         values =
+           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
+           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
+           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           TextMap.empty
+ 
    return TransferResult with
...
      summary = summary
+     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:795:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:789:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:964 -> Splice.AmuletRules:1005:

```diff
+ issueRewards config csum sender provider beneficiaries summary
- issueRewards config csum sender provider summary
    | usageFees <= 0.0 = pure ()
...
  
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+               totalAmount = usageFees + extraAppReward
+           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create AppRewardCoupon with
+               dso = csum.openRound.dso
+               provider
+               beneficiary = Some beneficiary
+               featured
+               amount = totalAmount * weight
+               round = csum.openRound.round
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-       when (config.issueAppRewards) $
-         void $ create AppRewardCoupon with
-           dso = csum.openRound.dso
-           provider
-           featured
-           amount = usageFees + extraAppReward
-           round = csum.openRound.round
    where
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:889 -> Splice.AmuletRules:932:

```diff
            forA_ lock $ \l -> do
+             assertWithinDeadline "lock.expiresAt" l.expiresAt
-             now <- getTime
-             let lockDuration = subTime l.expiresAt now
-             require "Positive lock duration" (days 0 <= lockDuration)
              require "Fewer than the allowed number of lock holders"
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1402 -> Splice.AmuletRules:1482:

```diff
+ splitAndBurn sender amount inputs context dso usage = do
- splitAndBurn sender amount inputs context dso = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
        ]
+     beneficiaries = None -- No beneficiaries for self-transfer
    let [TransferResultAmulet amuletCid] = transferResult.createdAmulets
...
    -- adjust the balance changes to account for the archival. This primarily matters for the tx log parsers in scan and the wallet.
+   let result = transferResult with
+         meta = None -- the calling choice will take care of setting the metadata on its own result
+         summary.balanceChanges =
+           Map.insertWith
+             (+)
+             sender
+             (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
+             transferResult.summary.balanceChanges
+   let burnAmount = totalBurnFromSummary transferResult.summary + amount
+   let meta = simpleHoldingTxMeta TxKind_Burn (Some usage) (Some burnAmount) <>
+              fromOptional mempty transferResult.meta -- overwrite so we keep the reward input metadata
+   pure (result, meta)
-   return transferResult with
-     summary.balanceChanges =
-       Map.insertWith
-         (+)
-         sender
-         (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
-         transferResult.summary.balanceChanges
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:802:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:716 -> Splice.AmuletRules:760:

```diff
  summarizeAndValidateContext context dso tf = do
-   now <- getTime
    -- check that the current mining round is open
    openRound <- fetchReferenceData (ForDso with dso) context.openMiningRound
+   assertDeadlineExceeded "openRound.opensAt" openRound.opensAt
-   require ("mining round is open: " <> show openRound) (openRound.opensAt <= now)
  
...
      issuingRound <- fetchReferenceData (ForRound with dso; round) issuingRoundCid
+     assertDeadlineExceeded ("issuingRound[" <> show round.number <> "].opensAt") issuingRound.opensAt
-     now <- getTime
-     require "issuing round is open" (issuingRound.opensAt <= now)
      pure (round, issuingRound)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:955:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ClaimExpiredRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeFees, Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:dedupOutputLockHolders, Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:642:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:648:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:879 -> Splice.AmuletRules:921:

```diff
      dedupLockHolders : Party -> TimeLock -> TimeLock
+     dedupLockHolders receiver (TimeLock with expiresAt; holders, optContext) =
-     dedupLockHolders receiver (TimeLock with expiresAt; holders) =
        TimeLock with
...
          holders = dedupSort $ filter (receiver /=) holders
+         optContext
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:dedupOutputLockHolders, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, Splice.Util:fetchPublicReferenceData, Splice.Util:requireMatchingContract, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:648:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1474:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1039:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:879 -> Splice.AmuletRules:921:

```diff
      dedupLockHolders : Party -> TimeLock -> TimeLock
+     dedupLockHolders receiver (TimeLock with expiresAt; holders, optContext) =
-     dedupLockHolders receiver (TimeLock with expiresAt; holders) =
        TimeLock with
...
          holders = dedupSort $ filter (receiver /=) holders
+         optContext
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:663 -> Splice.AmuletRules:691:

```diff
    -- issue rewards for burns
+   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   issueRewards config csum t.sender t.provider summary
    -- record outputs
...
  
+   let senderRetainsControl o =
+         t.sender == o.receiver &&
+         case o.lock of
+           None -> True
+           Some l -> all (t.sender ==) l.holders
+   let (txKind, optSender)
+         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
+         | otherwise = (TxKind_Transfer, Some t.sender)
+   let totalBurn = totalBurnFromSummary summary
+   let rewardInputMeta = Metadata with
+         values =
+           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
+           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
+           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           TextMap.empty
+ 
    return TransferResult with
...
      summary = summary
+     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:795:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:789:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:964 -> Splice.AmuletRules:1005:

```diff
+ issueRewards config csum sender provider beneficiaries summary
- issueRewards config csum sender provider summary
    | usageFees <= 0.0 = pure ()
...
  
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+               totalAmount = usageFees + extraAppReward
+           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create AppRewardCoupon with
+               dso = csum.openRound.dso
+               provider
+               beneficiary = Some beneficiary
+               featured
+               amount = totalAmount * weight
+               round = csum.openRound.round
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-       when (config.issueAppRewards) $
-         void $ create AppRewardCoupon with
-           dso = csum.openRound.dso
-           provider
-           featured
-           amount = usageFees + extraAppReward
-           round = csum.openRound.round
    where
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:889 -> Splice.AmuletRules:932:

```diff
            forA_ lock $ \l -> do
+             assertWithinDeadline "lock.expiresAt" l.expiresAt
-             now <- getTime
-             let lockDuration = subTime l.expiresAt now
-             require "Positive lock duration" (days 0 <= lockDuration)
              require "Fewer than the allowed number of lock holders"
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1402 -> Splice.AmuletRules:1482:

```diff
+ splitAndBurn sender amount inputs context dso usage = do
- splitAndBurn sender amount inputs context dso = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
        ]
+     beneficiaries = None -- No beneficiaries for self-transfer
    let [TransferResultAmulet amuletCid] = transferResult.createdAmulets
...
    -- adjust the balance changes to account for the archival. This primarily matters for the tx log parsers in scan and the wallet.
+   let result = transferResult with
+         meta = None -- the calling choice will take care of setting the metadata on its own result
+         summary.balanceChanges =
+           Map.insertWith
+             (+)
+             sender
+             (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
+             transferResult.summary.balanceChanges
+   let burnAmount = totalBurnFromSummary transferResult.summary + amount
+   let meta = simpleHoldingTxMeta TxKind_Burn (Some usage) (Some burnAmount) <>
+              fromOptional mempty transferResult.meta -- overwrite so we keep the reward input metadata
+   pure (result, meta)
-   return transferResult with
-     summary.balanceChanges =
-       Map.insertWith
-         (+)
-         sender
-         (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
-         transferResult.summary.balanceChanges
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:802:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:716 -> Splice.AmuletRules:760:

```diff
  summarizeAndValidateContext context dso tf = do
-   now <- getTime
    -- check that the current mining round is open
    openRound <- fetchReferenceData (ForDso with dso) context.openMiningRound
+   assertDeadlineExceeded "openRound.opensAt" openRound.opensAt
-   require ("mining round is open: " <> show openRound) (openRound.opensAt <= now)
  
...
      issuingRound <- fetchReferenceData (ForRound with dso; round) issuingRoundCid
+     assertDeadlineExceeded ("issuingRound[" <> show round.number <> "].opensAt") issuingRound.opensAt
-     now <- getTime
-     require "issuing round is open" (issuingRound.opensAt <= now)
      pure (round, issuingRound)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:955:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:dedupOutputLockHolders, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, Splice.Util:fetchPublicReferenceData, Splice.Util:requireMatchingContract, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:648:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1474:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1039:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:879 -> Splice.AmuletRules:921:

```diff
      dedupLockHolders : Party -> TimeLock -> TimeLock
+     dedupLockHolders receiver (TimeLock with expiresAt; holders, optContext) =
-     dedupLockHolders receiver (TimeLock with expiresAt; holders) =
        TimeLock with
...
          holders = dedupSort $ filter (receiver /=) holders
+         optContext
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:663 -> Splice.AmuletRules:691:

```diff
    -- issue rewards for burns
+   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   issueRewards config csum t.sender t.provider summary
    -- record outputs
...
  
+   let senderRetainsControl o =
+         t.sender == o.receiver &&
+         case o.lock of
+           None -> True
+           Some l -> all (t.sender ==) l.holders
+   let (txKind, optSender)
+         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
+         | otherwise = (TxKind_Transfer, Some t.sender)
+   let totalBurn = totalBurnFromSummary summary
+   let rewardInputMeta = Metadata with
+         values =
+           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
+           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
+           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           TextMap.empty
+ 
    return TransferResult with
...
      summary = summary
+     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:795:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:789:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:964 -> Splice.AmuletRules:1005:

```diff
+ issueRewards config csum sender provider beneficiaries summary
- issueRewards config csum sender provider summary
    | usageFees <= 0.0 = pure ()
...
  
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+               totalAmount = usageFees + extraAppReward
+           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create AppRewardCoupon with
+               dso = csum.openRound.dso
+               provider
+               beneficiary = Some beneficiary
+               featured
+               amount = totalAmount * weight
+               round = csum.openRound.round
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-       when (config.issueAppRewards) $
-         void $ create AppRewardCoupon with
-           dso = csum.openRound.dso
-           provider
-           featured
-           amount = usageFees + extraAppReward
-           round = csum.openRound.round
    where
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:889 -> Splice.AmuletRules:932:

```diff
            forA_ lock $ \l -> do
+             assertWithinDeadline "lock.expiresAt" l.expiresAt
-             now <- getTime
-             let lockDuration = subTime l.expiresAt now
-             require "Positive lock duration" (days 0 <= lockDuration)
              require "Fewer than the allowed number of lock holders"
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1402 -> Splice.AmuletRules:1482:

```diff
+ splitAndBurn sender amount inputs context dso usage = do
- splitAndBurn sender amount inputs context dso = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
        ]
+     beneficiaries = None -- No beneficiaries for self-transfer
    let [TransferResultAmulet amuletCid] = transferResult.createdAmulets
...
    -- adjust the balance changes to account for the archival. This primarily matters for the tx log parsers in scan and the wallet.
+   let result = transferResult with
+         meta = None -- the calling choice will take care of setting the metadata on its own result
+         summary.balanceChanges =
+           Map.insertWith
+             (+)
+             sender
+             (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
+             transferResult.summary.balanceChanges
+   let burnAmount = totalBurnFromSummary transferResult.summary + amount
+   let meta = simpleHoldingTxMeta TxKind_Burn (Some usage) (Some burnAmount) <>
+              fromOptional mempty transferResult.meta -- overwrite so we keep the reward input metadata
+   pure (result, meta)
-   return transferResult with
-     summary.balanceChanges =
-       Map.insertWith
-         (+)
-         sender
-         (BalanceChange (-amuletAsOfRoundZero) (- (amulet.amount.ratePerRound.rate)))
-         transferResult.summary.balanceChanges
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:802:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:716 -> Splice.AmuletRules:760:

```diff
  summarizeAndValidateContext context dso tf = do
-   now <- getTime
    -- check that the current mining round is open
    openRound <- fetchReferenceData (ForDso with dso) context.openMiningRound
+   assertDeadlineExceeded "openRound.opensAt" openRound.opensAt
-   require ("mining round is open: " <> show openRound) (openRound.opensAt <= now)
  
...
      issuingRound <- fetchReferenceData (ForRound with dso; round) issuingRoundCid
+     assertDeadlineExceeded ("issuingRound[" <> show round.number <> "].opensAt") issuingRound.opensAt
-     now <- getTime
-     require "issuing round is open" (issuingRound.opensAt <= now)
      pure (round, issuingRound)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:955:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_Tap``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:267 -> Splice.AmuletRules:276:

```diff
            amuletSum = result.amuletSum
+           meta = Some (simpleHoldingTxMeta TxKind_Mint (Some "tapped faucet") None)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeUnclaimedRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_Archive``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_Close``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_StartIssuing``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Mint``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:556:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:dedupOutputLockHolders, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, Splice.Util:requireMatchingContract, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:648:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1039:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:879 -> Splice.AmuletRules:921:

```diff
      dedupLockHolders : Party -> TimeLock -> TimeLock
+     dedupLockHolders receiver (TimeLock with expiresAt; holders, optContext) =
-     dedupLockHolders receiver (TimeLock with expiresAt; holders) =
        TimeLock with
...
          holders = dedupSort $ filter (receiver /=) holders
+         optContext
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:663 -> Splice.AmuletRules:691:

```diff
    -- issue rewards for burns
+   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   issueRewards config csum t.sender t.provider summary
    -- record outputs
...
  
+   let senderRetainsControl o =
+         t.sender == o.receiver &&
+         case o.lock of
+           None -> True
+           Some l -> all (t.sender ==) l.holders
+   let (txKind, optSender)
+         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
+         | otherwise = (TxKind_Transfer, Some t.sender)
+   let totalBurn = totalBurnFromSummary summary
+   let rewardInputMeta = Metadata with
+         values =
+           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
+           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
+           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
+           TextMap.empty
+ 
    return TransferResult with
...
      summary = summary
+     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:795:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:789:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:964 -> Splice.AmuletRules:1005:

```diff
+ issueRewards config csum sender provider beneficiaries summary
- issueRewards config csum sender provider summary
    | usageFees <= 0.0 = pure ()
...
  
+       if config.issueAppRewards
+         then do
+           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+           validateAppRewardBeneficiaries beneficiaries'
+           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+               totalAmount = usageFees + extraAppReward
+           forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create AppRewardCoupon with
+               dso = csum.openRound.dso
+               provider
+               beneficiary = Some beneficiary
+               featured
+               amount = totalAmount * weight
+               round = csum.openRound.round
+         else do
+           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-       when (config.issueAppRewards) $
-         void $ create AppRewardCoupon with
-           dso = csum.openRound.dso
-           provider
-           featured
-           amount = usageFees + extraAppReward
-           round = csum.openRound.round
    where
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:889 -> Splice.AmuletRules:932:

```diff
            forA_ lock $ \l -> do
+             assertWithinDeadline "lock.expiresAt" l.expiresAt
-             now <- getTime
-             let lockDuration = subTime l.expiresAt now
-             require "Positive lock duration" (days 0 <= lockDuration)
              require "Fewer than the allowed number of lock holders"
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:802:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:716 -> Splice.AmuletRules:760:

```diff
  summarizeAndValidateContext context dso tf = do
-   now <- getTime
    -- check that the current mining round is open
    openRound <- fetchReferenceData (ForDso with dso) context.openMiningRound
+   assertDeadlineExceeded "openRound.opensAt" openRound.opensAt
-   require ("mining round is open: " <> show openRound) (openRound.opensAt <= now)
  
...
      issuingRound <- fetchReferenceData (ForRound with dso; round) issuingRoundCid
+     assertDeadlineExceeded ("issuingRound[" <> show round.number <> "].opensAt") issuingRound.opensAt
-     now <- getTime
-     require "issuing round is open" (issuingRound.opensAt <= now)
      pure (round, issuingRound)
```

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:955:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Cancel``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.AmuletRules:1358 -> Splice.AmuletRules:1437:

```diff
        do require ("Controller " <> show p <> " is either receiver or provider") (p `elem` [receiver, provider])
+          assertWithinDeadline "TransferPreapproval.expiresAt" expiresAt
-          now <- getTime
-          require "Contract has not expired" (now < expiresAt)
           pure TransferPreapproval_CancelResult
```

## LOW - template-added - `Splice.AmuletTransferInstruction:AmuletTransferInstruction`

New template. Review it like a new package.

## LOW - body-changed - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules, choice `ExternalPartyAmuletRules_CreateTransferCommand``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.ExternalPartyAmuletRules:24 -> Splice.ExternalPartyAmuletRules:38:

```diff
          nonce : Int
+         description : Optional Text
        controller sender
+       do -- TODO(#18633): once the `isLedgerTimeXX` primitives are in, enforce that `expiresAt` is in the future
+          -- We don't do that yet, as otherwise this would be a regression, as it would introduce a time-bound
+          -- on the prepared transaction creating the transfer command.
+          cmd <- create TransferCommand with
-       do cmd <- create TransferCommand with
             dso
...
             amount
-            -- We cannot enforce a max expiry as we cannot call getTime.
             expiresAt
             nonce
+            description
           pure (ExternalPartyAmuletRules_CreateTransferCommandResult cmd)
```

## LOW - body-changed - `Splice.ExternalPartyAmuletRules:TransferCommand, choice `TransferCommand_Expire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.ExternalPartyAmuletRules:165 -> Splice.ExternalPartyAmuletRules:202:

```diff
        do require "Controller is a stakeholder" (p `elem` stakeholder this)
+          assertDeadlineExceeded "TransferCommand.expiresAt" expiresAt
-          now <- getTime
-          require "TransferCommand is expired" (expiresAt <= now)
           pure TransferCommand_ExpireResult with
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorFaucetCoupon, choice `ValidatorFaucetCoupon_DsoExpire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_RecordValidatorLivenessActivity``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLivenessActivityRecord, choice `ValidatorLivenessActivityRecord_DsoExpire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

