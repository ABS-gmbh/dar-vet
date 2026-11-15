# dar-vet: splice-amulet 0.1.11 -> 0.1.12

Old package ID `9824927cdb455f833867b74c01cffcd8cb8cc5edd4d2273cea1329b708ef3ce5`, new package ID `95a88ff9ffd509e097802ecf3bbd58c83a5dff408e439cca4e2105ebd2cd0760`.

**11 findings:** 0 HIGH, 6 MEDIUM, 5 LOW.

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AddFutureAmuletConfigSchedule``

Ledger effects changed. Added: none. Removed: create self:Splice.AmuletRules:AmuletRules.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:603 -> Splice.AmuletRules:601:

```diff
        do
+         deprecatedChoice "splice-amulet" "0.1.12" "AmuletRules_AddFutureAmuletConfigSchedule"
-         now <- getTime
-         require "The insertion is scheduled in the future" (now < newScheduleItem._1)
-         newAmuletRules <- create this with configSchedule = prune now (insert newScheduleItem this.configSchedule)
-         return AmuletRules_AddFutureAmuletConfigScheduleResult with ..
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AddFutureAmuletConfigSchedule``

Fewer checks in the choice body (assertions or aborts): 3 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:603 -> Splice.AmuletRules:601:

```diff
        do
+         deprecatedChoice "splice-amulet" "0.1.12" "AmuletRules_AddFutureAmuletConfigSchedule"
-         now <- getTime
-         require "The insertion is scheduled in the future" (now < newScheduleItem._1)
-         newAmuletRules <- create this with configSchedule = prune now (insert newScheduleItem this.configSchedule)
-         return AmuletRules_AddFutureAmuletConfigScheduleResult with ..
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_RemoveFutureAmuletConfigSchedule``

Ledger effects changed. Added: none. Removed: create self:Splice.AmuletRules:AmuletRules.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:615 -> Splice.AmuletRules:610:

```diff
        do
+         deprecatedChoice "splice-amulet" "0.1.12" "AmuletRules_RemoveFutureAmuletConfigSchedule"
-         now <- getTime
-         require "The removal is scheduled in the future" (now < scheduleTime)
-         newAmuletRules <- create this with configSchedule = prune now (remove scheduleTime this.configSchedule)
-         return AmuletRules_RemoveFutureAmuletConfigScheduleResult with ..
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_RemoveFutureAmuletConfigSchedule``

Fewer checks in the choice body (assertions or aborts): 3 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:615 -> Splice.AmuletRules:610:

```diff
        do
+         deprecatedChoice "splice-amulet" "0.1.12" "AmuletRules_RemoveFutureAmuletConfigSchedule"
-         now <- getTime
-         require "The removal is scheduled in the future" (now < scheduleTime)
-         newAmuletRules <- create this with configSchedule = prune now (remove scheduleTime this.configSchedule)
-         return AmuletRules_RemoveFutureAmuletConfigScheduleResult with ..
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UpdateFutureAmuletConfigSchedule``

Ledger effects changed. Added: none. Removed: create self:Splice.AmuletRules:AmuletRules.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:627 -> Splice.AmuletRules:619:

```diff
        do
+         deprecatedChoice "splice-amulet" "0.1.12" "AmuletRules_UpdateFutureAmuletConfigSchedule"
-         now <- getTime
-         require "The update is scheduled in the future" (now < scheduleItem._1)
-         newAmuletRules <- create this with configSchedule = prune now (update scheduleItem this.configSchedule)
-         return AmuletRules_UpdateFutureAmuletConfigScheduleResult with ..
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UpdateFutureAmuletConfigSchedule``

Fewer checks in the choice body (assertions or aborts): 3 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:627 -> Splice.AmuletRules:619:

```diff
        do
+         deprecatedChoice "splice-amulet" "0.1.12" "AmuletRules_UpdateFutureAmuletConfigSchedule"
-         now <- getTime
-         require "The update is scheduled in the future" (now < scheduleItem._1)
-         newAmuletRules <- create this with configSchedule = prune now (update scheduleItem this.configSchedule)
-         return AmuletRules_UpdateFutureAmuletConfigScheduleResult with ..
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1091:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:715:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1566:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1091:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:715:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1566:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1091:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:715:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1566:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1091:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:715:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1091:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:715:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.11 -> 0.1.12, Splice.AmuletRules:1566:

```text
(source text identical: the difference comes from code this function calls)
```

