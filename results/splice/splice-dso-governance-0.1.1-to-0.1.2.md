# dar-vet: splice-dso-governance 0.1.1 -> 0.1.2

Old package ID `332087889d9643fa24961edde7459a08d9edfb4fb737929cd4c79b952bc5819f`, new package ID `4206e127be8b111ac84bd7f98bd9dbf03ed489f1642b46ab31a46ee6d688e7e8`.

**2 findings:** 1 HIGH, 0 MEDIUM, 1 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeSvRewardState``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.DSO.SvState:SvRewardState, exercise self:Splice.DSO.SvState:SvRewardState.Archive, fetch self:Splice.DSO.SvState:SvRewardState.

Version 0.1.2, Splice.DsoRules:1137:

```daml
    nonconsuming choice DsoRules_MergeSvRewardState : DsoRules_MergeSvRewardStateResult
      with
        svName : Text
        rewardStateCids : [ContractId SvRewardState]
      controller dsoDelegate
      do require "Number of SvRewardState contracts to merge is >= 2" (length rewardStateCids >= 2)
         rewardStates <- forA rewardStateCids $ fetchAndArchive (ForSv this.dso svName)
         -- We don't attempt to merge the fields in the reward state since there is no good
         -- way to do so and none of the fields other than lastRoundCollected are crucial and for that
         -- taking the max is the correct merge.
         cid <- create $ maximumOn (\state -> state.state.lastRoundCollected) rewardStates
         pure $ DsoRules_MergeSvRewardStateResult cid
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AddSv``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DsoRules:dsoRules_addSv, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.1 -> 0.1.2, Splice.DsoRules:1300 -> Splice.DsoRules:1324:

```diff
    -- create per-sv contracts if they have never been onboarded
+   unless (svHasBeenOnboardedBefore newSvName this) $
-   unless (newSvParty `Map.member` this.offboardedSvs) $
      createPerSvContracts this arg
```

