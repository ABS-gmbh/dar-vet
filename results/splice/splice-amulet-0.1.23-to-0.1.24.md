# dar-vet: splice-amulet 0.1.23 -> 0.1.24

Old package ID `8fe7573f5535dc5b910a1f24d7d980da0e10ab38f8d29cb397146119bbfb7b3a`, new package ID `17b6f7c18efec9022dde39a19039783c68490a722e8a5088b22ee034678171a2`.

**7 findings:** 0 HIGH, 0 MEDIUM, 7 LOW.

## LOW - body-changed - `Splice.Amulet.RewardAccountingV2:ProcessRewardsV2, choice `ProcessRewardsV2_ProcessBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet.CryptoHash:hashText, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.Amulet.CryptoHash:101:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1164:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:2278:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1164:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:2329:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:2278:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1164:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:2329:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:2278:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1164:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:payForTransferPreapproval, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1164:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:2329:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:2278:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeExternalPartyTransfer, Splice.AmuletRules:executeTransfer$u0027, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1686:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1171:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.23 -> 0.1.24, Splice.AmuletRules:1180:

```text
(source text identical: the difference comes from code this function calls)
```

