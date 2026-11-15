# dar-vet: splice-amulet 0.1.16 -> 0.1.17

Old package ID `c208d7ead1e4e9b610fc2054d0bf00716144ad444011bce0b02dcd6cd0cb8a23`, new package ID `6c5802f86709a0ad4784af81f0bab40f3070b2f58128d8843da1e1784c147802`.

**57 findings:** 24 HIGH, 18 MEDIUM, 15 LOW.

## HIGH - new-choice - `Splice.Amulet:Amulet, choice `Amulet_ExpireV2``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: fetch self:Splice.ExternalPartyConfigState:ExternalPartyConfigState.

Version 0.1.17, Splice.Amulet:138:

```daml
    choice Amulet_ExpireV2 : Amulet_ExpireV2Result
      with
        externalPartyConfigState0Cid : ContractId ExternalPartyConfigState
        externalPartyConfigState1Cid : ContractId ExternalPartyConfigState
      controller dso
      do  requireAmuletExpiredForAllRounds externalPartyConfigState0Cid externalPartyConfigState1Cid this
          let expireSum = AmuletExpireV2Summary with
                owner = owner
          return Amulet_ExpireV2Result with
            meta = simpleHoldingTxMeta TxKind_ExpireDust None (Some amount.initialAmount)
            ..
```

## HIGH - new-choice - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmuletV2``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: fetch self:Splice.ExternalPartyConfigState:ExternalPartyConfigState.

Version 0.1.17, Splice.Amulet:216:

```daml
    choice LockedAmulet_ExpireAmuletV2 : LockedAmulet_ExpireAmuletV2Result
      with
        externalPartyConfigState0Cid : ContractId ExternalPartyConfigState
        externalPartyConfigState1Cid : ContractId ExternalPartyConfigState
      controller amulet.dso
      do
         requireAmuletExpiredForAllRounds externalPartyConfigState0Cid externalPartyConfigState1Cid amulet
         let expireSum = AmuletExpireV2Summary with
               owner = amulet.owner
         return LockedAmulet_ExpireAmuletV2Result with
           meta = simpleHoldingTxMeta TxKind_ExpireDust None (Some amulet.amount.initialAmount)
           ..
```

## HIGH - new-choice - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_OwnerExpireLockV2``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:Amulet.

Version 0.1.17, Splice.Amulet:203:

```daml
    choice LockedAmulet_OwnerExpireLockV2 : LockedAmulet_OwnerExpireLockV2Result
      controller amulet.owner
      do assertDeadlineExceeded "Lock.expiresAt" lock.expiresAt
         amuletCid <- create amulet
         return LockedAmulet_OwnerExpireLockV2Result with
          meta = simpleHoldingTxMeta TxKind_Unlock (Some "lock expired") None, ..
```

## HIGH - new-choice - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_UnlockV2``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:Amulet.

Version 0.1.17, Splice.Amulet:191:

```daml
    choice LockedAmulet_UnlockV2 : LockedAmulet_UnlockV2Result
      controller amulet.owner :: lock.holders
      do amuletCid <- create amulet
         return LockedAmulet_UnlockV2Result with
           meta = simpleHoldingTxMeta TxKind_Unlock (Some "holders released lock") None, ..
```

## HIGH - new-choice - `Splice.AmuletAllocation:AmuletAllocation, choice `AmuletAllocation_DsoExpire``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise self:Splice.Amulet:LockedAmulet.LockedAmulet_UnlockV2.

Version 0.1.17, Splice.AmuletAllocation:66:

```daml
    choice AmuletAllocation_DsoExpire : AmuletAllocation_DsoExpireResult
      with
        expireLock : Bool
      controller allocationInstrumentAdmin allocation
      do
        assertDeadlineExceeded "allocation.settlement.settleBefore" allocation.settlement.settleBefore

        let extraArgs = ExtraArgs with
              context = ChoiceContext with
                values = TextMap.fromList [(expireLockKey, toAnyValue expireLock)]
              meta = emptyMetadata

        senderHoldingCids <- unlockAmuletAllocation this extraArgs

        pure AmuletAllocation_DsoExpireResult with
          senderHoldingCids
          meta = Metadata with 
            values = TextMap.fromList [(reasonMetaKey, "expired as settleBefore has passed")]
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AllocateDevelopmentFundCoupon``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:654:

```text
(source text identical: the difference comes from code this function calls)
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Amulet_ExpireTransferInstructions``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferInstruction.TransferInstruction_Update, fetch splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferInstruction.

Version 0.1.17, Splice.AmuletRules:774:

```daml
    nonconsuming choice AmuletRules_Amulet_ExpireTransferInstructions : AmuletRules_Amulet_ExpireTransferInstructionsResult
      with
        expectedDso: Party
        inputs : [AmuletRules_ExpireTransferInstructionInput]
        observers : [Party]
      observer observers
      controller dso
      do
        require "The expectedDso must match the AmuletDso" (dso== expectedDso)
        individualResults <- forA inputs $ \input -> do
          instr <- fetchCheckedInterface (ForDso dso) input.transferInstructionCid
          let instrView = view instr

          require "The controller must be the admin of the transfer instruction's instrument" (instrView.transfer.instrumentId.admin == dso)


          exercise input.transferInstructionCid TransferInstruction_Update with
            extraActors = []
            extraArgs = ExtraArgs with
              meta = emptyMetadata
              context = ChoiceContext with
                values = TextMap.fromList [(expireLockKey, toAnyValue input.expireLock)]

        pure AmuletRules_Amulet_ExpireTransferInstructionsResult with
          results = individualResults
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BootstrapExternalPartyConfigState``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.ExternalPartyConfigState:ExternalPartyConfigState, fetch self:Splice.Round:OpenMiningRound.

Version 0.1.17, Splice.AmuletRules:420:

```daml
    nonconsuming choice AmuletRules_BootstrapExternalPartyConfigState : AmuletRules_BootstrapExternalPartyConfigStateResult
      with
        openMiningRoundTriple : OpenMiningRoundTriple
        expectedDso : Party
      controller dso
      do
         checkExpectedDso dso (Some expectedDso)
         bootstrapExternalPartyConfigState this openMiningRoundTriple
         pure AmuletRules_BootstrapExternalPartyConfigStateResult
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:209 -> Splice.AmuletRules:231:

```diff
            Right _ -> pure ()
-         openRound <- fetchReferenceData (ForDso with dso) context.openMiningRound
-         let transferConfigAmulet = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
          (trafficCostAmulet, trafficCostUsd) <- computeSynchronizerFees dso provider trafficAmount this context
+         (transferResult, meta) <- splitAndBurn provider trafficCostAmulet inputs context dso "traffic purchase"
-         -- We want to burn exactly trafficCostAmulet, and createFee is already burnt by the self-transfer itself, so we subtract the createFee from the amount we split off and burn explicitly.
-         (transferResult, meta) <- splitAndBurn provider (trafficCostAmulet - transferConfigAmulet.createFee.fee) inputs context dso "traffic purchase"
          -- create a new MemberTraffic contract for the purchased traffic
```

## HIGH - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Ledger effects changed. Added: create self:Splice.Amulet:FeaturedAppActivityMarker. Removed: create self:Splice.Amulet:AppRewardCoupon.

- The choice is non-consuming, so it can be repeated: each call now creates a contract.
- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1868:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:115 -> Splice.AmuletRules:139:

```diff
       controller sender
+      do deprecatedChoice "splice-amulet" "0.1.17" "AmuletRules_ComputeFees"
-      do checkExpectedDso dso expectedDso
-         fees <- computeFees dso context sender outputs
-         return AmuletRules_ComputeFeesResult with fees
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:168:

```text
(source text identical: the difference comes from code this function calls)
```

## HIGH - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Ledger effects changed. Added: create self:Splice.Amulet:FeaturedAppActivityMarker. Removed: create self:Splice.Amulet:AppRewardCoupon.

- The choice is non-consuming, so it can be repeated: each call now creates a contract.
- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1868:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:198:

```text
(source text identical: the difference comes from code this function calls)
```

## HIGH - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Ledger effects changed. Added: create self:Splice.Amulet:FeaturedAppActivityMarker. Removed: create self:Splice.Amulet:AppRewardCoupon.

- The choice is non-consuming, so it can be repeated: each call now creates a contract.
- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1868:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_FeatureApp``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:348:

```text
(source text identical: the difference comes from code this function calls)
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_Tap``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:335:

```text
(source text identical: the difference comes from code this function calls)
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Fetch``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:698:

```text
(source text identical: the difference comes from code this function calls)
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Mint``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:311:

```text
(source text identical: the difference comes from code this function calls)
```

## HIGH - controller-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:152:

```text
(source text identical: the difference comes from code this function calls)
```

## HIGH - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Ledger effects changed. Added: create self:Splice.Amulet:FeaturedAppActivityMarker. Removed: create self:Splice.Amulet:AppRewardCoupon, create self:Splice.Amulet:ValidatorRewardCoupon.

- The choice is non-consuming, so it can be repeated: each call now creates a contract.
- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UpdateExternalPartyConfigStates``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.ExternalPartyConfigState:ExternalPartyConfigState, exercise self:Splice.ExternalPartyConfigState:ExternalPartyConfigState.Archive, fetch self:Splice.ExternalPartyConfigState:ExternalPartyConfigState, fetch self:Splice.Round:OpenMiningRound.

Version 0.1.17, Splice.AmuletRules:800:

```daml
    nonconsuming choice AmuletRules_UpdateExternalPartyConfigStates : AmuletRules_UpdateExternalPartyConfigStatesResult
      with
        externalPartyConfigStateCid0 : ContractId ExternalPartyConfigState
        externalPartyConfigStateCid1 : ContractId ExternalPartyConfigState
        openMiningRoundTriple : OpenMiningRoundTriple
      controller dso
      do
        now <- getTime
        let config = getValueAsOf now configSchedule
        validatedRounds <- validateOpenMiningRoundTriple dso openMiningRoundTriple
        externalPartyConfigState0 <- fetchAndArchive (ForDso dso) externalPartyConfigStateCid0
        externalPartyConfigState1 <- fetchReferenceData (ForDso dso) externalPartyConfigStateCid1
        assertDeadlineExceeded "ExternalPartyConfigState0.targetArchiveAfter" externalPartyConfigState0.targetArchiveAfter
        require "ExternalPartyConfigState0.targetArchiveAfter <= ExternalPartyConfigState1.targetArchiveAfter" (externalPartyConfigState0.targetArchiveAfter <= externalPartyConfigState1.targetArchiveAfter)
        newExternalPartyConfigStateCid <- create ExternalPartyConfigState with
          dso
          targetArchiveAfter = now `addRelTime` (getExternalPartyConfigStateTickDuration config + getExternalPartyConfigStateTickDuration config)
          -- We use the oldest round for holding fees. That ensures that the rounds on ExternalPartyConfigState always lag behind the rounds on
          -- OpenMiningRound contracts so we underapproximate holding fees. Using the latest round can break that invariant if rounds stop advancing
          -- but ExternalPartyConfigState still advances. Not super relevant in practice but better to be safe.
          holdingFeesOpenRoundNumber = validatedRounds.oldestRound.round
          amuletPrice = validatedRounds.latestUsableRound.amuletPrice
          transferConfig = transferConfigToTransferConfigV2 validatedRounds.latestUsableRound.transferConfigUsd
        pure AmuletRules_UpdateExternalPartyConfigStatesResult with ..
```

## HIGH - new-choice - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.Amulet:Amulet, create self:Splice.Amulet:FeaturedAppActivityMarker, create self:Splice.Amulet:LockedAmulet, exercise self:Splice.Amulet:Amulet.Archive, exercise self:Splice.Amulet:AppRewardCoupon.Archive, exercise self:Splice.Amulet:DevelopmentFundCoupon.Archive, exercise self:Splice.Amulet:SvRewardCoupon.SvRewardCoupon_ArchiveAsBeneficiary, exercise self:Splice.Amulet:UnclaimedActivityRecord.Archive, exercise self:Splice.Amulet:ValidatorRewardCoupon.ValidatorRewardCoupon_ArchiveAsValidator, exercise self:Splice.ValidatorLicense:ValidatorFaucetCoupon.Archive, exercise self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.Archive, fetch self:Splice.Amulet:Amulet, fetch self:Splice.Amulet:AppRewardCoupon, fetch self:Splice.Amulet:DevelopmentFundCoupon, fetch self:Splice.Amulet:FeaturedAppRight, fetch self:Splice.Amulet:SvRewardCoupon, fetch self:Splice.Amulet:UnclaimedActivityRecord, fetch self:Splice.Amulet:ValidatorRewardCoupon, fetch self:Splice.ExternalPartyConfigState:ExternalPartyConfigState, fetch self:Splice.ValidatorLicense:ValidatorFaucetCoupon, fetch self:Splice.ValidatorLicense:ValidatorLivenessActivityRecord.

Version 0.1.17, Splice.AmuletRules:1757:

```daml
    nonconsuming choice TransferPreapproval_SendV2 : TransferPreapproval_SendV2Result
      with
        context : ExternalPartyTransferContext
        inputs : [TransferInput]
        amount : Decimal
        sender : Party
        description : Optional Text
      controller sender
      do
         assertWithinDeadline "TransferPreapproval.expiresAt" expiresAt
         transferResult <- executeExternalPartyTransfer dso context Transfer with
           sender
           provider
           inputs
           outputs =
             [ TransferOutput with
                 receiver
                 receiverFeeRatio = 0.0
                 amount = amount
                 lock = None
             ]
           beneficiaries = None
             -- We don't make this configurable. Rewards should
             -- go to the party hosting the receiver. Allowing the sender
             -- to configure arbitrary beneficiaries doesn't make sense.
             -- If needed, we could extend preapprovals to track beneficiaries later.
         let meta = optionalMetadata reasonMetaKey identity description (fromOptional emptyMetadata transferResult.meta).values
         -- strip metadata to avoid duplicating it needlessly
         let result = transferResult with meta = None
         pure (TransferPreapproval_SendV2Result result (Metadata meta))
```

## HIGH - new-choice - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules, choice `ExternalPartyAmuletRules_ExpireAmuletAllocations``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise self:Splice.AmuletAllocation:AmuletAllocation.AmuletAllocation_DsoExpire, fetch self:Splice.AmuletAllocation:AmuletAllocation.

Version 0.1.17, Splice.ExternalPartyAmuletRules:49:

```daml
    nonconsuming choice ExternalPartyAmuletRules_ExpireAmuletAllocations : ExternalPartyAmuletRules_ExpireAmuletAllocationsResult
      with
        expectedDso : Party
        inputs : [ExternalPartyAmuletRules_ExpireAmuletAllocationInput]
        observers : [Party]
      observer observers
      controller dso
      do
        require ("expectedDso " <> show expectedDso <> " matches the dsoParty " <> show dso) (expectedDso == dso)

        individualResults <- forA inputs $ \input -> do
          alloc <- fetchButArchiveLater (ForDso with dso) input.allocationCid
          require "The dsoParty must be the admin of the allocation's instrument" (alloc.allocation.transferLeg.instrumentId.admin == dso)

          exercise input.allocationCid AmuletAllocation_DsoExpire with
            expireLock = input.expireLock

        pure ExternalPartyAmuletRules_ExpireAmuletAllocationsResult with
          results = individualResults
```

## MEDIUM - effects-changed - `Splice.Amulet:Amulet, choice `Amulet_Expire``

Ledger effects changed. Added: none. Removed: fetch self:Splice.Round:OpenMiningRound.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.Amulet:110 -> Splice.Amulet:132:

```diff
        controller dso
+       do deprecatedChoice "splice-amulet" "0.1.17" "Amulet_Expire"
-       do  requireAmuletExpiredForAllOpenRounds roundCid this
-           let expireSum = AmuletExpireSummary with
-                 owner = owner
-                 round = amount.createdAt
-                 changeToInitialAmountAsOfRoundZero = - getValueAsOfRound0 amount
-                 changeToHoldingFeesRate = - (amount.ratePerRound.rate)
-           return Amulet_ExpireResult with
-             meta = Some (simpleHoldingTxMeta TxKind_ExpireDust None (Some amount.initialAmount))
-             ..
```

## MEDIUM - guard-removed - `Splice.Amulet:Amulet, choice `Amulet_Expire``

Fewer checks in the choice body (assertions or aborts): 6 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.Amulet:110 -> Splice.Amulet:132:

```diff
        controller dso
+       do deprecatedChoice "splice-amulet" "0.1.17" "Amulet_Expire"
-       do  requireAmuletExpiredForAllOpenRounds roundCid this
-           let expireSum = AmuletExpireSummary with
-                 owner = owner
-                 round = amount.createdAt
-                 changeToInitialAmountAsOfRoundZero = - getValueAsOfRound0 amount
-                 changeToHoldingFeesRate = - (amount.ratePerRound.rate)
-           return Amulet_ExpireResult with
-             meta = Some (simpleHoldingTxMeta TxKind_ExpireDust None (Some amount.initialAmount))
-             ..
```

## MEDIUM - effects-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmulet``

Ledger effects changed. Added: none. Removed: fetch self:Splice.Round:OpenMiningRound.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.Amulet:186 -> Splice.Amulet:210:

```diff
        controller amulet.dso
+       do deprecatedChoice "splice-amulet" "0.1.17" "LockedAmulet_ExpireAmulet"
-       do
-          assertDeadlineExceeded "Lock.expiresAt" lock.expiresAt
-          requireAmuletExpiredForAllOpenRounds roundCid amulet
-          let expireSum = AmuletExpireSummary with
-                owner = amulet.owner
-                round = amulet.amount.createdAt
-                changeToInitialAmountAsOfRoundZero = -(getValueAsOfRound0 amulet.amount)
-                changeToHoldingFeesRate = - (amulet.amount.ratePerRound.rate)
-          return LockedAmulet_ExpireAmuletResult with
-            meta = Some (simpleHoldingTxMeta TxKind_ExpireDust None (Some amulet.amount.initialAmount))
-            ..
```

## MEDIUM - guard-removed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmulet``

Fewer checks in the choice body (assertions or aborts): 7 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.Amulet:186 -> Splice.Amulet:210:

```diff
        controller amulet.dso
+       do deprecatedChoice "splice-amulet" "0.1.17" "LockedAmulet_ExpireAmulet"
-       do
-          assertDeadlineExceeded "Lock.expiresAt" lock.expiresAt
-          requireAmuletExpiredForAllOpenRounds roundCid amulet
-          let expireSum = AmuletExpireSummary with
-                owner = amulet.owner
-                round = amulet.amount.createdAt
-                changeToInitialAmountAsOfRoundZero = -(getValueAsOfRound0 amulet.amount)
-                changeToHoldingFeesRate = - (amulet.amount.ratePerRound.rate)
-          return LockedAmulet_ExpireAmuletResult with
-            meta = Some (simpleHoldingTxMeta TxKind_ExpireDust None (Some amulet.amount.initialAmount))
-            ..
```

## MEDIUM - effects-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_OwnerExpireLock``

Ledger effects changed. Added: none. Removed: create self:Splice.Amulet:Amulet, fetch self:Splice.Round:OpenMiningRound.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.Amulet:172 -> Splice.Amulet:197:

```diff
        controller amulet.owner
+       do deprecatedChoice "splice-amulet" "0.1.17" "LockedAmulet_OwnerExpireLock"
-       do openMiningRound <- fetchReferenceData (ForDso with dso = amulet.dso) openRoundCid
-          assertDeadlineExceeded "Lock.expiresAt" lock.expiresAt
-          amuletCid <- create amulet
-          let amuletSum = AmuletCreateSummary with
-                amulet = amuletCid
-                amuletPrice = openMiningRound.amuletPrice
-                round = openMiningRound.round
-          return LockedAmulet_OwnerExpireLockResult with
-           meta = Some (simpleHoldingTxMeta TxKind_Unlock (Some "lock expired") None), ..
```

## MEDIUM - guard-removed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_OwnerExpireLock``

Fewer checks in the choice body (assertions or aborts): 2 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.Amulet:172 -> Splice.Amulet:197:

```diff
        controller amulet.owner
+       do deprecatedChoice "splice-amulet" "0.1.17" "LockedAmulet_OwnerExpireLock"
-       do openMiningRound <- fetchReferenceData (ForDso with dso = amulet.dso) openRoundCid
-          assertDeadlineExceeded "Lock.expiresAt" lock.expiresAt
-          amuletCid <- create amulet
-          let amuletSum = AmuletCreateSummary with
-                amulet = amuletCid
-                amuletPrice = openMiningRound.amuletPrice
-                round = openMiningRound.round
-          return LockedAmulet_OwnerExpireLockResult with
-           meta = Some (simpleHoldingTxMeta TxKind_Unlock (Some "lock expired") None), ..
```

## MEDIUM - effects-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_Unlock``

Ledger effects changed. Added: none. Removed: create self:Splice.Amulet:Amulet, fetch self:Splice.Round:OpenMiningRound.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.Amulet:159 -> Splice.Amulet:185:

```diff
        controller amulet.owner :: lock.holders
+       do deprecatedChoice "splice-amulet" "0.1.17" "LockedAmulet_Unlock"
-       do openMiningRound <- fetchReferenceData (ForDso with dso = amulet.dso) openRoundCid
-          amuletCid <- create amulet
-          let amuletSum = AmuletCreateSummary with
-                amulet = amuletCid
-                amuletPrice = openMiningRound.amuletPrice
-                round = openMiningRound.round
-          return LockedAmulet_UnlockResult with
-            meta = Some (simpleHoldingTxMeta TxKind_Unlock (Some "holders released lock") None), ..
```

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, Splice.AmuletConfig:validTransferConfig, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletConfig:58 -> Splice.AmuletConfig:79:

```diff
      optional True (>= 0.0) transferPreapprovalFee &&
+     optional True (>= 0.0) featuredAppActivityMarkerAmount &&
+     featuredAppActivityMarkerAmount == Some transferConfig.extraFeaturedAppRewardAmount
-     optional True (>= 0.0) featuredAppActivityMarkerAmount
```

Version 0.1.16 -> 0.1.17, Splice.AmuletConfig:77 -> Splice.AmuletConfig:99:

```diff
    =
+     createFee == FixedFee 0.0 &&
+     lockHolderFee == FixedFee 0.0 &&
+     lockHolderFee == FixedFee 0.0 &&
+     transferFee.initialRate == 0.0 &&
+     -- TODO(#4424) Enforce `null transferFee.steps`
+     all (\(_step, rate) -> rate == 0.0) transferFee.steps &&
-     positiveFixedFee createFee &&
      positiveRatePerRound holdingFee &&
-     validSteppedRate transferFee &&
-     positiveFixedFee lockHolderFee &&
      extraFeaturedAppRewardAmount >= 0.0 &&
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Bootstrap_Rounds``

Ledger effects changed. Added: exercise self:Splice.AmuletRules:AmuletRules.AmuletRules_BootstrapExternalPartyConfigState. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:343 -> Splice.AmuletRules:363:

```diff
        do
+         require "contractStateSchemaVersion is not set" (isNone contractStateSchemaVersion)
          now <- getTime
...
          let tickDuration = configUsd.tickDuration
+         let nr0 = fromOptional 0 initialRound
-         let nr0 = case initialRound of
-                     Some round -> round
-                     None -> 0
          let nr1 = nr0 + 1
...
  
+         oldest <- create OpenMiningRound with
-         create OpenMiningRound with
            dso; round = Round nr0; amuletPrice; opensAt = opensAt0; targetClosesAt = targetClosesAt0; issuingFor = issuingFor0; transferConfigUsd ; tickDuration ; issuanceConfig = issuanceConfig0
+         newestOpen <- create OpenMiningRound with
-         create OpenMiningRound with
            dso; round = Round nr1; amuletPrice; opensAt = opensAt1; targetClosesAt = targetClosesAt1; issuingFor = issuingFor1; transferConfigUsd ; tickDuration ; issuanceConfig = issuanceConfig1
...
            dso; round = Round nr2; amuletPrice; opensAt = opensAt2; targetClosesAt = targetClosesAt2; issuingFor = issuingFor2; transferConfigUsd ; tickDuration ; issuanceConfig = issuanceConfig2
+ 
+         exercise self AmuletRules_BootstrapExternalPartyConfigState with
+           openMiningRoundTriple = OpenMiningRoundTriple with
+             round0Cid = oldest
+             round1Cid = newestOpen
+             round2Cid = last
+           expectedDso = dso
+ 
          return AmuletRules_Bootstrap_RoundsResult with
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Fewer checks in the choice body (assertions or aborts): 31 before, 24 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1868:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Ledger effects changed. Added: none. Removed: fetch self:Splice.Round:OpenMiningRound.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:115 -> Splice.AmuletRules:139:

```diff
       controller sender
+      do deprecatedChoice "splice-amulet" "0.1.17" "AmuletRules_ComputeFees"
-      do checkExpectedDso dso expectedDso
-         fees <- computeFees dso context sender outputs
-         return AmuletRules_ComputeFeesResult with fees
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Fewer checks in the choice body (assertions or aborts): 4 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:115 -> Splice.AmuletRules:139:

```diff
       controller sender
+      do deprecatedChoice "splice-amulet" "0.1.17" "AmuletRules_ComputeFees"
-      do checkExpectedDso dso expectedDso
-         fees <- computeFees dso context sender outputs
-         return AmuletRules_ComputeFeesResult with fees
```

## MEDIUM - choice-observers-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Who is informed when this choice is exercised has changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:745:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Fewer checks in the choice body (assertions or aborts): 33 before, 26 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1868:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Fewer checks in the choice body (assertions or aborts): 33 before, 26 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1868:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Fewer checks in the choice body (assertions or aborts): 30 before, 23 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Ledger effects changed. Added: create self:Splice.Amulet:FeaturedAppActivityMarker. Removed: create self:Splice.Amulet:AppRewardCoupon.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1868:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## MEDIUM - guard-removed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Fewer checks in the choice body (assertions or aborts): 32 before, 25 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:getIssuingMiningRound, Splice.AmuletRules:getValidatorRight, Splice.AmuletRules:issueRewards, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:881:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1206 -> Splice.AmuletRules:1336:

```diff
              owner = sender
+             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound.round
      createOutput out = do
...
              owner = out.owner
+             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
-             amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound.round
  
...
          None -> TransferResultAmulet <$> create amulet
+         Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
-         Some lock -> do
-           -- We require the lock to expire before the amulet to allow app authors to
-           -- assume that a locked amulet is never archived by standard DSO while the lock is held.
-           require "lock expires before amulet"
-             (doesLockExpireBeforeAmulet currentOpenRound lock amulet.amount currentOpenRound.tickDuration)
-           TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:799 -> Splice.AmuletRules:896:

```diff
    csum <- summarizeAndValidateContext context dso t
+   executeTransfer' config csum dso t
-   isum <- summarizeAndConsumeInputs csum dso t.sender t.inputs
-   osum <- preprocessOutputs csum.config t.sender t.outputs
-   summary <- summarizeTransfer t.sender csum.openRound csum.config isum osum
-   -- check that overall transfer constraints are satisfied
-   case checkTransferConstraints t summary csum.config of
-     Left err -> throw (InvalidTransfer err)
-     Right _ -> pure ()
- 
-   -- issue rewards for burns
-   issueRewards config csum t.sender t.provider t.beneficiaries summary
-   -- record outputs
-   (createdAmulets, senderChangeAmulet) <-
-     createTransferOutputs csum.openRound csum.config dso t.sender summary osum
- 
-   let senderRetainsControl o =
-         t.sender == o.receiver &&
-         case o.lock of
-           None -> True
-           Some l -> all (t.sender ==) l.holders
-   let (txKind, optSender)
-         | all senderRetainsControl t.outputs = (TxKind_MergeSplit, None)
-         | otherwise = (TxKind_Transfer, Some t.sender)
-   let totalBurn = totalBurnFromSummary summary
-   let rewardInputMeta = Metadata with
-         values =
-           nonZeroMetadata svRewardAmountMetaKey summary.inputSvRewardAmount $
-           nonZeroMetadata appRewardAmountMetaKey summary.inputAppRewardAmount $
-           nonZeroMetadata validatorRewardAmountMetaKey summary.inputValidatorRewardAmount $
-           optionalNonZeroMetadata unclaimedActivityRecordAmountMetaKey summary.inputUnclaimedActivityRecordAmount $
-           optionalNonZeroMetadata developmentFundAmountMetaKey summary.inputDevelopmentFundAmount $
-           TextMap.empty
- 
-   return TransferResult with
-     round = csum.openRound.round
-     summary = summary
-     meta = Some (holdingTxMeta txKind None optSender (Some totalBurn) <> rewardInputMeta)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1055:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1049:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1173 -> Splice.AmuletRules:1314:

```diff
+ issueRewards config csum provider beneficiaries = do
- issueRewards config csum sender provider beneficiaries summary = do
-     when (config.issueValidatorRewards && usageFees > 0.0) $
-       void $ create ValidatorRewardCoupon with
-         dso = csum.openRound.dso
-         user = sender
-         amount = usageFees
-         round = csum.openRound.round
- 
      if config.issueAppRewards
...
          let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+         when featured $
-             totalAmount = usageFees + extraAppReward
-         when (totalAmount > 0.0) $
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
+             void $ create FeaturedAppActivityMarker
+               with
+                 dso = csum.dso
+                 provider
+                 beneficiary = beneficiary
+                 weight = weight
-             void $ create AppRewardCoupon with
-               dso = csum.openRound.dso
-               provider
-               beneficiary = Some beneficiary
-               featured
-               amount = totalAmount * weight
-               round = csum.openRound.round
        else do
...
    where
-     usageFees = sum summary.outputFees
      featured = Some provider == csum.featuredAppProvider
-     extraAppReward = if featured then csum.config.extraFeaturedAppRewardAmount else 0.0
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1094 -> Splice.AmuletRules:1238:

```diff
+ preprocessOutputs transferConfigAmulet outs =
- preprocessOutputs transferConfigAmulet sender outs =
      mapA (preprocessOutput . dedupOutputLockHolders) outs
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
-     preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
            -- compute output
+           require "Amount is positive" (0.0 < amount)
-           let outputFee = computeOutputFee transferConfigAmulet sender out
-               receiverOutputFee = outputFee * receiverFeeRatio
-           require "Amount is larger than receiver output fee" (receiverOutputFee < amount)
            return PreprocessedTransferOutput with
              owner = receiver
+             outputFee = 0.0
+             amount
-             outputFee = outputFee
-             amount = amount - receiverOutputFee
              lock
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1868:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:918 -> Splice.AmuletRules:1062:

```diff
  summarizeAndConsumeInputs csum dso sender inps = do
+     foldlA (summarizeAndConsumeInput csum.openRoundNumber) initialSummary inps
-     foldlA (summarizeAndConsumeInput csum.openRound.round) initialSummary inps
    where
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:876 -> Splice.AmuletRules:998:

```diff
  
+   return TransferContextSummaryV2 with
+     dso = openRound.dso
+     config = scaleFees2 (1.0 / openRound.amuletPrice) $ transferConfigToTransferConfigV2 openRound.transferConfigUsd
-   return TransferContextSummary with
-     config = scaleFees (1.0 / openRound.amuletPrice) openRound.transferConfigUsd
      featuredAppProvider
+     openRoundNumber = openRound.round
-     openRound = openRound
      issuingMiningRounds = Map.fromList issuingMiningRounds
      validatorRights = Map.fromList validatorRights
+     amuletPrice = openRound.amuletPrice
```

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:1117 -> Splice.AmuletRules:1259:

```diff
+ summarizeTransfer sender openRoundNumber amuletPrice transferConfigAmulet inp preprocessedOutputs = do
- summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
      -- Holding fees are only charged on `Amulet_Expire`
...
    where
+     amuletAsOfRoundZeroAdjustment = intToDecimal openRoundNumber.number * transferConfigAmulet.holdingFee.rate
-     amuletAsOfRoundZeroAdjustment = intToDecimal openRound.round.number * transferConfigAmulet.holdingFee.rate
      outputBalanceChange preprocessedOutput =
...
        - totalOutputAmount - sum outputFees
+     senderChangeAmount = leftOverAmount
-     senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
-     senderChangeAmount = leftOverAmount - senderChangeFee
  
...
        outputFees
+       senderChangeFee = min 0.0 leftOverAmount -- There is never a change fee but the leftover amount is used in the insufficient funds check.
-       senderChangeFee
        senderChangeAmount
+       amuletPrice
-       amuletPrice = openRound.amuletPrice
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AdvanceOpenMiningRounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:430:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AllocateDevelopmentFundCoupon``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:654:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ClaimExpiredRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:555:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:745:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_FeatureApp``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:348:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_Tap``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:335:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:274:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeUnclaimedDevelopmentFundCoupons``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:634:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeUnclaimedRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:615:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_Archive``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:546:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_Close``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:524:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_StartIssuing``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Issuance:computeIssuanceTranche, Splice.Issuance:computeIssuingRoundParameters, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.16 -> 0.1.17, Splice.Issuance:165 -> Splice.Issuance:165:

```diff
        unclaimedRewards = rewardsToIssue
+   | capPerCoupon <= 0.0 = IssuanceTranche with
+       rewardsToIssue
+       issuancePerCoupon = 0.0
+       unclaimedRewards = rewardsToIssue
    | otherwise =
```

Version 0.1.16 -> 0.1.17, Splice.Issuance:92:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Mint``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:311:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.AmuletRules:705:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - template-added - `Splice.ExternalPartyConfigState:ExternalPartyConfigState`

New template. Review it like a new package.

