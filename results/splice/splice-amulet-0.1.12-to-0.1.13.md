# dar-vet: splice-amulet 0.1.12 -> 0.1.13

Old package ID `95a88ff9ffd509e097802ecf3bbd58c83a5dff408e439cca4e2105ebd2cd0760`, new package ID `6e9fc50fb94e56751b49f09ba2dc84da53a9d7cff08115ebb4f6b7a12d0c990c`.

**7 findings:** 0 HIGH, 0 MEDIUM, 7 LOW.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Bootstrap_Rounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:333 -> Splice.AmuletRules:334:

```diff
          -- Duration of the very first round, typically set to a number large enough for all initial svs to complete joining and grab their SvReward coupon.
+         initialRound : Optional Int
        controller dso
...
          let tickDuration = configUsd.tickDuration
+         let nr0 = case initialRound of
+                     Some round -> round
+                     None -> 0
-         let nr0 = 0
          let nr1 = nr0 + 1
...
  
+         let issuingFor0 = microseconds (nr0 * convertRelTimeToMicroseconds tickDuration)
-         let issuingFor0 = days 0
          let issuingFor1 = issuingFor0 + tickDuration
...
            openMiningRoundCid = last
+           initialRound = Some (Round nr0)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1096:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1571:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1096:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1571:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1096:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1571:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:246:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1096:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1096:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.12 -> 0.1.13, Splice.AmuletRules:1571:

```text
(source text identical: the difference comes from code this function calls)
```

