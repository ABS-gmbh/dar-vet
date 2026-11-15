# dar-vet: splice-dso-governance 0.1.24 -> 0.1.25

Old package ID `4974c654485d4ecaa6b5caf8ef3c2679efa8195c4b50d4965a8fff1b72e8efa4`, new package ID `dfe102514acd41f79945b8363ca84d8243be97b2b235522ce77c754a36bb8479`.

**12 findings:** 4 HIGH, 5 MEDIUM, 3 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveDryRunRewardAccountingV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_ArchiveDryRunRewardAccountingV2.

Version 0.1.25, Splice.DsoRules:1470:

```daml
    nonconsuming choice DsoRules_ArchiveDryRunRewardAccountingV2 : DsoRules_ArchiveDryRunRewardAccountingV2Result
      with
        amuletRulesCid : ContractId AmuletRules
        choiceArg : AmuletRules_ArchiveDryRunRewardAccountingV2
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        result <- exercise amuletRulesCid choiceArg
        return DsoRules_ArchiveDryRunRewardAccountingV2Result with
          result
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ClaimExpiredRewardsV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_ClaimExpiredRewardsV2.

Version 0.1.25, Splice.DsoRules:1433:

```daml
    nonconsuming choice DsoRules_ClaimExpiredRewardsV2 : DsoRules_ClaimExpiredRewardsV2Result
      with
        amuletRulesCid : ContractId AmuletRules
        choiceArg : AmuletRules_ClaimExpiredRewardsV2
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        result <- exercise amuletRulesCid choiceArg
        return DsoRules_ClaimExpiredRewardsV2Result with
          result
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ProcessRewardsV2_ProcessBatch``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.Amulet.RewardAccountingV2:ProcessRewardsV2.ProcessRewardsV2_ProcessBatch.

Version 0.1.25, Splice.DsoRules:1445:

```daml
    nonconsuming choice DsoRules_ProcessRewardsV2_ProcessBatch : DsoRules_ProcessRewardsV2_ProcessBatchResult
      with
        processRewardsCid : ContractId RewardAccountingV2.ProcessRewardsV2
        choiceArg : RewardAccountingV2.ProcessRewardsV2_ProcessBatch
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        result <- exercise processRewardsCid choiceArg
        return DsoRules_ProcessRewardsV2_ProcessBatchResult with
          result
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_UnhideRewardCouponsV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_UnhideRewardCouponsV2.

Version 0.1.25, Splice.DsoRules:1457:

```daml
    nonconsuming choice DsoRules_UnhideRewardCouponsV2 : DsoRules_UnhideRewardCouponsV2Result
      with
        amuletRulesCid : ContractId AmuletRules
        choiceArg : AmuletRules_UnhideRewardCouponsV2
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        result <- exercise amuletRulesCid choiceArg
        return DsoRules_UnhideRewardCouponsV2Result with
          result
```

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.24 -> 0.1.25, Splice.DSO.DecentralizedSynchronizer:80:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.24 -> 0.1.25, Splice.DsoRules:382:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Ledger effects changed. Added: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_StartProcessingRewardsV2. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:actionRequiringConfirmationEffectiveAt, Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.DsoRules:1760 -> Splice.DsoRules:1829:

```diff
          CRARC_MiningRound_StartIssuing _ -> None
+         CRARC_StartProcessingRewardsV2 _ -> None
      ARC_DsoRules with .. -> None
```

Version 0.1.24 -> 0.1.25, Splice.DsoRules:1689 -> Splice.DsoRules:1757:

```diff
            CRARC_UpdateFutureAmuletConfigSchedule choiceArg -> void $ exercise amuletRulesCid choiceArg
+           CRARC_StartProcessingRewardsV2 choiceArg -> void $ exercise amuletRulesCid choiceArg
    ARC_DsoRules with .. -> do
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Ledger effects changed. Added: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_StartProcessingRewardsV2. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.24 -> 0.1.25, Splice.DsoRules:1689 -> Splice.DsoRules:1757:

```diff
            CRARC_UpdateFutureAmuletConfigSchedule choiceArg -> void $ exercise amuletRulesCid choiceArg
+           CRARC_StartProcessingRewardsV2 choiceArg -> void $ exercise amuletRulesCid choiceArg
    ARC_DsoRules with .. -> do
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.24 -> 0.1.25, Splice.DsoRules:421:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - dependency-behaviour-changed - `(package)`

2 expressions are unchanged in this package but call dependency code that changed.

- Changed dependencies: splice-amulet 0.1.18 -> 0.1.19, splice-amulet-name-service 0.1.19 -> 0.1.20, splice-wallet-payments 0.1.18 -> 0.1.19.
- Affected: Splice.DsoRules:DsoRules DsoRules_AddConfirmedSv (body); Splice.DsoRules:DsoRules DsoRules_ReceiveSvRewardCoupon (body).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.DsoRules:781:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.24 -> 0.1.25, Splice.DsoRules:1537:

```text
(source text identical: the difference comes from code this function calls)
```

