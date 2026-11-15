# dar-vet: splice-wallet 0.1.18 -> 0.1.19

Old package ID `94d88246f69d8a4b69333d1f993e3280deaca19b70511ea7687f01e4328a34a4`, new package ID `1d8317b1e476c03ea2a85bed8435e5c182abe501db58350009187fa839ab2cca`.

**2 findings:** 0 HIGH, 0 MEDIUM, 2 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.18 -> 0.1.19, Splice.Wallet.Install:52 -> Splice.Wallet.Install:42:

```diff
      CO_AppPayment cid -> do
+       result <- exercise cid (AppPaymentRequest_Accept inputs executionContext.paymentContext executionContext.validator)
+       handleResultAcceptAppPayment result
-       resultE <- catchAll $ exercise cid (AppPaymentRequest_Accept inputs executionContext.paymentContext executionContext.validator)
-       handleResultAcceptAppPayment resultE
      CO_CompleteAcceptedTransfer cid -> do
+       result <- exercise cid (AcceptedTransferOffer_Complete inputs executionContext.paymentContext executionContext.validator)
+       handleResultCompleteAcceptedTransfer result
-       resultE <- catchAll $ exercise cid (AcceptedTransferOffer_Complete inputs executionContext.paymentContext executionContext.validator)
-       handleResultCompleteAcceptedTransfer resultE
      CO_SubscriptionAcceptAndMakeInitialPayment cid -> do
+       result <- exercise cid (SubscriptionRequest_AcceptAndMakePayment inputs executionContext.paymentContext executionContext.validator)
+       handleResultSubscriptionAcceptAndMakePayment result
-       resultE <- catchAll $ exercise cid (SubscriptionRequest_AcceptAndMakePayment inputs executionContext.paymentContext executionContext.validator)
-       handleResultSubscriptionAcceptAndMakePayment resultE
      CO_SubscriptionMakePayment cid -> do
+       result <- exercise cid (SubscriptionIdleState_MakePayment inputs executionContext.paymentContext executionContext.validator)
+       handleResultSubscriptionMakePayment result
-       resultE <- catchAll $ exercise cid (SubscriptionIdleState_MakePayment inputs executionContext.paymentContext executionContext.validator)
-       handleResultSubscriptionMakePayment resultE
      CO_BuyMemberTraffic trafficAmount memberId synchronizerId migrationId minTopupInterval topupStateCid -> do
+       result <- handleBuyMemberTraffic trafficAmount memberId synchronizerId migrationId minTopupInterval topupStateCid
+       handleResult COO_BuyMemberTraffic result
-       resultE <- catchAll $ handleBuyMemberTraffic trafficAmount memberId synchronizerId migrationId minTopupInterval topupStateCid
-       handleResult COO_BuyMemberTraffic resultE
      CO_CompleteBuyTrafficRequest cid -> do
+       result <- exercise cid (BuyTrafficRequest_Complete inputs executionContext.paymentContext executionContext.validator)
+       handleResultBuyTraffic result
-       resultE <- catchAll $ exercise cid (BuyTrafficRequest_Complete inputs executionContext.paymentContext executionContext.validator)
-       handleResultBuyTraffic resultE
      CO_MergeTransferInputs -> do
          let transfer = mkMergeAmuletAndRewardsTransfer executionContext.endUser inputs
+         result <- exercisePaymentTransfer executionContext.dso context transfer
-         result <- catchAll (exercisePaymentTransfer executionContext.dso context transfer)
          handleResultMergeInputs result
      CO_Tap tapAmount -> do
+       amuletsum <- exercise context.amuletRules (AmuletRules_DevNet_Tap executionContext.endUser tapAmount context.context.openMiningRound)
-       amuletsum <- catchAll (exercise context.amuletRules (AmuletRules_DevNet_Tap executionContext.endUser tapAmount context.context.openMiningRound))
        handleResultTap amuletsum
...
        -- creates ExternalPartySetupProposal for externally hosted parties
+       result <- handleCreateExternalPartySetupProposal executionContext externalParty preapprovalExpiresAt
+       handleResult COO_CreateExternalPartySetupProposal result
-       resultE <- catchAll $ handleCreateExternalPartySetupProposal executionContext externalParty preapprovalExpiresAt
-       handleResult COO_CreateExternalPartySetupProposal resultE
      CO_AcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt -> do
        -- creates TransferPreapproval via the wallet
+       result <- handleAcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt
+       handleResult COO_AcceptTransferPreapprovalProposal result
-       resultE <- catchAll $ handleAcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt
-       handleResult COO_AcceptTransferPreapprovalProposal resultE
      CO_RenewTransferPreapproval previousApprovalCid newExpiresAt -> do
+       result <- handleRenewTransferPreapproval previousApprovalCid newExpiresAt
+       handleResult COO_RenewTransferPreapproval result
-       resultE <- catchAll $ handleRenewTransferPreapproval previousApprovalCid newExpiresAt
-       handleResult COO_RenewTransferPreapproval resultE
      CO_TransferPreapprovalSend transferPreapprovalCid providerFeaturedAppRightCid amount description -> do
...
        let paymentTransferContext = context with context = transferContext
+       result <- exercise transferPreapprovalCid TransferPreapproval_Send with
-       result <- catchAll $ exercise transferPreapprovalCid TransferPreapproval_Send with
          sender = executionContext.endUser
...
  
+     handleResult : (r -> AmuletOperationOutcome) -> (r, Optional (ContractId Amulet)) -> Update [AmuletOperationOutcome]
+     handleResult resultConstructor (result, change) = do
+       let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) change
+       executeAmuletOperationRec executionContext inputAmulets (resultConstructor result :: prevResults) remainingOperations
-     handleResult : (r -> AmuletOperationOutcome) -> Either InvalidTransferReason (r, Optional (ContractId Amulet)) -> Update [AmuletOperationOutcome]
-     handleResult resultConstructor = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right (result, change) -> do
-         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) change
-         executeAmuletOperationRec executionContext inputAmulets (resultConstructor result :: prevResults) remainingOperations
  
+     handleResultMergeInputs : TransferResult -> Update [AmuletOperationOutcome]
+     handleResultMergeInputs transferResult = do
+       let senderChange = transferResult.senderChangeAmulet
+       let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
+       executeAmuletOperationRec executionContext inputAmulets (COO_MergeTransferInputs senderChange :: prevResults) remainingOperations
-     handleResultMergeInputs : Either InvalidTransferReason (TransferResult) -> Update [AmuletOperationOutcome]
-     handleResultMergeInputs = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right transferResult -> do
-         let senderChange = transferResult.senderChangeAmulet
-         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
-         executeAmuletOperationRec executionContext inputAmulets (COO_MergeTransferInputs senderChange :: prevResults) remainingOperations
  
+     handleResultTap : AmuletRules_DevNet_TapResult -> Update [AmuletOperationOutcome]
+     handleResultTap result = do
+       let tappedAmulet = result.amuletSum.amulet
+       executeAmuletOperationRec executionContext (InputAmulet tappedAmulet :: inputs) (COO_Tap result.amuletSum.amulet :: prevResults) remainingOperations
-     handleResultTap : Either InvalidTransferReason (AmuletRules_DevNet_TapResult) -> Update [AmuletOperationOutcome]
-     handleResultTap = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right result -> do
-         let tappedAmulet = result.amuletSum.amulet
-         executeAmuletOperationRec executionContext (InputAmulet tappedAmulet :: inputs) (COO_Tap result.amuletSum.amulet :: prevResults) remainingOperations
  
+     handleResultBuyTraffic : BuyTrafficRequest_CompleteResult -> Update [AmuletOperationOutcome]
+     handleResultBuyTraffic result = do
+       let ((purchasedTraffic, trackingInfo), senderChange) = ((result.purchasedTraffic, result.trackingInfo), result.senderChangeAmulet)
+       let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
+       executeAmuletOperationRec executionContext inputAmulets (COO_CompleteBuyTrafficRequest (purchasedTraffic, trackingInfo) :: prevResults) remainingOperations
-     handleLeft : InvalidTransferReason -> Update [AmuletOperationOutcome]
-     handleLeft errorMsg = executeAmuletOperationRec executionContext inputs (COO_Error (errorMsg) :: prevResults) remainingOperations
  
+     handleResultCompleteAcceptedTransfer : AcceptedTransferOffer_CompleteResult -> Update [AmuletOperationOutcome]
+     handleResultCompleteAcceptedTransfer result = do
+       let ((transferResult, trackingInfo), senderChange) = ((result.transferResult, result.trackingInfo), result.senderChangeAmulet)
+       let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
+       executeAmuletOperationRec executionContext inputAmulets (COO_CompleteAcceptedTransfer (transferResult, trackingInfo) :: prevResults) remainingOperations
-     handleResultBuyTraffic : Either InvalidTransferReason (BuyTrafficRequest_CompleteResult) -> Update [AmuletOperationOutcome]
-     handleResultBuyTraffic = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right result -> do
-         let ((purchasedTraffic, trackingInfo), senderChange) = ((result.purchasedTraffic, result.trackingInfo), result.senderChangeAmulet)
-         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
-         executeAmuletOperationRec executionContext inputAmulets (COO_CompleteBuyTrafficRequest (purchasedTraffic, trackingInfo) :: prevResults) remainingOperations
  
+     handleResultAcceptAppPayment : AppPaymentRequest_AcceptResult -> Update [AmuletOperationOutcome]
+     handleResultAcceptAppPayment result = do
+       let (acceptedPayment, senderChange) = (result.acceptedPayment, result.senderChangeAmulet)
+       let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
+       executeAmuletOperationRec executionContext inputAmulets (COO_AcceptedAppPayment (acceptedPayment) :: prevResults) remainingOperations
-     handleResultCompleteAcceptedTransfer : Either InvalidTransferReason (AcceptedTransferOffer_CompleteResult) -> Update [AmuletOperationOutcome]
-     handleResultCompleteAcceptedTransfer = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right result -> do
-         let ((transferResult, trackingInfo), senderChange) = ((result.transferResult, result.trackingInfo), result.senderChangeAmulet)
-         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
-         executeAmuletOperationRec executionContext inputAmulets (COO_CompleteAcceptedTransfer (transferResult, trackingInfo) :: prevResults) remainingOperations
  
+     handleResultSubscriptionAcceptAndMakePayment : SubscriptionRequest_AcceptAndMakePaymentResult -> Update [AmuletOperationOutcome]
+     handleResultSubscriptionAcceptAndMakePayment result = do
+       let (subscriptionPayment, senderChange) = (result.subscriptionPayment, result.senderChange)
+       let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
+       executeAmuletOperationRec executionContext inputAmulets (COO_SubscriptionInitialPayment (subscriptionPayment) :: prevResults) remainingOperations
-     handleResultAcceptAppPayment : Either InvalidTransferReason (AppPaymentRequest_AcceptResult) -> Update [AmuletOperationOutcome]
-     handleResultAcceptAppPayment = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right result -> do
-         let (acceptedPayment, senderChange) = (result.acceptedPayment, result.senderChangeAmulet)
-         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
-         executeAmuletOperationRec executionContext inputAmulets (COO_AcceptedAppPayment (acceptedPayment) :: prevResults) remainingOperations
  
+     handleResultSubscriptionMakePayment : SubscriptionIdleState_MakePaymentResult -> Update [AmuletOperationOutcome]
+     handleResultSubscriptionMakePayment result = do
+       let (subscriptionPayment, senderChange) = (result.subscriptionPayment, result.senderChange)
+       let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
+       executeAmuletOperationRec executionContext inputAmulets (COO_SubscriptionPayment (subscriptionPayment) :: prevResults) remainingOperations
-     handleResultSubscriptionAcceptAndMakePayment : Either InvalidTransferReason (SubscriptionRequest_AcceptAndMakePaymentResult) -> Update [AmuletOperationOutcome]
-     handleResultSubscriptionAcceptAndMakePayment = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right result -> do
-         let (subscriptionPayment, senderChange) = (result.subscriptionPayment, result.senderChange)
-         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
-         executeAmuletOperationRec executionContext inputAmulets (COO_SubscriptionInitialPayment (subscriptionPayment) :: prevResults) remainingOperations
  
+     handleResultTransferPreapprovalSend : TransferPreapproval_SendResult -> Update [AmuletOperationOutcome]
+     handleResultTransferPreapprovalSend (TransferPreapproval_SendResult transferResult _meta) = do
+       let senderChange = transferResult.senderChangeAmulet
+       let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
+       executeAmuletOperationRec executionContext inputAmulets (COO_TransferPreapprovalSend senderChange :: prevResults) remainingOperations
-     handleResultSubscriptionMakePayment : Either InvalidTransferReason (SubscriptionIdleState_MakePaymentResult) -> Update [AmuletOperationOutcome]
-     handleResultSubscriptionMakePayment = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right result -> do
-         let (subscriptionPayment, senderChange) = (result.subscriptionPayment, result.senderChange)
-         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
-         executeAmuletOperationRec executionContext inputAmulets (COO_SubscriptionPayment (subscriptionPayment) :: prevResults) remainingOperations
  
-     handleResultTransferPreapprovalSend : Either InvalidTransferReason TransferPreapproval_SendResult -> Update [AmuletOperationOutcome]
-     handleResultTransferPreapprovalSend = \case
-       Left errorMsg -> handleLeft errorMsg
-       Right (TransferPreapproval_SendResult transferResult _meta) -> do
-         let senderChange = transferResult.senderChangeAmulet
-         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
-         executeAmuletOperationRec executionContext inputAmulets (COO_TransferPreapprovalSend senderChange :: prevResults) remainingOperations
- 
      handleBuyMemberTraffic: Int -> Text -> Text -> Int -> RelTime -> Optional (ContractId ValidatorTopUpState) -> Update (ContractId MemberTraffic, Optional (ContractId Amulet))
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.18 -> 0.1.19, Splice.Wallet.MintingDelegation:20:

```text
(source text identical: the difference comes from code this function calls)
```

