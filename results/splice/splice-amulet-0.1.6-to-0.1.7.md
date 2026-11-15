# dar-vet: splice-amulet 0.1.6 -> 0.1.7

Old package ID `979ec710c3ae3a05cb44edf8461a9b4d7dd2053add95664f94fc89e5f18df80f`, new package ID `4646d50cbdec6f088c98ae543da5c973d2d1be3363b9f32eb097d8fdc063ade7`.

**6 findings:** 0 HIGH, 0 MEDIUM, 6 LOW.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:972:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:646:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:972:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:646:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:972:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:646:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:206:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:972:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:646:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:972:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.6 -> 0.1.7, Splice.AmuletRules:646:

```text
(source text identical: the difference comes from code this function calls)
```

