# dar-vet: splice-amulet 0.1.3 -> 0.1.4

Old package ID `0d89016d5a90eb8bced48bbac99e81c57781b3a36094b8d48b8e4389851e19af`, new package ID `a36ef8888fb44caae13d96341ce1fabd84fc9e2e7b209bbc3caabb48b6be1668`.

**4 findings:** 0 HIGH, 0 MEDIUM, 4 LOW.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:563 -> Splice.AmuletRules:566:

```diff
+ computeOutputFee transferConfigAmulet sender out0 =
- computeOutputFee transferConfigAmulet sender out =
      transferConfigAmulet.createFee.fee + lockingFee + transferFee
    where
+     out = dedupOutputLockHolders out0
      lockingFee = optional 0.0 (\l -> transferConfigAmulet.lockHolderFee.fee * intToDecimal (length l.holders)) out.lock
```

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:920:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:609:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:806 -> Splice.AmuletRules:820:

```diff
    where
-     -- deduplicate lock-holders to store them and charge for them at most once
-     dedupOutputLockHolders out = out with lock = fmap (dedupLockHolders out.receiver) out.lock
- 
-     dedupLockHolders : Party -> TimeLock -> TimeLock
-     dedupLockHolders receiver (TimeLock with expiresAt; holders) =
-       TimeLock with
-         expiresAt
-         holders = dedupSort $ filter (receiver /=) holders
- 
      preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeFees, Splice.AmuletRules:computeOutputFee, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:560:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:563 -> Splice.AmuletRules:566:

```diff
+ computeOutputFee transferConfigAmulet sender out0 =
- computeOutputFee transferConfigAmulet sender out =
      transferConfigAmulet.createFee.fee + lockingFee + transferFee
    where
+     out = dedupOutputLockHolders out0
      lockingFee = optional 0.0 (\l -> transferConfigAmulet.lockHolderFee.fee * intToDecimal (length l.holders)) out.lock
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:177:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:563 -> Splice.AmuletRules:566:

```diff
+ computeOutputFee transferConfigAmulet sender out0 =
- computeOutputFee transferConfigAmulet sender out =
      transferConfigAmulet.createFee.fee + lockingFee + transferFee
    where
+     out = dedupOutputLockHolders out0
      lockingFee = optional 0.0 (\l -> transferConfigAmulet.lockHolderFee.fee * intToDecimal (length l.holders)) out.lock
```

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:920:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:609:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.3 -> 0.1.4, Splice.AmuletRules:806 -> Splice.AmuletRules:820:

```diff
    where
-     -- deduplicate lock-holders to store them and charge for them at most once
-     dedupOutputLockHolders out = out with lock = fmap (dedupLockHolders out.receiver) out.lock
- 
-     dedupLockHolders : Party -> TimeLock -> TimeLock
-     dedupLockHolders receiver (TimeLock with expiresAt; holders) =
-       TimeLock with
-         expiresAt
-         holders = dedupSort $ filter (receiver /=) holders
- 
      preprocessOutput out@(TransferOutput with receiver; amount; lock; receiverFeeRatio)
```

