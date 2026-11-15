# dar-vet: splice-amulet 0.1.7 -> 0.1.8

Old package ID `4646d50cbdec6f088c98ae543da5c973d2d1be3363b9f32eb097d8fdc063ade7`, new package ID `511bd3bf23fab4e5171edb22dceabe3061f7faf78a44f8af44f3b87f977c61f6`.

**7 findings:** 1 HIGH, 0 MEDIUM, 6 LOW.

## HIGH - new-choice - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.AmuletRules:AmuletRules.

Version 0.1.8, Splice.AmuletRules:546:

```daml
    choice AmuletRules_SetConfig: AmuletRules_SetConfigResult
      with
        newConfig : AmuletConfig Unit.USD
        baseConfig : AmuletConfig Unit.USD
      controller dso
      do
        require "futureValues is empty" (null this.configSchedule.futureValues)
        newAmuletRules <- create this with
          configSchedule = Schedule with
            initialValue = patch newConfig baseConfig this.configSchedule.initialValue
            futureValues = []
        return AmuletRules_SetConfigResult with ..
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:989:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:663:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:1402:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:989:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:663:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:1402:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:989:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:663:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:1402:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:209:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:989:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:663:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:989:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:663:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.7 -> 0.1.8, Splice.AmuletRules:1402:

```text
(source text identical: the difference comes from code this function calls)
```

