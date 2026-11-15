# dar-vet: splice-amulet 0.1.10 -> 0.1.11

Old package ID `17e51d96dbe9e41e7e5f621861419905206c12aeef77956c0a0da14714c1fa62`, new package ID `9824927cdb455f833867b74c01cffcd8cb8cc5edd4d2273cea1329b708ef3ce5`.

**11 findings:** 0 HIGH, 3 MEDIUM, 8 LOW.

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Send``

Ledger effects changed. Added: none. Removed: fetch self:Splice.AmuletRules:AmuletRules.

- The change is inside the helper function(s) Splice.AmuletRules:exercisePaymentTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1153 -> Splice.AmuletRules:1187:

```diff
+ exercisePaymentTransfer expectedDso PaymentTransferContext{..} transfer = do
- exercisePaymentTransfer PaymentTransferContext{..} transfer =
    exercise amuletRules AmuletRules_Transfer with
...
      context
+     expectedDso = Some expectedDso
```

## MEDIUM - guard-removed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Send``

Fewer checks in the choice body (assertions or aborts): 2 before, 1 now.

- The change is inside the helper function(s) Splice.AmuletRules:exercisePaymentTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1153 -> Splice.AmuletRules:1187:

```diff
+ exercisePaymentTransfer expectedDso PaymentTransferContext{..} transfer = do
- exercisePaymentTransfer PaymentTransferContext{..} transfer =
    exercise amuletRules AmuletRules_Transfer with
...
      context
+     expectedDso = Some expectedDso
```

## MEDIUM - effects-changed - `Splice.ExternalPartyAmuletRules:TransferCommand, choice `TransferCommand_Send``

Ledger effects changed. Added: none. Removed: fetch self:Splice.AmuletRules:AmuletRules.

- The change is inside the helper function(s) Splice.AmuletRules:exercisePaymentTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1153 -> Splice.AmuletRules:1187:

```diff
+ exercisePaymentTransfer expectedDso PaymentTransferContext{..} transfer = do
- exercisePaymentTransfer PaymentTransferContext{..} transfer =
    exercise amuletRules AmuletRules_Transfer with
...
      context
+     expectedDso = Some expectedDso
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeSynchronizerFees, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1128 -> Splice.AmuletRules:1154:

```diff
+ computeSynchronizerFees dso validator trafficAmount amuletRules context = do
- computeSynchronizerFees validator trafficAmount amuletRules context = do
    -- compute traffic cost in USD
...
    -- compute traffic cost in Amulet
+   contextMiningRound <- fetchPublicReferenceData (ForDso dso) context.openMiningRound (OpenMiningRound_Fetch validator)
-   contextMiningRound <- exercise context.openMiningRound (OpenMiningRound_Fetch validator)
    let trafficCostAmulet = trafficCostUsd / contextMiningRound.amuletPrice
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1102:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:726:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1577:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:104 -> Splice.AmuletRules:105:

```diff
          outputs : [TransferOutput]
+         expectedDso : Optional Party
+         -- Party that the sender expects to represent the DSO party of the AmuletRules contract they are calling.
+         -- Must always be set to protect from malicious delegees swapping the AmuletRules contract out for
+         -- one under their control.
       controller sender
+      do checkExpectedDso dso expectedDso
+         fees <- computeFees dso context sender outputs
-      do fees <- computeFees dso context sender outputs
          return AmuletRules_ComputeFeesResult with fees
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1102:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:726:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1577:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1102:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:726:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1577:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:245:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1102:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:726:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1102:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:726:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.10 -> 0.1.11, Splice.AmuletRules:1577:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules, choice `ExternalPartyAmuletRules_CreateTransferCommand``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.ExternalPartyAmuletRules:38 -> Splice.ExternalPartyAmuletRules:38:

```diff
          description : Optional Text
+         expectedDso : Optional Party
+         -- Party that the sender expects to represent the DSO party of the ExternalAmuletRules contract they are calling.
+         -- Must always be set to protect from malicious delegees swapping the ExternalAmuletRules contract out for
+         -- one under their control.
        controller sender
+       do
+          checkExpectedDso dso expectedDso
+          assertWithinDeadline "ExternalPartyAmuletRules_CreateTransferCommand.expiresAt" expiresAt
-       do -- TODO(#18633): once the `isLedgerTimeXX` primitives are in, enforce that `expiresAt` is in the future
-          -- We don't do that yet, as otherwise this would be a regression, as it would introduce a time-bound
-          -- on the prepared transaction creating the transfer command.
           cmd <- create TransferCommand with
```

