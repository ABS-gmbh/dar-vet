# dar-vet: splice-dso-governance 0.1.4 -> 0.1.5

Old package ID `dc24fd18b4d151cd1e0ff6bfb7438bafb2f50fe076d0f16f50565e60b153a0be`, new package ID `9e3ca1d22ad495dfabf3d61acae3dc1a7718f527f02092280b58cf69edfdc84c`.

**2 findings:** 1 HIGH, 0 MEDIUM, 1 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_PruneAmuletConfigSchedule``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create splice-amulet:Splice.AmuletRules:AmuletRules, exercise splice-amulet:Splice.AmuletRules:AmuletRules.Archive, fetch splice-amulet:Splice.AmuletRules:AmuletRules.

Version 0.1.5, Splice.DsoRules:1175:

```daml
    nonconsuming choice DsoRules_PruneAmuletConfigSchedule : DsoRules_PruneAmuletConfigScheduleResult
      with
        amuletRulesCid : ContractId AmuletRules
      controller dsoDelegate
      do now <- getTime
         amuletRules <- fetchAndArchive (ForDso dso) amuletRulesCid
         case pruneAtLeastOne now amuletRules.configSchedule of
           None -> abort ("Must have at least one scheduled config in futureValues with a timestamp <= " <> show now)
           Some newSchedule -> do
             newAmuletRules <- create amuletRules with configSchedule = newSchedule
             pure $ DsoRules_PruneAmuletConfigScheduleResult newAmuletRules
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.4 -> 0.1.5, Splice.DsoRules:694:

```text
(source text identical: the difference comes from code this function calls)
```

