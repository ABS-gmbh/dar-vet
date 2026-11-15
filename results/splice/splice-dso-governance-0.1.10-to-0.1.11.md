# dar-vet: splice-dso-governance 0.1.10 -> 0.1.11

Old package ID `1fa33994d8ae60d7839042ec8e0c86b5da2c725f1576a161fbf6d38e259f8c66`, new package ID `a44a2b811de7183661d4e7835a1c60f9055eac090a35b2236dd039caebc09716`.

**9 findings:** 0 HIGH, 6 MEDIUM, 3 LOW.

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.10 -> 0.1.11, Splice.DsoRules:313:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Fewer checks in the choice body (assertions or aborts): 6 before, 5 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.DsoRules:694 -> Splice.DsoRules:703:

```diff
          -- validate and archive request
-         now <- getTime
          request <- fetchAndArchive (ForDso with dso) requestCid
-         require "request is open" (now < request.voteBefore)
          -- store vote
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Ledger effects changed. Added: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_SetConfig. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:actionRequiringConfirmationEffectiveAt, Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.DsoRules:1399 -> Splice.DsoRules:1459:

```diff
        case amuletRulesAction of
+         CRARC_SetConfig _ -> None
          CRARC_AddFutureAmuletConfigSchedule choiceArg -> Some choiceArg.newScheduleItem._1
```

Version 0.1.10 -> 0.1.11, Splice.DsoRules:1284 -> Splice.DsoRules:1343:

```diff
            CRARC_MiningRound_Archive choiceArg -> void $ exercise amuletRulesCid choiceArg
+           CRARC_SetConfig choiceArg -> void $ exercise amuletRulesCid choiceArg
            CRARC_AddFutureAmuletConfigSchedule choiceArg -> void $ exercise amuletRulesCid choiceArg
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Fewer checks in the choice body (assertions or aborts): 7 before, 6 now.

- The change is inside the helper function(s) Splice.DsoRules:actionRequiringConfirmationEffectiveAt, Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.DsoRules:1399 -> Splice.DsoRules:1459:

```diff
        case amuletRulesAction of
+         CRARC_SetConfig _ -> None
          CRARC_AddFutureAmuletConfigSchedule choiceArg -> Some choiceArg.newScheduleItem._1
```

Version 0.1.10 -> 0.1.11, Splice.DsoRules:1284 -> Splice.DsoRules:1343:

```diff
            CRARC_MiningRound_Archive choiceArg -> void $ exercise amuletRulesCid choiceArg
+           CRARC_SetConfig choiceArg -> void $ exercise amuletRulesCid choiceArg
            CRARC_AddFutureAmuletConfigSchedule choiceArg -> void $ exercise amuletRulesCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Ledger effects changed. Added: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_SetConfig. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.DsoRules:1284 -> Splice.DsoRules:1343:

```diff
            CRARC_MiningRound_Archive choiceArg -> void $ exercise amuletRulesCid choiceArg
+           CRARC_SetConfig choiceArg -> void $ exercise amuletRulesCid choiceArg
            CRARC_AddFutureAmuletConfigSchedule choiceArg -> void $ exercise amuletRulesCid choiceArg
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.10 -> 0.1.11, Splice.DsoRules:377 -> Splice.DsoRules:354:

```diff
+     ensure all (voteBefore <=) targetEffectiveAt
-   deriving(Eq, Show)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.DsoRules:1237:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_RequestVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.DsoRules:658 -> Splice.DsoRules:665:

```diff
          voteRequestTimeout : Optional RelTime
+         targetEffectiveAt : Optional Time
        controller requester
...
            trackingCid = None
+           targetEffectiveAt
          return DsoRules_RequestVoteResult with ..
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.DsoRules:829 -> Splice.DsoRules:884:

```diff
          newConfig : DsoRulesConfig
+         baseConfig: Optional DsoRulesConfig
        controller dso
...
          -- TODO(M3-57): sanity check the newConfig to avoid locking up operations, e.g., by setting `maxTextLength = -1`
+         newDsoRules <- case baseConfig of
+           None -> create this with config = newConfig
+           Some baseConfig -> do
+              create this with config = patch newConfig baseConfig this.config
-         newDsoRules <- create this with config = newConfig
          return DsoRules_SetConfigResult with ..
```

