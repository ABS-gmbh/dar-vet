# dar-vet: splice-amulet 0.1.15 -> 0.1.16

Old package ID `67fac2f853bce8dbf0b9817bb5ba7c59f10e8120b7c808696f7010e5f0c8a791`, new package ID `c208d7ead1e4e9b610fc2054d0bf00716144ad444011bce0b02dcd6cd0cb8a23`.

**10 findings:** 2 HIGH, 2 MEDIUM, 6 LOW.

## HIGH - new-interface-instance - `Splice.Amulet:FeaturedAppActivityMarker`

Template now implements interface splice-api-featured-app-v2:Splice.Api.FeaturedAppRightV2:FeaturedAppActivityMarker. Its choices can be exercised on contracts created under the old version.

## HIGH - new-interface-instance - `Splice.Amulet:FeaturedAppRight`

Template now implements interface splice-api-featured-app-v2:Splice.Api.FeaturedAppRightV2:FeaturedAppRight. Its choices can be exercised on contracts created under the old version.

## MEDIUM - ensure-changed - `Splice.Amulet:FeaturedAppActivityMarker`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.15 -> 0.1.16, Splice.Amulet:286 -> Splice.Amulet:309:

```diff
+     ensure 0.0 < weight && weight <= 10000.0
-     ensure 0.0 < weight && weight <= 1.0
```

## MEDIUM - choice-observers-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Who is informed when this choice is exercised has changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:708 -> Splice.AmuletRules:708:

```diff
          openMiningRoundCid : ContractId OpenMiningRound
+         observers : Optional [Party] -- ^ A list of choice observers. This is expected to be set to the union of all providers and beneficiaries to ensure that this creates only one view.
+       observer fromOptional [] observers
        controller dso
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1206:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:799:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1685:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:708 -> Splice.AmuletRules:708:

```diff
          openMiningRoundCid : ContractId OpenMiningRound
+         observers : Optional [Party] -- ^ A list of choice observers. This is expected to be set to the union of all providers and beneficiaries to ensure that this creates only one view.
+       observer fromOptional [] observers
        controller dso
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1206:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:799:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1685:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1206:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:799:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1685:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1206:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:799:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1206:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:799:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.15 -> 0.1.16, Splice.AmuletRules:1685:

```text
(source text identical: the difference comes from code this function calls)
```

