# dar-vet: splice-dso-governance 0.1.15 -> 0.1.16

Old package ID `8ef106efc4df1812572a24bd3f41218e69580e15606b636b1632223ea08434f9`, new package ID `5c96913f6c441129f7df33f402efe737194b90a69784521cb6841c499479fb18`.

**5 findings:** 0 HIGH, 3 MEDIUM, 2 LOW.

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_PruneAmuletConfigSchedule``

Ledger effects changed. Added: none. Removed: create splice-amulet:Splice.AmuletRules:AmuletRules, exercise splice-amulet:Splice.AmuletRules:AmuletRules.Archive, fetch splice-amulet:Splice.AmuletRules:AmuletRules.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.15 -> 0.1.16, Splice.DsoRules:1362 -> Splice.DsoRules:1363:

```diff
        do _ <- getAndValidateSvParty this sv
+          deprecatedChoice "splice-dso-governance" "0.1.16" "DsoRules_PruneAmuletConfigSchedule"
-          now <- getTime
-          amuletRules <- fetchAndArchive (ForDso dso) amuletRulesCid
-          case pruneAtLeastOne now amuletRules.configSchedule of
-            None -> abort ("Must have at least one scheduled config in futureValues with a timestamp <= " <> show now)
-            Some newSchedule -> do
-              newAmuletRules <- create amuletRules with configSchedule = newSchedule
-              pure $ DsoRules_PruneAmuletConfigScheduleResult newAmuletRules
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_PruneAmuletConfigSchedule``

Fewer checks in the choice body (assertions or aborts): 5 before, 4 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.15 -> 0.1.16, Splice.DsoRules:1362 -> Splice.DsoRules:1363:

```diff
        do _ <- getAndValidateSvParty this sv
+          deprecatedChoice "splice-dso-governance" "0.1.16" "DsoRules_PruneAmuletConfigSchedule"
-          now <- getTime
-          amuletRules <- fetchAndArchive (ForDso dso) amuletRulesCid
-          case pruneAtLeastOne now amuletRules.configSchedule of
-            None -> abort ("Must have at least one scheduled config in futureValues with a timestamp <= " <> show now)
-            Some newSchedule -> do
-              newAmuletRules <- create amuletRules with configSchedule = newSchedule
-              pure $ DsoRules_PruneAmuletConfigScheduleResult newAmuletRules
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.15 -> 0.1.16, Splice.DsoRules:374:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.15 -> 0.1.16, Splice.DsoRules:741:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.15 -> 0.1.16, Splice.DsoRules:790:

```text
(source text identical: the difference comes from code this function calls)
```

