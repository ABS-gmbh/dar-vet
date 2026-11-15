# dar-vet: splice-dso-governance 0.1.11 -> 0.1.12

Old package ID `a44a2b811de7183661d4e7835a1c60f9055eac090a35b2236dd039caebc09716`, new package ID `0c0b2c4c56a0f30a7d8ce425cb072f3067a4413cbad59fe9bc97c99defef7c2a`.

**24 findings:** 1 HIGH, 1 MEDIUM, 22 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkers``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_ConvertFeaturedAppActivityMarkers.

Version 0.1.12, Splice.DsoRules:1316:

```daml
    nonconsuming choice DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkers : DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkersResult
      with
        amuletRulesCid : ContractId AmuletRules
        argument : AmuletRules_ConvertFeaturedAppActivityMarkers
      controller dsoDelegate
      do result <- exercise amuletRulesCid argument
         pure DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkersResult with result
```

## MEDIUM - dependency-behaviour-changed - `(package)`

3 expressions are unchanged in this package but call dependency code that changed, including 3 that decide authority (controllers, observers, signatories).

- Changed dependencies: daml-stdlib 3.0.0.20240318.12913.0 -> 3.3.0.20250502.13767.0, splice-amulet 0.1.8 -> 0.1.9, splice-amulet-name-service 0.1.8 -> 0.1.9, splice-util 0.1.2 -> 0.1.3, splice-wallet-payments 0.1.8 -> 0.1.9.
- Affected: Splice.DsoBootstrap:DsoBootstrap template (ensure clause); Splice.DsoRules:Confirmation template (ensure clause); Splice.DsoRules:VoteRequest template (ensure clause).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AddConfirmedSv``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AdvanceOpenMiningRounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DSO.AmuletPrice:fetchMedianAmuletPrice, Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.11 -> 0.1.12, Splice.DSO.AmuletPrice:36:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveOutdatedElectionRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveSvOnboardingRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ElectDsoDelegate``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSvOnboardingConfirmed``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_GarbageCollectAmuletPriceVotes``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeSvRewardState``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeValidatorLicense``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_OffboardSv``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.DsoRules:456:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_PruneAmuletConfigSchedule``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ReceiveSvRewardCoupon``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_RequestVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.DsoRules:669:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_RevokeFeaturedAppRight``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SetSynchronizerNodeConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DsoRules:getSvInfo, Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.11 -> 0.1.12, Splice.DsoRules:1492:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SubmitStatusReport``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_UpdateAmuletPriceVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_UpdateSvRewardWeight``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.DsoRules:901:

```text
(source text identical: the difference comes from code this function calls)
```

