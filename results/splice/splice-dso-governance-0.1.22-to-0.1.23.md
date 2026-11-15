# dar-vet: splice-dso-governance 0.1.22 -> 0.1.23

Old package ID `5c28530209b9ab37c5f187132cd826709bb18b0efe28411488ab750870414738`, new package ID `0c94a036ac5168a1dee26b435838e062f0d2f47d6eac49303978228ae559edb9`.

**20 findings:** 7 HIGH, 8 MEDIUM, 5 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_Amulet_ExpireTransferInstructions``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_Amulet_ExpireTransferInstructions.

Version 0.1.23, Splice.DsoRules:1147:

```daml
    nonconsuming choice DsoRules_Amulet_ExpireTransferInstructions : DsoRules_Amulet_ExpireTransferInstructionsResult
      with
        amuletRulesCid : ContractId AmuletRules
        transferInsts : AmuletRules_Amulet_ExpireTransferInstructions
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        require ("expectedDso " <> show transferInsts.expectedDso <> " matches the actualDso " <> show dso) (transferInsts.expectedDso == dso)
        res <- exercise amuletRulesCid (transferInsts with expectedDso = dso)
        pure DsoRules_Amulet_ExpireTransferInstructionsResult with result = res
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_Amulet_ExpireV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.Amulet:Amulet.Amulet_ExpireV2.

Version 0.1.23, Splice.DsoRules:1167:

```daml
    nonconsuming choice DsoRules_Amulet_ExpireV2 : DsoRules_Amulet_ExpireV2Result
      with
        cid : ContractId Amulet
        choiceArg : Amulet_ExpireV2
        sv : Optional Party
      controller sv
      do
        _ <- getAndValidateSvParty this sv
        result <- exercise cid choiceArg
        return DsoRules_Amulet_ExpireV2Result with
          expireSum = result.expireSum
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_BootstrapExternalPartyConfigState``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise self:Splice.DsoRules:BootstrapExternalPartyConfigStateInstruction.Archive, exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_BootstrapExternalPartyConfigState, fetch self:Splice.DsoRules:BootstrapExternalPartyConfigStateInstruction.

Version 0.1.23, Splice.DsoRules:1634:

```daml
    nonconsuming choice DsoRules_BootstrapExternalPartyConfigState : DsoRules_BootstrapExternalPartyConfigStateResult
      with
        amuletRulesCid : ContractId AmuletRules
        instructionCid : ContractId BootstrapExternalPartyConfigStateInstruction
        openMiningRoundTriple : OpenMiningRoundTriple
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        _ <- fetchAndArchive (ForDso dso) instructionCid
        _ <- exercise amuletRulesCid AmuletRules_BootstrapExternalPartyConfigState with
          openMiningRoundTriple
          expectedDso = dso
        pure DsoRules_BootstrapExternalPartyConfigStateResult
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_CreateBootstrapExternalPartyConfigStateInstruction``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.DsoRules:BootstrapExternalPartyConfigStateInstruction.

Version 0.1.23, Splice.DsoRules:1628:

```daml
    nonconsuming choice DsoRules_CreateBootstrapExternalPartyConfigStateInstruction : DsoRules_CreateBootstrapExternalPartyConfigStateInstructionResult
      controller dso
      do
        instructionCid <- create BootstrapExternalPartyConfigStateInstruction with dso
        pure (DsoRules_CreateBootstrapExternalPartyConfigStateInstructionResult instructionCid)
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireAmuletAllocations``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules.ExternalPartyAmuletRules_ExpireAmuletAllocations.

Version 0.1.23, Splice.DsoRules:1132:

```daml
    nonconsuming choice DsoRules_ExpireAmuletAllocations : DsoRules_ExpireAmuletAllocationsResult
      with
        extAmuletRulesCid : ContractId ExternalPartyAmuletRules
        expireAllocations : ExternalPartyAmuletRules_ExpireAmuletAllocations
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)

        require ("expectedDso " <> show expireAllocations.expectedDso <> " matches the actualDso " <> show dso) (expireAllocations.expectedDso == dso)

        res <- exercise extAmuletRulesCid (expireAllocations with expectedDso = dso)

        pure DsoRules_ExpireAmuletAllocationsResult with result = res
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_LockedAmulet_ExpireAmuletV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.Amulet:LockedAmulet.LockedAmulet_ExpireAmuletV2.

Version 0.1.23, Splice.DsoRules:1191:

```daml
    nonconsuming choice DsoRules_LockedAmulet_ExpireAmuletV2 : DsoRules_LockedAmulet_ExpireAmuletV2Result
      with
        cid : ContractId LockedAmulet
        choiceArg : LockedAmulet_ExpireAmuletV2
        sv : Optional Party
      controller sv
      do
        _ <- getAndValidateSvParty this sv
        result <- exercise cid choiceArg
        return DsoRules_LockedAmulet_ExpireAmuletV2Result with
          expireSum = result.expireSum
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_UpdateExternalPartyConfigStates``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_UpdateExternalPartyConfigStates.

Version 0.1.23, Splice.DsoRules:1224:

```daml
    nonconsuming choice DsoRules_UpdateExternalPartyConfigStates : DsoRules_UpdateExternalPartyConfigStatesResult
      with
        amuletRulesCid : ContractId AmuletRules
        externalPartyConfigStateCid0 : ContractId ExternalPartyConfigState
        externalPartyConfigStateCid1 : ContractId ExternalPartyConfigState
        openMiningRoundTriple : OpenMiningRoundTriple
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        result <- exercise amuletRulesCid AmuletRules_UpdateExternalPartyConfigStates with ..
        return DsoRules_UpdateExternalPartyConfigStatesResult with
          newExternalPartyConfigStateCid = result.newExternalPartyConfigStateCid
```

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.22 -> 0.1.23, Splice.DSO.DecentralizedSynchronizer:88:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.22 -> 0.1.23, Splice.DsoRules:365:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:DsoRules`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.22 -> 0.1.23, Splice.DsoRules:489:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_Amulet_Expire``

Ledger effects changed. Added: none. Removed: exercise splice-amulet:Splice.Amulet:Amulet.Amulet_Expire.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.DsoRules:1101 -> Splice.DsoRules:1159:

```diff
        controller sv
+       do deprecatedChoice "splice-dso-governance" "0.1.23" "DsoRules_Amulet_Expire"
-       do
-         _ <- getAndValidateSvParty this sv
-         result <- exercise cid choiceArg
-         return DsoRules_Amulet_ExpireResult with
-           expireSum = result.expireSum
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_Amulet_Expire``

Fewer checks in the choice body (assertions or aborts): 3 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.DsoRules:1101 -> Splice.DsoRules:1159:

```diff
        controller sv
+       do deprecatedChoice "splice-dso-governance" "0.1.23" "DsoRules_Amulet_Expire"
-       do
-         _ <- getAndValidateSvParty this sv
-         result <- exercise cid choiceArg
-         return DsoRules_Amulet_ExpireResult with
-           expireSum = result.expireSum
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateBootstrapExternalPartyConfigStateInstruction. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.DsoRules:1567 -> Splice.DsoRules:1681:

```diff
        SRARC_CreateUnallocatedUnclaimedActivityRecord choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateBootstrapExternalPartyConfigStateInstruction choiceArg -> void $ exercise dsoRulesCid choiceArg
    ARC_AnsEntryContext with .. -> do
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateBootstrapExternalPartyConfigStateInstruction. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.22 -> 0.1.23, Splice.DsoRules:1567 -> Splice.DsoRules:1681:

```diff
        SRARC_CreateUnallocatedUnclaimedActivityRecord choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateBootstrapExternalPartyConfigStateInstruction choiceArg -> void $ exercise dsoRulesCid choiceArg
    ARC_AnsEntryContext with .. -> do
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.22 -> 0.1.23, Splice.DsoRules:404:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoBootstrap:DsoBootstrap, choice `DsoBootstrap_Bootstrap``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.DsoBootstrap:42 -> Splice.DsoBootstrap:42:

```diff
            isDevNet
+           contractStateSchemaVersion = None
          _ <- create ExternalPartyAmuletRules with dso
```

## LOW - template-added - `Splice.DsoRules:BootstrapExternalPartyConfigStateInstruction`

New template. Review it like a new package.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.DsoRules:749:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ClaimExpiredRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.DsoRules:1253 -> Splice.DsoRules:1345:

```diff
        do
+         _ <- getAndValidateSvParty this sv
+ 
          -- TODO(#2025) Deprecate AmuletRules_ClaimExpiredRewards. We did not do that in the same change
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.22 -> 0.1.23, Splice.DsoRules:1461:

```text
(source text identical: the difference comes from code this function calls)
```

