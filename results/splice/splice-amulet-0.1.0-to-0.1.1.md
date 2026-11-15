# dar-vet: splice-amulet 0.1.0 -> 0.1.1

Old package ID `48cac5ba4b6bf78df6c3a952ce05409a1d2ef39c05351074679adc0cf9cd1351`, new package ID `67bc95402ad7b08fcdff0ed478308d39c70baca2238c35c5d425a435a8a9e7f7`.

**3 findings:** 0 HIGH, 0 MEDIUM, 3 LOW.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.0 -> 0.1.1, Splice.AmuletRules:915:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.0 -> 0.1.1, Splice.AmuletRules:605:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MiningRound_StartIssuing``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.0 -> 0.1.1, Splice.AmuletRules:353 -> Splice.AmuletRules:353:

```diff
          -- record unclaimed reward contract
+         let totalUnclaimedRewards = params.unclaimedValidatorRewards + params.unclaimedAppRewards + params.unclaimedSvRewards
+         when (totalUnclaimedRewards > 0.0) $
+           void $ create UnclaimedReward with
+             dso
+             amount = totalUnclaimedRewards
-         create UnclaimedReward with
-           dso
-           amount = params.unclaimedValidatorRewards + params.unclaimedAppRewards + params.unclaimedSvRewards
  
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.0 -> 0.1.1, Splice.AmuletRules:915:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.0 -> 0.1.1, Splice.AmuletRules:605:

```text
(source text identical: the difference comes from code this function calls)
```

