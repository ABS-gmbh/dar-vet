# dar-vet: splice-amulet 0.1.20 -> 0.1.21

Old package ID `23f47481dab6b1ec01339d6e14494d85bb2844c25f45b26fc5c9ef4cd4942d1f`, new package ID `73e9ffdb6b0bc19a5f67372b118103926da11547ab9109eccae47e4e4cc35d6f`.

**43 findings:** 11 HIGH, 17 MEDIUM, 15 LOW.

## HIGH - new-interface-instance - `Splice.Amulet:Amulet`

Template now implements interface splice-api-token-holding-v2:Splice.Api.Token.HoldingV2:Holding. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.Amulet:LockedAmulet`

Template now implements interface splice-api-token-holding-v2:Splice.Api.Token.HoldingV2:Holding. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.AmuletAllocation:AmuletAllocation`

Template now implements interface splice-api-token-allocation-v2:Splice.Api.Token.AllocationV2:Allocation. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.AmuletRules:AmuletRules`

Template now implements interface splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.AmuletTransferInstruction:AmuletTransferInstruction`

Template now implements interface splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferInstruction. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules`

Template now implements interface splice-api-token-allocation-instruction-v2:Splice.Api.Token.AllocationInstructionV2:AllocationFactory. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules`

Template now implements interface splice-api-token-allocation-v2:Splice.Api.Token.AllocationV2:SettlementFactory. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules`

Template now implements interface splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules`

Template now implements interface splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferFactory. Its choices can be exercised on contracts created under the old version.

## HIGH - new-choice - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules, choice `ExternalPartyAmuletRules_ExpireAmuletAllocationsV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-allocation-v2:Splice.Api.Token.AllocationV2:Allocation.Allocation_Cancel, fetch splice-api-token-allocation-v2:Splice.Api.Token.AllocationV2:Allocation.

Version 0.1.21, Splice.ExternalPartyAmuletRules:77:

```daml
    nonconsuming choice ExternalPartyAmuletRules_ExpireAmuletAllocationsV2 : ExternalPartyAmuletRules_ExpireAmuletAllocationsV2Result
      with
        cancellations : [(ContractId V2.Allocation, V2.Allocation_Cancel)]
        observers : [Party]
      observer observers
      controller dso
      do
        forA_ cancellations $ \(allocCid, choiceArg) -> do
          -- prudent engineering: double-check the actors
          requireMatchExpected ("choiceArg.actors", choiceArg.actors) [dso]
          alloc <- fetchCheckedInterface @V2.Allocation (ForDso with dso) allocCid
          -- Only allow this call on expired allocations, so that it becomes safe to delegate it
          -- to any SV. Without this check a malicious SV could use this call to cancel any allocation
          -- that has the DSO party as the executor.
          case (view alloc).expiresAt of
            None -> fail "Cannot expire allocation without an expiry time"
            Some expiresAt -> do
              assertDeadlineExceeded "allocation.expiresAt" expiresAt
              exercise allocCid choiceArg

        pure ExternalPartyAmuletRules_ExpireAmuletAllocationsV2Result
```

## HIGH - new-interface-instance - `Splice.ExternalPartyConfigState:ExternalPartyConfigState`

Template now implements interface splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog. Its choices can be exercised on contracts created under the old version.

## MEDIUM - effects-changed - `Splice.Amulet:Amulet, choice `Amulet_ExpireV2``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.Amulet:603:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_ExpireAmuletV2``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.Amulet:requireAmuletExpiredForAllRounds, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.Amulet:603:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_OwnerExpireLockV2``

Ledger effects changed. Added: create self:Splice.AmuletEventLog:AmuletEventLog, exercise self:Splice.AmuletEventLog:AmuletEventLog.Archive, exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.Amulet:205 -> Splice.Amulet:220:

```diff
           amuletCid <- create amulet
+          logAmuletUnlock amulet.dso "owner released expired lock" amulet.owner self amuletCid
           return LockedAmulet_OwnerExpireLockV2Result with
```

## MEDIUM - effects-changed - `Splice.Amulet:LockedAmulet, choice `LockedAmulet_UnlockV2``

Ledger effects changed. Added: create self:Splice.AmuletEventLog:AmuletEventLog, exercise self:Splice.AmuletEventLog:AmuletEventLog.Archive, exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.Amulet:193 -> Splice.Amulet:207:

```diff
        do amuletCid <- create amulet
+          logAmuletUnlock amulet.dso "holders released lock" amulet.owner self amuletCid
           return LockedAmulet_UnlockV2Result with
```

## MEDIUM - ensure-changed - `Splice.AmuletAllocation:AmuletAllocation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.20 -> 0.1.21, Splice.AmuletAllocation:34 -> Splice.AmuletAllocation:55:

```diff
+     ensure isValidAllocationSpecificationV1 allocation &&
+       allocation.transferLeg.instrumentId.id == amuletInstrumentIdName
- template AmuletAllocation
```

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, Splice.AmuletConfig:validTransferConfig, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:134:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:154:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.AmuletConfig:transferConfigToTransferConfigV2, Splice.AmuletRules:computeSynchronizerFees, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:scaleFees2, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:36 -> Splice.AmuletConfig:38:

```diff
      maxNumLockHolders = tc.maxNumLockHolders
+     tokenStandardMaxTTL = tc.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1563 -> Splice.AmuletRules:1622:

```diff
  createTransferOutputs currentOpenRound transferConfigAmulet dso sender summary preprocessedOutputs = do
+     let indices = [0 .. length preprocessedOutputs - 1]
+     outputAmuletsWithEvents <- mapA createOutput (zip indices preprocessedOutputs)
-     outputAmulets <- mapA createOutput preprocessedOutputs
      senderChangeAmulet <- createSenderChange
+     return (map fst outputAmuletsWithEvents, senderChangeAmulet, concatMap snd outputAmuletsWithEvents)
-     return (outputAmulets, senderChangeAmulet)
    where
...
              amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
+     createOutput (i, out) = do
-     createOutput out = do
        let amulet = Amulet with
...
              amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
+       createdAmulet <- case out.lock of
- 
-       case out.lock of
          None -> TransferResultAmulet <$> create amulet
          Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
+       -- compute tx history v2 events for this output
+       let outputHoldingCid = case createdAmulet of
+             TransferResultAmulet amuletCid -> toInterfaceContractId amuletCid
+             TransferResultLockedAmulet lockedAmuletCid -> toInterfaceContractId lockedAmuletCid
+             ExtCreatedAmulet _ -> error "unexpected: ExtCreatedAmulet used for transfer output"
+       let senderAccount = basicAccount sender
+       let receiverAccount
+             | out.owner == dso = burnAccount  -- transfers to the DSO party are always burns
+             | otherwise = basicAccount out.owner
+       let leg side otherside = TransferEventsV2.TransferLegSide with
+             transferLegId = "leg" <> show i
+             side
+             otherside
+             amount = out.amount
+             instrumentId = amuletInstrumentIdName
+             meta = fromOptional emptyMetadata out.meta
+       let emptyExtraArgs = ExtraArgs with context = emptyChoiceContext; meta = emptyMetadata
+       let senderEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = senderAccount
+             inputHoldingCids = [] -- sender input holdings are recorded separately
+             transferLegSides = [leg TransferEventsV2.SenderSide receiverAccount]
+             outputHoldingCids = [] -- sender change is recorded separately
+             observers = [sender]
+             extraArgs = emptyExtraArgs
+       let receiverEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = receiverAccount
+             inputHoldingCids = [] -- no receiver input holdings
+             transferLegSides = [leg TransferEventsV2.ReceiverSide senderAccount]
+             outputHoldingCids = [outputHoldingCid] -- the created output holding
+             observers = [out.owner]
+             extraArgs = emptyExtraArgs
+       let events
+             | out.owner == dso = [senderEvent] -- the DSO party does not need holding change events
+             | otherwise = [senderEvent, receiverEvent]
+       pure (createdAmulet, events)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1101 -> Splice.AmuletRules:1120:

```diff
+ executeTransfer eventLogCid config context dso t = do
- executeTransfer config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso t
-   csum <- summarizeAndValidateContext context dso t
    executeTransfer' config csum dso t
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1117 -> Splice.AmuletRules:1136:

```diff
    -- record outputs
+   (createdAmulets, senderChangeAmulet, outputEvents) <-
-   (createdAmulets, senderChangeAmulet) <-
      createTransferOutputs csum.openRoundNumber csum.config dso t.sender summary osum
...
  
+   -- create V2 tx history mint event for rewards if rewards were minted
+   let mintAmount =
+         summary.inputAppRewardAmount +
+         summary.inputValidatorRewardAmount +
+         summary.inputSvRewardAmount +
+         fromOptional 0.0 summary.inputUnclaimedActivityRecordAmount +
+         fromOptional 0.0 summary.inputDevelopmentFundAmount
+   let optMintLeg = do
+         guard (mintAmount > 0.0)
+         pure $ TransferEventsV2.TransferLegSide with
+           transferLegId = "mint"
+           side = TransferEventsV2.ReceiverSide
+           otherside = mintAccount
+           amount = mintAmount
+           instrumentId = amuletInstrumentIdName
+           meta = Metadata with
+             values =
+               TextMap.insert reasonMetaKey "mint rewards" $
+               rewardInputMeta.values
+   let senderInputOutputEvent = TransferEventsV2.EventLog_HoldingsChange with
+         admin = dso
+         account = basicAccount t.sender
+         inputHoldingCids = [ toInterfaceContractId cid | InputAmulet cid <- t.inputs ]
+         transferLegSides = optionalToList optMintLeg
+         outputHoldingCids = [ toInterfaceContractId cid | Some cid <- pure senderChangeAmulet ]
+         observers = [t.sender]
+         extraArgs = ExtraArgs with
+           context = emptyChoiceContext
+           meta = emptyMetadata
+ 
+   -- record all holdings changes grouped by account
+   forA_ csum.eventLogCid $ \eventLogCid ->
+     forA_ (mergeHoldingsChangeEventsByAccount (senderInputOutputEvent :: outputEvents)) $ \holdingChangeEvent ->
+       exercise eventLogCid holdingChangeEvent
+ 
    return TransferResult with
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1463 -> Splice.AmuletRules:1522:

```diff
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio; meta)
-     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
              lock
+             meta
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1599 -> Splice.AmuletRules:1696:

```diff
      maxNumLockHolders = c.maxNumLockHolders
+     tokenStandardMaxTTL = c.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2095 -> Splice.AmuletRules:2214:

```diff
+ splitAndBurn eventLogCid sender amount inputs context dso usage = do
- splitAndBurn sender amount inputs context dso usage = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
          issueValidatorRewards = False
+   transferResult <- executeTransfer eventLogCid rewardsConfig context dso Transfer with
-   transferResult <- executeTransfer rewardsConfig context dso Transfer with
      sender
...
            lock = None
+           meta = Some (reasonToMeta ("split for " <> usage) emptyMetadata)
        ]
...
    amulet <- fetchAndArchive (ForOwner with dso; owner = sender) amuletCid
+   -- record event
+   logAmuletBurn dso eventLogCid sender amount usage (toInterfaceContractId amuletCid)
    -- create validator activity rewards over the whole burn.
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1204 -> Splice.AmuletRules:1260:

```diff
+ summarizeAndValidateContext eventLogCid context dso tf = do
- summarizeAndValidateContext context dso tf = do
    -- check that the current mining round is open
...
      rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
+     eventLogCid = Some eventLogCid
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1484 -> Splice.AmuletRules:1544:

```diff
      require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
+     pure summary
-     -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
-     trace summary $ trace preprocessedOutputs $ trace inp $ pure summary
    where
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.AmuletConfig:transferConfigToTransferConfigV2, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:scaleFees2, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:36 -> Splice.AmuletConfig:38:

```diff
      maxNumLockHolders = tc.maxNumLockHolders
+     tokenStandardMaxTTL = tc.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2206:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1563 -> Splice.AmuletRules:1622:

```diff
  createTransferOutputs currentOpenRound transferConfigAmulet dso sender summary preprocessedOutputs = do
+     let indices = [0 .. length preprocessedOutputs - 1]
+     outputAmuletsWithEvents <- mapA createOutput (zip indices preprocessedOutputs)
-     outputAmulets <- mapA createOutput preprocessedOutputs
      senderChangeAmulet <- createSenderChange
+     return (map fst outputAmuletsWithEvents, senderChangeAmulet, concatMap snd outputAmuletsWithEvents)
-     return (outputAmulets, senderChangeAmulet)
    where
...
              amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
+     createOutput (i, out) = do
-     createOutput out = do
        let amulet = Amulet with
...
              amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
+       createdAmulet <- case out.lock of
- 
-       case out.lock of
          None -> TransferResultAmulet <$> create amulet
          Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
+       -- compute tx history v2 events for this output
+       let outputHoldingCid = case createdAmulet of
+             TransferResultAmulet amuletCid -> toInterfaceContractId amuletCid
+             TransferResultLockedAmulet lockedAmuletCid -> toInterfaceContractId lockedAmuletCid
+             ExtCreatedAmulet _ -> error "unexpected: ExtCreatedAmulet used for transfer output"
+       let senderAccount = basicAccount sender
+       let receiverAccount
+             | out.owner == dso = burnAccount  -- transfers to the DSO party are always burns
+             | otherwise = basicAccount out.owner
+       let leg side otherside = TransferEventsV2.TransferLegSide with
+             transferLegId = "leg" <> show i
+             side
+             otherside
+             amount = out.amount
+             instrumentId = amuletInstrumentIdName
+             meta = fromOptional emptyMetadata out.meta
+       let emptyExtraArgs = ExtraArgs with context = emptyChoiceContext; meta = emptyMetadata
+       let senderEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = senderAccount
+             inputHoldingCids = [] -- sender input holdings are recorded separately
+             transferLegSides = [leg TransferEventsV2.SenderSide receiverAccount]
+             outputHoldingCids = [] -- sender change is recorded separately
+             observers = [sender]
+             extraArgs = emptyExtraArgs
+       let receiverEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = receiverAccount
+             inputHoldingCids = [] -- no receiver input holdings
+             transferLegSides = [leg TransferEventsV2.ReceiverSide senderAccount]
+             outputHoldingCids = [outputHoldingCid] -- the created output holding
+             observers = [out.owner]
+             extraArgs = emptyExtraArgs
+       let events
+             | out.owner == dso = [senderEvent] -- the DSO party does not need holding change events
+             | otherwise = [senderEvent, receiverEvent]
+       pure (createdAmulet, events)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1101 -> Splice.AmuletRules:1120:

```diff
+ executeTransfer eventLogCid config context dso t = do
- executeTransfer config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso t
-   csum <- summarizeAndValidateContext context dso t
    executeTransfer' config csum dso t
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1117 -> Splice.AmuletRules:1136:

```diff
    -- record outputs
+   (createdAmulets, senderChangeAmulet, outputEvents) <-
-   (createdAmulets, senderChangeAmulet) <-
      createTransferOutputs csum.openRoundNumber csum.config dso t.sender summary osum
...
  
+   -- create V2 tx history mint event for rewards if rewards were minted
+   let mintAmount =
+         summary.inputAppRewardAmount +
+         summary.inputValidatorRewardAmount +
+         summary.inputSvRewardAmount +
+         fromOptional 0.0 summary.inputUnclaimedActivityRecordAmount +
+         fromOptional 0.0 summary.inputDevelopmentFundAmount
+   let optMintLeg = do
+         guard (mintAmount > 0.0)
+         pure $ TransferEventsV2.TransferLegSide with
+           transferLegId = "mint"
+           side = TransferEventsV2.ReceiverSide
+           otherside = mintAccount
+           amount = mintAmount
+           instrumentId = amuletInstrumentIdName
+           meta = Metadata with
+             values =
+               TextMap.insert reasonMetaKey "mint rewards" $
+               rewardInputMeta.values
+   let senderInputOutputEvent = TransferEventsV2.EventLog_HoldingsChange with
+         admin = dso
+         account = basicAccount t.sender
+         inputHoldingCids = [ toInterfaceContractId cid | InputAmulet cid <- t.inputs ]
+         transferLegSides = optionalToList optMintLeg
+         outputHoldingCids = [ toInterfaceContractId cid | Some cid <- pure senderChangeAmulet ]
+         observers = [t.sender]
+         extraArgs = ExtraArgs with
+           context = emptyChoiceContext
+           meta = emptyMetadata
+ 
+   -- record all holdings changes grouped by account
+   forA_ csum.eventLogCid $ \eventLogCid ->
+     forA_ (mergeHoldingsChangeEventsByAccount (senderInputOutputEvent :: outputEvents)) $ \holdingChangeEvent ->
+       exercise eventLogCid holdingChangeEvent
+ 
    return TransferResult with
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2143 -> Splice.AmuletRules:2265:

```diff
+ payForTransferPreapproval reason dso provider context inputs expiresAt newExpiresAt = do
- payForTransferPreapproval dso provider context inputs expiresAt newExpiresAt = do
    amuletRules <- fetchChecked (ForDso dso) context.amuletRules
...
        (amuletPaid, _) <- computeTransferPreapprovalFee paidExtension configUsd context.context dso
+       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso reason
-       (transferResult, meta) <- splitAndBurn provider amuletPaid inputs context.context dso "renew TransferPreapproval"
        pure (amuletPaid, transferResult, meta)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1463 -> Splice.AmuletRules:1522:

```diff
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio; meta)
-     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
              lock
+             meta
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1599 -> Splice.AmuletRules:1696:

```diff
      maxNumLockHolders = c.maxNumLockHolders
+     tokenStandardMaxTTL = c.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2095 -> Splice.AmuletRules:2214:

```diff
+ splitAndBurn eventLogCid sender amount inputs context dso usage = do
- splitAndBurn sender amount inputs context dso usage = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
          issueValidatorRewards = False
+   transferResult <- executeTransfer eventLogCid rewardsConfig context dso Transfer with
-   transferResult <- executeTransfer rewardsConfig context dso Transfer with
      sender
...
            lock = None
+           meta = Some (reasonToMeta ("split for " <> usage) emptyMetadata)
        ]
...
    amulet <- fetchAndArchive (ForOwner with dso; owner = sender) amuletCid
+   -- record event
+   logAmuletBurn dso eventLogCid sender amount usage (toInterfaceContractId amuletCid)
    -- create validator activity rewards over the whole burn.
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1204 -> Splice.AmuletRules:1260:

```diff
+ summarizeAndValidateContext eventLogCid context dso tf = do
- summarizeAndValidateContext context dso tf = do
    -- check that the current mining round is open
...
      rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
+     eventLogCid = Some eventLogCid
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1484 -> Splice.AmuletRules:1544:

```diff
      require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
+     pure summary
-     -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
-     trace summary $ trace preprocessedOutputs $ trace inp $ pure summary
    where
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.AmuletConfig:transferConfigToTransferConfigV2, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:scaleFees2, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:36 -> Splice.AmuletConfig:38:

```diff
      maxNumLockHolders = tc.maxNumLockHolders
+     tokenStandardMaxTTL = tc.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2206:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1563 -> Splice.AmuletRules:1622:

```diff
  createTransferOutputs currentOpenRound transferConfigAmulet dso sender summary preprocessedOutputs = do
+     let indices = [0 .. length preprocessedOutputs - 1]
+     outputAmuletsWithEvents <- mapA createOutput (zip indices preprocessedOutputs)
-     outputAmulets <- mapA createOutput preprocessedOutputs
      senderChangeAmulet <- createSenderChange
+     return (map fst outputAmuletsWithEvents, senderChangeAmulet, concatMap snd outputAmuletsWithEvents)
-     return (outputAmulets, senderChangeAmulet)
    where
...
              amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
+     createOutput (i, out) = do
-     createOutput out = do
        let amulet = Amulet with
...
              amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
+       createdAmulet <- case out.lock of
- 
-       case out.lock of
          None -> TransferResultAmulet <$> create amulet
          Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
+       -- compute tx history v2 events for this output
+       let outputHoldingCid = case createdAmulet of
+             TransferResultAmulet amuletCid -> toInterfaceContractId amuletCid
+             TransferResultLockedAmulet lockedAmuletCid -> toInterfaceContractId lockedAmuletCid
+             ExtCreatedAmulet _ -> error "unexpected: ExtCreatedAmulet used for transfer output"
+       let senderAccount = basicAccount sender
+       let receiverAccount
+             | out.owner == dso = burnAccount  -- transfers to the DSO party are always burns
+             | otherwise = basicAccount out.owner
+       let leg side otherside = TransferEventsV2.TransferLegSide with
+             transferLegId = "leg" <> show i
+             side
+             otherside
+             amount = out.amount
+             instrumentId = amuletInstrumentIdName
+             meta = fromOptional emptyMetadata out.meta
+       let emptyExtraArgs = ExtraArgs with context = emptyChoiceContext; meta = emptyMetadata
+       let senderEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = senderAccount
+             inputHoldingCids = [] -- sender input holdings are recorded separately
+             transferLegSides = [leg TransferEventsV2.SenderSide receiverAccount]
+             outputHoldingCids = [] -- sender change is recorded separately
+             observers = [sender]
+             extraArgs = emptyExtraArgs
+       let receiverEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = receiverAccount
+             inputHoldingCids = [] -- no receiver input holdings
+             transferLegSides = [leg TransferEventsV2.ReceiverSide senderAccount]
+             outputHoldingCids = [outputHoldingCid] -- the created output holding
+             observers = [out.owner]
+             extraArgs = emptyExtraArgs
+       let events
+             | out.owner == dso = [senderEvent] -- the DSO party does not need holding change events
+             | otherwise = [senderEvent, receiverEvent]
+       pure (createdAmulet, events)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1101 -> Splice.AmuletRules:1120:

```diff
+ executeTransfer eventLogCid config context dso t = do
- executeTransfer config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso t
-   csum <- summarizeAndValidateContext context dso t
    executeTransfer' config csum dso t
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1117 -> Splice.AmuletRules:1136:

```diff
    -- record outputs
+   (createdAmulets, senderChangeAmulet, outputEvents) <-
-   (createdAmulets, senderChangeAmulet) <-
      createTransferOutputs csum.openRoundNumber csum.config dso t.sender summary osum
...
  
+   -- create V2 tx history mint event for rewards if rewards were minted
+   let mintAmount =
+         summary.inputAppRewardAmount +
+         summary.inputValidatorRewardAmount +
+         summary.inputSvRewardAmount +
+         fromOptional 0.0 summary.inputUnclaimedActivityRecordAmount +
+         fromOptional 0.0 summary.inputDevelopmentFundAmount
+   let optMintLeg = do
+         guard (mintAmount > 0.0)
+         pure $ TransferEventsV2.TransferLegSide with
+           transferLegId = "mint"
+           side = TransferEventsV2.ReceiverSide
+           otherside = mintAccount
+           amount = mintAmount
+           instrumentId = amuletInstrumentIdName
+           meta = Metadata with
+             values =
+               TextMap.insert reasonMetaKey "mint rewards" $
+               rewardInputMeta.values
+   let senderInputOutputEvent = TransferEventsV2.EventLog_HoldingsChange with
+         admin = dso
+         account = basicAccount t.sender
+         inputHoldingCids = [ toInterfaceContractId cid | InputAmulet cid <- t.inputs ]
+         transferLegSides = optionalToList optMintLeg
+         outputHoldingCids = [ toInterfaceContractId cid | Some cid <- pure senderChangeAmulet ]
+         observers = [t.sender]
+         extraArgs = ExtraArgs with
+           context = emptyChoiceContext
+           meta = emptyMetadata
+ 
+   -- record all holdings changes grouped by account
+   forA_ csum.eventLogCid $ \eventLogCid ->
+     forA_ (mergeHoldingsChangeEventsByAccount (senderInputOutputEvent :: outputEvents)) $ \holdingChangeEvent ->
+       exercise eventLogCid holdingChangeEvent
+ 
    return TransferResult with
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2143 -> Splice.AmuletRules:2265:

```diff
+ payForTransferPreapproval reason dso provider context inputs expiresAt newExpiresAt = do
- payForTransferPreapproval dso provider context inputs expiresAt newExpiresAt = do
    amuletRules <- fetchChecked (ForDso dso) context.amuletRules
...
        (amuletPaid, _) <- computeTransferPreapprovalFee paidExtension configUsd context.context dso
+       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso reason
-       (transferResult, meta) <- splitAndBurn provider amuletPaid inputs context.context dso "renew TransferPreapproval"
        pure (amuletPaid, transferResult, meta)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1463 -> Splice.AmuletRules:1522:

```diff
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio; meta)
-     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
              lock
+             meta
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1599 -> Splice.AmuletRules:1696:

```diff
      maxNumLockHolders = c.maxNumLockHolders
+     tokenStandardMaxTTL = c.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2095 -> Splice.AmuletRules:2214:

```diff
+ splitAndBurn eventLogCid sender amount inputs context dso usage = do
- splitAndBurn sender amount inputs context dso usage = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
          issueValidatorRewards = False
+   transferResult <- executeTransfer eventLogCid rewardsConfig context dso Transfer with
-   transferResult <- executeTransfer rewardsConfig context dso Transfer with
      sender
...
            lock = None
+           meta = Some (reasonToMeta ("split for " <> usage) emptyMetadata)
        ]
...
    amulet <- fetchAndArchive (ForOwner with dso; owner = sender) amuletCid
+   -- record event
+   logAmuletBurn dso eventLogCid sender amount usage (toInterfaceContractId amuletCid)
    -- create validator activity rewards over the whole burn.
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1204 -> Splice.AmuletRules:1260:

```diff
+ summarizeAndValidateContext eventLogCid context dso tf = do
- summarizeAndValidateContext context dso tf = do
    -- check that the current mining round is open
...
      rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
+     eventLogCid = Some eventLogCid
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1484 -> Splice.AmuletRules:1544:

```diff
      require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
+     pure summary
-     -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
-     trace summary $ trace preprocessedOutputs $ trace inp $ pure summary
    where
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Mint``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:scaleFees, Splice.AmuletRules:transferConfigAmuletFromOpenRound, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1587 -> Splice.AmuletRules:1683:

```diff
      maxNumLockHolders = c.maxNumLockHolders
+     tokenStandardMaxTTL = c.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1704:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.AmuletConfig:transferConfigToTransferConfigV2, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:scaleFees2, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:36 -> Splice.AmuletConfig:38:

```diff
      maxNumLockHolders = tc.maxNumLockHolders
+     tokenStandardMaxTTL = tc.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1563 -> Splice.AmuletRules:1622:

```diff
  createTransferOutputs currentOpenRound transferConfigAmulet dso sender summary preprocessedOutputs = do
+     let indices = [0 .. length preprocessedOutputs - 1]
+     outputAmuletsWithEvents <- mapA createOutput (zip indices preprocessedOutputs)
-     outputAmulets <- mapA createOutput preprocessedOutputs
      senderChangeAmulet <- createSenderChange
+     return (map fst outputAmuletsWithEvents, senderChangeAmulet, concatMap snd outputAmuletsWithEvents)
-     return (outputAmulets, senderChangeAmulet)
    where
...
              amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
+     createOutput (i, out) = do
-     createOutput out = do
        let amulet = Amulet with
...
              amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
+       createdAmulet <- case out.lock of
- 
-       case out.lock of
          None -> TransferResultAmulet <$> create amulet
          Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
+       -- compute tx history v2 events for this output
+       let outputHoldingCid = case createdAmulet of
+             TransferResultAmulet amuletCid -> toInterfaceContractId amuletCid
+             TransferResultLockedAmulet lockedAmuletCid -> toInterfaceContractId lockedAmuletCid
+             ExtCreatedAmulet _ -> error "unexpected: ExtCreatedAmulet used for transfer output"
+       let senderAccount = basicAccount sender
+       let receiverAccount
+             | out.owner == dso = burnAccount  -- transfers to the DSO party are always burns
+             | otherwise = basicAccount out.owner
+       let leg side otherside = TransferEventsV2.TransferLegSide with
+             transferLegId = "leg" <> show i
+             side
+             otherside
+             amount = out.amount
+             instrumentId = amuletInstrumentIdName
+             meta = fromOptional emptyMetadata out.meta
+       let emptyExtraArgs = ExtraArgs with context = emptyChoiceContext; meta = emptyMetadata
+       let senderEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = senderAccount
+             inputHoldingCids = [] -- sender input holdings are recorded separately
+             transferLegSides = [leg TransferEventsV2.SenderSide receiverAccount]
+             outputHoldingCids = [] -- sender change is recorded separately
+             observers = [sender]
+             extraArgs = emptyExtraArgs
+       let receiverEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = receiverAccount
+             inputHoldingCids = [] -- no receiver input holdings
+             transferLegSides = [leg TransferEventsV2.ReceiverSide senderAccount]
+             outputHoldingCids = [outputHoldingCid] -- the created output holding
+             observers = [out.owner]
+             extraArgs = emptyExtraArgs
+       let events
+             | out.owner == dso = [senderEvent] -- the DSO party does not need holding change events
+             | otherwise = [senderEvent, receiverEvent]
+       pure (createdAmulet, events)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1101 -> Splice.AmuletRules:1120:

```diff
+ executeTransfer eventLogCid config context dso t = do
- executeTransfer config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso t
-   csum <- summarizeAndValidateContext context dso t
    executeTransfer' config csum dso t
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1117 -> Splice.AmuletRules:1136:

```diff
    -- record outputs
+   (createdAmulets, senderChangeAmulet, outputEvents) <-
-   (createdAmulets, senderChangeAmulet) <-
      createTransferOutputs csum.openRoundNumber csum.config dso t.sender summary osum
...
  
+   -- create V2 tx history mint event for rewards if rewards were minted
+   let mintAmount =
+         summary.inputAppRewardAmount +
+         summary.inputValidatorRewardAmount +
+         summary.inputSvRewardAmount +
+         fromOptional 0.0 summary.inputUnclaimedActivityRecordAmount +
+         fromOptional 0.0 summary.inputDevelopmentFundAmount
+   let optMintLeg = do
+         guard (mintAmount > 0.0)
+         pure $ TransferEventsV2.TransferLegSide with
+           transferLegId = "mint"
+           side = TransferEventsV2.ReceiverSide
+           otherside = mintAccount
+           amount = mintAmount
+           instrumentId = amuletInstrumentIdName
+           meta = Metadata with
+             values =
+               TextMap.insert reasonMetaKey "mint rewards" $
+               rewardInputMeta.values
+   let senderInputOutputEvent = TransferEventsV2.EventLog_HoldingsChange with
+         admin = dso
+         account = basicAccount t.sender
+         inputHoldingCids = [ toInterfaceContractId cid | InputAmulet cid <- t.inputs ]
+         transferLegSides = optionalToList optMintLeg
+         outputHoldingCids = [ toInterfaceContractId cid | Some cid <- pure senderChangeAmulet ]
+         observers = [t.sender]
+         extraArgs = ExtraArgs with
+           context = emptyChoiceContext
+           meta = emptyMetadata
+ 
+   -- record all holdings changes grouped by account
+   forA_ csum.eventLogCid $ \eventLogCid ->
+     forA_ (mergeHoldingsChangeEventsByAccount (senderInputOutputEvent :: outputEvents)) $ \holdingChangeEvent ->
+       exercise eventLogCid holdingChangeEvent
+ 
    return TransferResult with
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1463 -> Splice.AmuletRules:1522:

```diff
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio; meta)
-     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
              lock
+             meta
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1599 -> Splice.AmuletRules:1696:

```diff
      maxNumLockHolders = c.maxNumLockHolders
+     tokenStandardMaxTTL = c.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1204 -> Splice.AmuletRules:1260:

```diff
+ summarizeAndValidateContext eventLogCid context dso tf = do
- summarizeAndValidateContext context dso tf = do
    -- check that the current mining round is open
...
      rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
+     eventLogCid = Some eventLogCid
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1484 -> Splice.AmuletRules:1544:

```diff
      require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
+     pure summary
-     -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
-     trace summary $ trace preprocessedOutputs $ trace inp $ pure summary
    where
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.AmuletConfig:transferConfigToTransferConfigV2, Splice.AmuletRules:computeTransferPreapprovalFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:scaleFees2, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndValidateContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:36 -> Splice.AmuletConfig:38:

```diff
      maxNumLockHolders = tc.maxNumLockHolders
+     tokenStandardMaxTTL = tc.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2206:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1563 -> Splice.AmuletRules:1622:

```diff
  createTransferOutputs currentOpenRound transferConfigAmulet dso sender summary preprocessedOutputs = do
+     let indices = [0 .. length preprocessedOutputs - 1]
+     outputAmuletsWithEvents <- mapA createOutput (zip indices preprocessedOutputs)
-     outputAmulets <- mapA createOutput preprocessedOutputs
      senderChangeAmulet <- createSenderChange
+     return (map fst outputAmuletsWithEvents, senderChangeAmulet, concatMap snd outputAmuletsWithEvents)
-     return (outputAmulets, senderChangeAmulet)
    where
...
              amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
+     createOutput (i, out) = do
-     createOutput out = do
        let amulet = Amulet with
...
              amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
+       createdAmulet <- case out.lock of
- 
-       case out.lock of
          None -> TransferResultAmulet <$> create amulet
          Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
+       -- compute tx history v2 events for this output
+       let outputHoldingCid = case createdAmulet of
+             TransferResultAmulet amuletCid -> toInterfaceContractId amuletCid
+             TransferResultLockedAmulet lockedAmuletCid -> toInterfaceContractId lockedAmuletCid
+             ExtCreatedAmulet _ -> error "unexpected: ExtCreatedAmulet used for transfer output"
+       let senderAccount = basicAccount sender
+       let receiverAccount
+             | out.owner == dso = burnAccount  -- transfers to the DSO party are always burns
+             | otherwise = basicAccount out.owner
+       let leg side otherside = TransferEventsV2.TransferLegSide with
+             transferLegId = "leg" <> show i
+             side
+             otherside
+             amount = out.amount
+             instrumentId = amuletInstrumentIdName
+             meta = fromOptional emptyMetadata out.meta
+       let emptyExtraArgs = ExtraArgs with context = emptyChoiceContext; meta = emptyMetadata
+       let senderEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = senderAccount
+             inputHoldingCids = [] -- sender input holdings are recorded separately
+             transferLegSides = [leg TransferEventsV2.SenderSide receiverAccount]
+             outputHoldingCids = [] -- sender change is recorded separately
+             observers = [sender]
+             extraArgs = emptyExtraArgs
+       let receiverEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = receiverAccount
+             inputHoldingCids = [] -- no receiver input holdings
+             transferLegSides = [leg TransferEventsV2.ReceiverSide senderAccount]
+             outputHoldingCids = [outputHoldingCid] -- the created output holding
+             observers = [out.owner]
+             extraArgs = emptyExtraArgs
+       let events
+             | out.owner == dso = [senderEvent] -- the DSO party does not need holding change events
+             | otherwise = [senderEvent, receiverEvent]
+       pure (createdAmulet, events)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1101 -> Splice.AmuletRules:1120:

```diff
+ executeTransfer eventLogCid config context dso t = do
- executeTransfer config context dso t = do
    -- compute summaries
+   csum <- summarizeAndValidateContext eventLogCid context dso t
-   csum <- summarizeAndValidateContext context dso t
    executeTransfer' config csum dso t
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1117 -> Splice.AmuletRules:1136:

```diff
    -- record outputs
+   (createdAmulets, senderChangeAmulet, outputEvents) <-
-   (createdAmulets, senderChangeAmulet) <-
      createTransferOutputs csum.openRoundNumber csum.config dso t.sender summary osum
...
  
+   -- create V2 tx history mint event for rewards if rewards were minted
+   let mintAmount =
+         summary.inputAppRewardAmount +
+         summary.inputValidatorRewardAmount +
+         summary.inputSvRewardAmount +
+         fromOptional 0.0 summary.inputUnclaimedActivityRecordAmount +
+         fromOptional 0.0 summary.inputDevelopmentFundAmount
+   let optMintLeg = do
+         guard (mintAmount > 0.0)
+         pure $ TransferEventsV2.TransferLegSide with
+           transferLegId = "mint"
+           side = TransferEventsV2.ReceiverSide
+           otherside = mintAccount
+           amount = mintAmount
+           instrumentId = amuletInstrumentIdName
+           meta = Metadata with
+             values =
+               TextMap.insert reasonMetaKey "mint rewards" $
+               rewardInputMeta.values
+   let senderInputOutputEvent = TransferEventsV2.EventLog_HoldingsChange with
+         admin = dso
+         account = basicAccount t.sender
+         inputHoldingCids = [ toInterfaceContractId cid | InputAmulet cid <- t.inputs ]
+         transferLegSides = optionalToList optMintLeg
+         outputHoldingCids = [ toInterfaceContractId cid | Some cid <- pure senderChangeAmulet ]
+         observers = [t.sender]
+         extraArgs = ExtraArgs with
+           context = emptyChoiceContext
+           meta = emptyMetadata
+ 
+   -- record all holdings changes grouped by account
+   forA_ csum.eventLogCid $ \eventLogCid ->
+     forA_ (mergeHoldingsChangeEventsByAccount (senderInputOutputEvent :: outputEvents)) $ \holdingChangeEvent ->
+       exercise eventLogCid holdingChangeEvent
+ 
    return TransferResult with
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2143 -> Splice.AmuletRules:2265:

```diff
+ payForTransferPreapproval reason dso provider context inputs expiresAt newExpiresAt = do
- payForTransferPreapproval dso provider context inputs expiresAt newExpiresAt = do
    amuletRules <- fetchChecked (ForDso dso) context.amuletRules
...
        (amuletPaid, _) <- computeTransferPreapprovalFee paidExtension configUsd context.context dso
+       (transferResult, meta) <- splitAndBurn (toInterfaceContractId context.amuletRules) provider amuletPaid inputs context.context dso reason
-       (transferResult, meta) <- splitAndBurn provider amuletPaid inputs context.context dso "renew TransferPreapproval"
        pure (amuletPaid, transferResult, meta)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1463 -> Splice.AmuletRules:1522:

```diff
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio; meta)
-     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
              lock
+             meta
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1599 -> Splice.AmuletRules:1696:

```diff
      maxNumLockHolders = c.maxNumLockHolders
+     tokenStandardMaxTTL = c.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2095 -> Splice.AmuletRules:2214:

```diff
+ splitAndBurn eventLogCid sender amount inputs context dso usage = do
- splitAndBurn sender amount inputs context dso usage = do
  -- do not create activity records as part of the transfer. We create a validator activity record for the full burn below.
...
          issueValidatorRewards = False
+   transferResult <- executeTransfer eventLogCid rewardsConfig context dso Transfer with
-   transferResult <- executeTransfer rewardsConfig context dso Transfer with
      sender
...
            lock = None
+           meta = Some (reasonToMeta ("split for " <> usage) emptyMetadata)
        ]
...
    amulet <- fetchAndArchive (ForOwner with dso; owner = sender) amuletCid
+   -- record event
+   logAmuletBurn dso eventLogCid sender amount usage (toInterfaceContractId amuletCid)
    -- create validator activity rewards over the whole burn.
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1204 -> Splice.AmuletRules:1260:

```diff
+ summarizeAndValidateContext eventLogCid context dso tf = do
- summarizeAndValidateContext context dso tf = do
    -- check that the current mining round is open
...
      rewardCalculationVersion = (.mintingVersion) <$> openRound.rewardConfig
+     eventLogCid = Some eventLogCid
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1484 -> Splice.AmuletRules:1544:

```diff
      require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
+     pure summary
-     -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
-     trace summary $ trace preprocessedOutputs $ trace inp $ pure summary
    where
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

Ledger effects changed. Added: exercise splice-api-token-transfer-events-v2:Splice.Api.Token.TransferEventsV2:EventLog.EventLog_HoldingsChange. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeExternalPartyTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:preprocessOutputs, Splice.AmuletRules:scaleFees2, Splice.AmuletRules:summarizeAndValidateExternalPartyContext, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1563 -> Splice.AmuletRules:1622:

```diff
  createTransferOutputs currentOpenRound transferConfigAmulet dso sender summary preprocessedOutputs = do
+     let indices = [0 .. length preprocessedOutputs - 1]
+     outputAmuletsWithEvents <- mapA createOutput (zip indices preprocessedOutputs)
-     outputAmulets <- mapA createOutput preprocessedOutputs
      senderChangeAmulet <- createSenderChange
+     return (map fst outputAmuletsWithEvents, senderChangeAmulet, concatMap snd outputAmuletsWithEvents)
-     return (outputAmulets, senderChangeAmulet)
    where
...
              amount = expiringAmount transferConfigAmulet.holdingFee summary.senderChangeAmount currentOpenRound
+     createOutput (i, out) = do
-     createOutput out = do
        let amulet = Amulet with
...
              amount = expiringAmount transferConfigAmulet.holdingFee out.amount currentOpenRound
+       createdAmulet <- case out.lock of
- 
-       case out.lock of
          None -> TransferResultAmulet <$> create amulet
          Some lock -> TransferResultLockedAmulet <$> create LockedAmulet with amulet; lock
+       -- compute tx history v2 events for this output
+       let outputHoldingCid = case createdAmulet of
+             TransferResultAmulet amuletCid -> toInterfaceContractId amuletCid
+             TransferResultLockedAmulet lockedAmuletCid -> toInterfaceContractId lockedAmuletCid
+             ExtCreatedAmulet _ -> error "unexpected: ExtCreatedAmulet used for transfer output"
+       let senderAccount = basicAccount sender
+       let receiverAccount
+             | out.owner == dso = burnAccount  -- transfers to the DSO party are always burns
+             | otherwise = basicAccount out.owner
+       let leg side otherside = TransferEventsV2.TransferLegSide with
+             transferLegId = "leg" <> show i
+             side
+             otherside
+             amount = out.amount
+             instrumentId = amuletInstrumentIdName
+             meta = fromOptional emptyMetadata out.meta
+       let emptyExtraArgs = ExtraArgs with context = emptyChoiceContext; meta = emptyMetadata
+       let senderEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = senderAccount
+             inputHoldingCids = [] -- sender input holdings are recorded separately
+             transferLegSides = [leg TransferEventsV2.SenderSide receiverAccount]
+             outputHoldingCids = [] -- sender change is recorded separately
+             observers = [sender]
+             extraArgs = emptyExtraArgs
+       let receiverEvent = TransferEventsV2.EventLog_HoldingsChange with
+             admin = dso
+             account = receiverAccount
+             inputHoldingCids = [] -- no receiver input holdings
+             transferLegSides = [leg TransferEventsV2.ReceiverSide senderAccount]
+             outputHoldingCids = [outputHoldingCid] -- the created output holding
+             observers = [out.owner]
+             extraArgs = emptyExtraArgs
+       let events
+             | out.owner == dso = [senderEvent] -- the DSO party does not need holding change events
+             | otherwise = [senderEvent, receiverEvent]
+       pure (createdAmulet, events)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1127:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1117 -> Splice.AmuletRules:1136:

```diff
    -- record outputs
+   (createdAmulets, senderChangeAmulet, outputEvents) <-
-   (createdAmulets, senderChangeAmulet) <-
      createTransferOutputs csum.openRoundNumber csum.config dso t.sender summary osum
...
  
+   -- create V2 tx history mint event for rewards if rewards were minted
+   let mintAmount =
+         summary.inputAppRewardAmount +
+         summary.inputValidatorRewardAmount +
+         summary.inputSvRewardAmount +
+         fromOptional 0.0 summary.inputUnclaimedActivityRecordAmount +
+         fromOptional 0.0 summary.inputDevelopmentFundAmount
+   let optMintLeg = do
+         guard (mintAmount > 0.0)
+         pure $ TransferEventsV2.TransferLegSide with
+           transferLegId = "mint"
+           side = TransferEventsV2.ReceiverSide
+           otherside = mintAccount
+           amount = mintAmount
+           instrumentId = amuletInstrumentIdName
+           meta = Metadata with
+             values =
+               TextMap.insert reasonMetaKey "mint rewards" $
+               rewardInputMeta.values
+   let senderInputOutputEvent = TransferEventsV2.EventLog_HoldingsChange with
+         admin = dso
+         account = basicAccount t.sender
+         inputHoldingCids = [ toInterfaceContractId cid | InputAmulet cid <- t.inputs ]
+         transferLegSides = optionalToList optMintLeg
+         outputHoldingCids = [ toInterfaceContractId cid | Some cid <- pure senderChangeAmulet ]
+         observers = [t.sender]
+         extraArgs = ExtraArgs with
+           context = emptyChoiceContext
+           meta = emptyMetadata
+ 
+   -- record all holdings changes grouped by account
+   forA_ csum.eventLogCid $ \eventLogCid ->
+     forA_ (mergeHoldingsChangeEventsByAccount (senderInputOutputEvent :: outputEvents)) $ \holdingChangeEvent ->
+       exercise eventLogCid holdingChangeEvent
+ 
    return TransferResult with
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1463 -> Splice.AmuletRules:1522:

```diff
    where
+     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio; meta)
-     preprocessOutput (TransferOutput with receiver; amount; lock; receiverFeeRatio)
        | not (0.0 <= receiverFeeRatio && receiverFeeRatio <= 1.0) =
...
              lock
+             meta
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1599 -> Splice.AmuletRules:1696:

```diff
      maxNumLockHolders = c.maxNumLockHolders
+     tokenStandardMaxTTL = c.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1236 -> Splice.AmuletRules:1294:

```diff
      dso = dso
+     config = transferConfigAmuletFromExternalPartyConfigState externalPartyConfigState
-     config = scaleFees2 (1.0 / externalPartyConfigState.amuletPrice) $ externalPartyConfigState.transferConfig
      featuredAppProvider
...
      rewardCalculationVersion = externalPartyConfigState.rewardCalculationVersion
+     eventLogCid = Some (toInterfaceContractId context.externalPartyConfigState)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1484 -> Splice.AmuletRules:1544:

```diff
      require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
+     pure summary
-     -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
-     trace summary $ trace preprocessedOutputs $ trace inp $ pure summary
    where
```

## MEDIUM - guard-removed - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules, choice `ExternalPartyAmuletRules_ExpireAmuletAllocations``

Fewer checks in the choice body (assertions or aborts): 4 before, 2 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.ExternalPartyAmuletRules:44 -> Splice.ExternalPartyAmuletRules:59:

```diff
        do
+         requireMatchExpected ("expectedDso", expectedDso) dso
-         require ("expectedDso " <> show expectedDso <> " matches the dsoParty " <> show dso) (expectedDso == dso)
  
+         results <- forA inputs $ \input -> do
-         individualResults <- forA inputs $ \input -> do
            alloc <- fetchButArchiveLater (ForDso with dso) input.allocationCid
+           requireMatchExpected ("allocation.transferLeg.instrumentId.admin", alloc.allocation.transferLeg.instrumentId.admin) dso
-           require "The dsoParty must be the admin of the allocation's instrument" (alloc.allocation.transferLeg.instrumentId.admin == dso)
- 
            exercise input.allocationCid AmuletAllocation_DsoExpire with
...
  
+         pure ExternalPartyAmuletRules_ExpireAmuletAllocationsResult with results
-         pure ExternalPartyAmuletRules_ExpireAmuletAllocationsResult with
-           results = individualResults
```

## MEDIUM - ensure-changed - `Splice.ExternalPartyAmuletRules:TransferCommand`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.20 -> 0.1.21, Splice.ExternalPartyAmuletRules:223:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.ExternalPartyConfigState:ExternalPartyConfigState`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:68:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.Round:OpenMiningRound`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.20 -> 0.1.21, Splice.Round:39:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet.RewardAccountingV2:ProcessRewardsV2, choice `ProcessRewardsV2_ProcessBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet.CryptoHash:hashText, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.Amulet.CryptoHash:101:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletAllocation:AmuletAllocation, choice `AmuletAllocation_DsoExpire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet.TwoStepTransfer:abortTwoStepTransfer, Splice.AmuletAllocation:unlockAmuletAllocation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.Amulet.TwoStepTransfer:129 -> Splice.Amulet.TwoStepTransfer:132:

```diff
+ abortTwoStepTransfer lockExpiresAt lockedAmuletCid extraArgs = do
+   optExpireLockedAmulet <- lookupFromContextU @Bool extraArgs.context expireLockKey
+   -- if the key is not set, then we are likely chaining within a Daml transaction, which means
+   -- the amulet is still locked and should be unlocked.
+   let expireLockedAmulet = fromOptional True optExpireLockedAmulet
- abortTwoStepTransfer TwoStepTransfer{..} lockedAmuletCid extraArgs = do
-   expireLockedAmulet <- getFromContextU @Bool extraArgs.context expireLockKey
    if expireLockedAmulet
...
      else do
+       -- the choice context is telling us to not expire the lock, this is OK if the lock expired
+       assertDeadlineExceeded "lockedAmulet.lockExpiresAt" lockExpiresAt
-       -- scan is telling us to not expire the lock, this is OK if the transfer expired
-       assertDeadlineExceeded transferBeforeDeadline transferBefore
        pure []
```

Version 0.1.20 -> 0.1.21, Splice.AmuletAllocation:119 -> Splice.AmuletAllocation:190:

```diff
  unlockAmuletAllocation amuletAllocation extraArgs = do
+   let expiresAt = fromOptional amuletAllocation.allocation.settlement.settleBefore amuletAllocation.expiresAt
+   abortTwoStepTransfer expiresAt amuletAllocation.lockedAmulet extraArgs
-   let twoStepTransfer = allocationToTwoStepTransfer amuletAllocation.allocation
-   abortTwoStepTransfer twoStepTransfer amuletAllocation.lockedAmulet extraArgs
```

## LOW - template-added - `Splice.AmuletAllocationV2:AmuletAllocationV2`

New template. Review it like a new package.

## LOW - template-added - `Splice.AmuletEventLog:AmuletEventLog`

New template. Review it like a new package.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AdvanceOpenMiningRounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:483:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BootstrapExternalPartyConfigState``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletConfig:transferConfigToTransferConfigV2, Splice.AmuletRules:bootstrapExternalPartyConfigState, Splice.AmuletRules:validateOpenMiningRoundTriple, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:36 -> Splice.AmuletConfig:38:

```diff
      maxNumLockHolders = tc.maxNumLockHolders
+     tokenStandardMaxTTL = tc.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:2325:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1008:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ClaimExpiredRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:636:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:905:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_Tap``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:358 -> Splice.AmuletRules:376:

```diff
          require "Tapped amount <= 100,000,000" (amount <= 100000000.0)
+         let reason = Some "tapped DevNet faucet"
          result <- exercise self AmuletRules_Mint with ..
...
            amuletSum = result.amuletSum
+           meta = Some (simpleHoldingTxMeta TxKind_Mint reason None)
-           meta = Some (simpleHoldingTxMeta TxKind_Mint (Some "tapped faucet") None)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:309:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_StartIssuing``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:560:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:865:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UpdateExternalPartyConfigStates``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletConfig:transferConfigToTransferConfigV2, Splice.AmuletRules:validateOpenMiningRoundTriple, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.AmuletConfig:36 -> Splice.AmuletConfig:38:

```diff
      maxNumLockHolders = tc.maxNumLockHolders
+     tokenStandardMaxTTL = tc.tokenStandardMaxTTL
```

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1008:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Send``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.AmuletRules:1952 -> Splice.AmuletRules:2056:

```diff
        do
+          let metaWithDescription = optional identity reasonToMeta description emptyMetadata
           assertWithinDeadline "TransferPreapproval.expiresAt" expiresAt
...
                   lock = None
+                  meta = Some metaWithDescription
               ]
...
               -- If needed, we could extend preapprovals to track beneficiaries later.
+ 
+          -- V1 history metdata also reports on mints that happened in the transfer
+          let metaForV1History = fromOptional mempty transferResult.meta <> metaWithDescription
-          let meta = optionalMetadata reasonMetaKey identity description (fromOptional emptyMetadata transferResult.meta).values
           -- strip metadata to avoid duplicating it needlessly
           let result = transferResult with meta = None
+          pure (TransferPreapproval_SendResult result (Some metaForV1History))
-          pure (TransferPreapproval_SendResult result (Some (Metadata meta)))
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_RecordValidatorLivenessActivity``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.ValidatorLicense:81:

```text
(source text identical: the difference comes from code this function calls)
```

