# dar-vet: splice-amulet 0.1.17 -> 0.1.18

Old package ID `6c5802f86709a0ad4784af81f0bab40f3070b2f58128d8843da1e1784c147802`, new package ID `a31be0483f3175647053f28965a4e6d97e3dbc433ea2338be303fae69bbcff6a`.

**8 findings:** 0 HIGH, 6 MEDIUM, 2 LOW.

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Fewer checks in the choice body (assertions or aborts): 26 before, 25 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:881 -> Splice.AmuletRules:932:

```diff
  checkTransferConstraints transfer summary transferConfigAmulet
+     | length transfer.inputs > transferConfigAmulet.maxNumInputs =
+         Left $ mkMaximumInputsExceededFailure
+     | length transfer.outputs > transferConfigAmulet.maxNumOutputs =
+         Left $ mkMaximumOutputsExceededFailure
+     | summary.senderChangeFee < 0.0 =
+         Left $ mkInsufficientFundsFailure (summary.senderChangeFee * (-1.0))
-     | length transfer.inputs > transferConfigAmulet.maxNumInputs = Left $ ITR_Other $ "More than the maximum number of inputs"
-     | length transfer.outputs > transferConfigAmulet.maxNumOutputs = Left $ ITR_Other $ "More than the maximum number of outputs"
-     | summary.senderChangeFee < 0.0 = Left $ ITR_InsufficientFunds with missingAmount = summary.senderChangeFee * (-1.0)
      | otherwise = Right ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1389:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:949:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:912 -> Splice.AmuletRules:965:

```diff
    case checkTransferConstraints t summary csum.config of
+     Left failureStatus -> failWithStatus failureStatus
-     Left err -> throw (InvalidTransfer err)
      Right _ -> pure ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1921:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Fewer checks in the choice body (assertions or aborts): 26 before, 25 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:881 -> Splice.AmuletRules:932:

```diff
  checkTransferConstraints transfer summary transferConfigAmulet
+     | length transfer.inputs > transferConfigAmulet.maxNumInputs =
+         Left $ mkMaximumInputsExceededFailure
+     | length transfer.outputs > transferConfigAmulet.maxNumOutputs =
+         Left $ mkMaximumOutputsExceededFailure
+     | summary.senderChangeFee < 0.0 =
+         Left $ mkInsufficientFundsFailure (summary.senderChangeFee * (-1.0))
-     | length transfer.inputs > transferConfigAmulet.maxNumInputs = Left $ ITR_Other $ "More than the maximum number of inputs"
-     | length transfer.outputs > transferConfigAmulet.maxNumOutputs = Left $ ITR_Other $ "More than the maximum number of outputs"
-     | summary.senderChangeFee < 0.0 = Left $ ITR_InsufficientFunds with missingAmount = summary.senderChangeFee * (-1.0)
      | otherwise = Right ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1389:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:949:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:912 -> Splice.AmuletRules:965:

```diff
    case checkTransferConstraints t summary csum.config of
+     Left failureStatus -> failWithStatus failureStatus
-     Left err -> throw (InvalidTransfer err)
      Right _ -> pure ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1921:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Fewer checks in the choice body (assertions or aborts): 23 before, 22 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:881 -> Splice.AmuletRules:932:

```diff
  checkTransferConstraints transfer summary transferConfigAmulet
+     | length transfer.inputs > transferConfigAmulet.maxNumInputs =
+         Left $ mkMaximumInputsExceededFailure
+     | length transfer.outputs > transferConfigAmulet.maxNumOutputs =
+         Left $ mkMaximumOutputsExceededFailure
+     | summary.senderChangeFee < 0.0 =
+         Left $ mkInsufficientFundsFailure (summary.senderChangeFee * (-1.0))
-     | length transfer.inputs > transferConfigAmulet.maxNumInputs = Left $ ITR_Other $ "More than the maximum number of inputs"
-     | length transfer.outputs > transferConfigAmulet.maxNumOutputs = Left $ ITR_Other $ "More than the maximum number of outputs"
-     | summary.senderChangeFee < 0.0 = Left $ ITR_InsufficientFunds with missingAmount = summary.senderChangeFee * (-1.0)
      | otherwise = Right ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1389:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:949:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:912 -> Splice.AmuletRules:965:

```diff
    case checkTransferConstraints t summary csum.config of
+     Left failureStatus -> failWithStatus failureStatus
-     Left err -> throw (InvalidTransfer err)
      Right _ -> pure ()
```

## MEDIUM - guard-removed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Fewer checks in the choice body (assertions or aborts): 25 before, 24 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:881 -> Splice.AmuletRules:932:

```diff
  checkTransferConstraints transfer summary transferConfigAmulet
+     | length transfer.inputs > transferConfigAmulet.maxNumInputs =
+         Left $ mkMaximumInputsExceededFailure
+     | length transfer.outputs > transferConfigAmulet.maxNumOutputs =
+         Left $ mkMaximumOutputsExceededFailure
+     | summary.senderChangeFee < 0.0 =
+         Left $ mkInsufficientFundsFailure (summary.senderChangeFee * (-1.0))
-     | length transfer.inputs > transferConfigAmulet.maxNumInputs = Left $ ITR_Other $ "More than the maximum number of inputs"
-     | length transfer.outputs > transferConfigAmulet.maxNumOutputs = Left $ ITR_Other $ "More than the maximum number of outputs"
-     | summary.senderChangeFee < 0.0 = Left $ ITR_InsufficientFunds with missingAmount = summary.senderChangeFee * (-1.0)
      | otherwise = Right ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1389:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:949:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:912 -> Splice.AmuletRules:965:

```diff
    case checkTransferConstraints t summary csum.config of
+     Left failureStatus -> failWithStatus failureStatus
-     Left err -> throw (InvalidTransfer err)
      Right _ -> pure ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1921:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

Fewer checks in the choice body (assertions or aborts): 20 before, 19 now.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeExternalPartyTransfer, Splice.AmuletRules:executeTransfer$u0027, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:881 -> Splice.AmuletRules:932:

```diff
  checkTransferConstraints transfer summary transferConfigAmulet
+     | length transfer.inputs > transferConfigAmulet.maxNumInputs =
+         Left $ mkMaximumInputsExceededFailure
+     | length transfer.outputs > transferConfigAmulet.maxNumOutputs =
+         Left $ mkMaximumOutputsExceededFailure
+     | summary.senderChangeFee < 0.0 =
+         Left $ mkInsufficientFundsFailure (summary.senderChangeFee * (-1.0))
-     | length transfer.inputs > transferConfigAmulet.maxNumInputs = Left $ ITR_Other $ "More than the maximum number of inputs"
-     | length transfer.outputs > transferConfigAmulet.maxNumOutputs = Left $ ITR_Other $ "More than the maximum number of outputs"
-     | summary.senderChangeFee < 0.0 = Left $ ITR_InsufficientFunds with missingAmount = summary.senderChangeFee * (-1.0)
      | otherwise = Right ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1389:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:956:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:912 -> Splice.AmuletRules:965:

```diff
    case checkTransferConstraints t summary csum.config of
+     Left failureStatus -> failWithStatus failureStatus
-     Left err -> throw (InvalidTransfer err)
      Right _ -> pure ()
```

## MEDIUM - effects-changed - `Splice.ExternalPartyAmuletRules:TransferCommand, choice `TransferCommand_Send``

Ledger effects changed. Added: none. Removed: exercise self:Splice.AmuletRules:AmuletRules.AmuletRules_Transfer.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.17 -> 0.1.18, Splice.ExternalPartyAmuletRules:164 -> Splice.ExternalPartyAmuletRules:160:

```diff
        do transferCounter <- fetchChecked (ForOwner with owner = sender, dso) transferCounterCid
- 
-          let mergeInputsAndReportError error = do
-              -- In the absence of automatic merging of inputs for external parties, it is possible for
-              -- a user to accumulate tons of small amulet holdings and be unable to do a transfer.
-              -- We do a self-transfer to merge inputs even in case of a failed transfer to guard against this.
-              _ <- exercisePaymentTransfer dso context Transfer with
-                sender
-                provider = delegate
-                inputs = inputs
-                outputs = []
-                beneficiaries = None -- No beneficiaries for self-transfer merge.
-              pure (TransferCommandResultFailure error)
- 
           if | transferCounter.nextNonce > nonce ->
...
                    result <-
+                     do
-                     try do
                        TransferPreapproval_SendResult result _meta <- exercise transferPreapprovalCid (TransferPreapproval_Send context inputs amount sender description)
                        pure (TransferCommandResultSuccess result)
-                     catch
-                       (ex : InvalidTransfer) -> mergeInputsAndReportError ex.reason
-                       (ex : AnyException) -> mergeInputsAndReportError (ITR_Other $ message ex)
                    pure TransferCommand_SendResult with
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:checkTransferConstraints, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:validateBuyMemberTrafficInputs, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:881 -> Splice.AmuletRules:932:

```diff
  checkTransferConstraints transfer summary transferConfigAmulet
+     | length transfer.inputs > transferConfigAmulet.maxNumInputs =
+         Left $ mkMaximumInputsExceededFailure
+     | length transfer.outputs > transferConfigAmulet.maxNumOutputs =
+         Left $ mkMaximumOutputsExceededFailure
+     | summary.senderChangeFee < 0.0 =
+         Left $ mkInsufficientFundsFailure (summary.senderChangeFee * (-1.0))
-     | length transfer.inputs > transferConfigAmulet.maxNumInputs = Left $ ITR_Other $ "More than the maximum number of inputs"
-     | length transfer.outputs > transferConfigAmulet.maxNumOutputs = Left $ ITR_Other $ "More than the maximum number of outputs"
-     | summary.senderChangeFee < 0.0 = Left $ ITR_InsufficientFunds with missingAmount = summary.senderChangeFee * (-1.0)
      | otherwise = Right ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1389:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:949:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:912 -> Splice.AmuletRules:965:

```diff
    case checkTransferConstraints t summary csum.config of
+     Left failureStatus -> failWithStatus failureStatus
-     Left err -> throw (InvalidTransfer err)
      Right _ -> pure ()
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1921:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:1382 -> Splice.AmuletRules:1435:

```diff
  validateBuyMemberTrafficInputs configUsd synchronizerId trafficAmount
+   | not (synchronizerId `Set.member` configUsd.decentralizedSynchronizer.requiredSynchronizers) =
+       Left $ mkUnknownSynchronizerFailure synchronizerId
+   | trafficAmount < configUsd.decentralizedSynchronizer.fees.minTopupAmount =
+       Left $ mkInsufficientTopupAmountFailure trafficAmount configUsd.decentralizedSynchronizer.fees.minTopupAmount
-   | not (synchronizerId `Set.member` configUsd.decentralizedSynchronizer.requiredSynchronizers) = Left $ ITR_UnknownSynchronizer with synchronizerId
-   | trafficAmount < configUsd.decentralizedSynchronizer.fees.minTopupAmount = Left $ ITR_InsufficientTopupAmount with
-       requestedTopupAmount = trafficAmount
-       minTopupAmount = configUsd.decentralizedSynchronizer.fees.minTopupAmount
    | otherwise = Right ()
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.17 -> 0.1.18, Splice.AmuletRules:275:

```text
(source text identical: the difference comes from code this function calls)
```

