# dar-vet: splice-wallet 0.1.5 -> 0.1.6

Old package ID `614b525a50c624062d851ce7df5bdb90ddfa0d6871c486cb6e2c7b694bfbce59`, new package ID `ba51e944e430facce00c297c58f4769240402721de4a1d121705050f8e9be901`.

**2 findings:** 0 HIGH, 1 MEDIUM, 1 LOW.

## MEDIUM - effects-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Ledger effects changed. Added: exercise self:Splice.Wallet.TransferPreapproval:TransferPreapprovalProposal.TransferPreapprovalProposal_Accept, exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_CreateExternalPartySetupProposal, exercise splice-amulet:Splice.AmuletRules:TransferPreapproval.TransferPreapproval_Renew, exercise splice-amulet:Splice.AmuletRules:TransferPreapproval.TransferPreapproval_Send. Removed: none.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.5 -> 0.1.6, Splice.Wallet.Install:45 -> Splice.Wallet.Install:46:

```diff
      ExtAmuletOperation _dummyUnitField -> error "ExtAmuletOperation: bare extension constructors are not allowed at runtime"
+     CO_CreateExternalPartySetupProposal externalParty preapprovalExpiresAt -> do
+       -- creates ExternalPartySetupProposal for externally hosted parties
+       resultE <- catchAll $ handleCreateExternalPartySetupProposal externalParty preapprovalExpiresAt
+       handleResult COO_CreateExternalPartySetupProposal resultE
+     CO_AcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt -> do
+       -- creates TransferPreapproval via the wallet
+       resultE <- catchAll $ handleAcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt
+       handleResult COO_AcceptTransferPreapprovalProposal resultE
+     CO_RenewTransferPreapproval previousApprovalCid newExpiresAt -> do
+       resultE <- catchAll $ handleRenewTransferPreapproval previousApprovalCid newExpiresAt
+       handleResult COO_RenewTransferPreapproval resultE
+     CO_TransferPreapprovalSend transferPreapprovalCid providerFeaturedAppRightCid amount -> do
+       -- The designated app provider for pre-approved transfers is the host of the receiver's party and
+       -- not sender's wallet provider. So, the `featuredAppRight` in this context needs to be replaced
+       -- with that of the app provider hosting the receiver which must be passed as an argument.
+       let transferContext = context.context with featuredAppRight = providerFeaturedAppRightCid
+       let paymentTransferContext = context with context = transferContext
+       result <- catchAll $ exercise transferPreapprovalCid TransferPreapproval_Send with
+         sender = executionContext.endUser
+         context = paymentTransferContext
+         inputs
+         amount
+       handleResultTransferPreapprovalSend result
    where
...
  
+     handleResultTransferPreapprovalSend : Either InvalidTransferReason TransferPreapproval_SendResult -> Update [AmuletOperationOutcome]
+     handleResultTransferPreapprovalSend = \case
+       Left errorMsg -> handleLeft errorMsg
+       Right (TransferPreapproval_SendResult transferResult) -> do
+         let senderChange = transferResult.senderChangeAmulet
+         let inputAmulets = optional [] (\amulet -> [InputAmulet amulet]) senderChange
+         executeAmuletOperationRec executionContext inputAmulets (COO_TransferPreapprovalSend senderChange :: prevResults) remainingOperations
+ 
      handleBuyMemberTraffic: Int -> Text -> Text -> Int -> RelTime -> Optional (ContractId ValidatorTopUpState) -> Update (ContractId MemberTraffic, Optional (ContractId Amulet))
...
  
+     handleCreateExternalPartySetupProposal : Party -> Time -> Update (ContractId ExternalPartySetupProposal, Optional (ContractId Amulet))
+     handleCreateExternalPartySetupProposal externalParty preapprovalExpiresAt = do
+       require "Only a validator can create a proposal to host an external party" (executionContext.endUser == executionContext.validator)
+       AmuletRules_CreateExternalPartySetupProposalResult{..} <- exercise context.amuletRules AmuletRules_CreateExternalPartySetupProposal with
+         context = executionContext.paymentContext
+         inputs
+         user = externalParty
+         validator = executionContext.validator
+         preapprovalExpiresAt
+       pure (proposalCid, transferResult.senderChangeAmulet)
+ 
+     handleAcceptTransferPreapprovalProposal : ContractId TransferPreapprovalProposal -> Time -> Update (ContractId TransferPreapproval, Optional (ContractId Amulet))
+     handleAcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt = do
+       require "Only a validator can accept a proposal to create a transfer preapproval" (executionContext.endUser == executionContext.validator)
+       TransferPreapprovalProposal_AcceptResult{..} <- exercise preapprovalProposalCid TransferPreapprovalProposal_Accept with
+         context = executionContext.paymentContext
+         inputs
+         expiresAt
+       pure (transferPreapprovalCid, transferResult.senderChangeAmulet)
+ 
+     handleRenewTransferPreapproval : ContractId TransferPreapproval -> Time -> Update (ContractId TransferPreapproval, Optional (ContractId Amulet))
+     handleRenewTransferPreapproval previousApprovalCid newExpiresAt = do
+       TransferPreapproval_RenewResult{..} <- exercise previousApprovalCid TransferPreapproval_Renew with
+         context = executionContext.paymentContext
+         inputs
+         newExpiresAt
+       pure (transferPreapprovalCid, transferResult.senderChangeAmulet)
+ 
  executeAmuletOperationRec _executionContext _inputs prevResults [] = do
```

## LOW - template-added - `Splice.Wallet.TransferPreapproval:TransferPreapprovalProposal`

New template. Review it like a new package.

