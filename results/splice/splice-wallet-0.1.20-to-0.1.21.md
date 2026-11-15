# dar-vet: splice-wallet 0.1.20 -> 0.1.21

Old package ID `e485c2eb59cca556e0e2ecfaaef45346dd0ba64f9976a3957938d814f846c78c`, new package ID `f799a58fa53dfe48bae52bd5dbcc2b578a7d4dfee3ae3f4eb7635fe9a8cc67d3`.

**3 findings:** 0 HIGH, 1 MEDIUM, 2 LOW.

## MEDIUM - guard-removed - `Splice.Wallet.TransferPreapproval:TransferPreapprovalProposal, choice `TransferPreapprovalProposal_Accept``

Fewer checks in the choice body (assertions or aborts): 2 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.Wallet.TransferPreapproval:20 -> Splice.Wallet.TransferPreapproval:20:

```diff
          now <- getTime
+         assertWithinDeadline "expiresAt is not in the past" expiresAt
-         require "expiresAt is not in the past" (now < expiresAt)
          AmuletRules_CreateTransferPreapprovalResult{..} <- exercise context.amuletRules AmuletRules_CreateTransferPreapproval
...
              expectedDso
+             optCreatedAt = Some now
          return TransferPreapprovalProposal_AcceptResult with ..
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.20 -> 0.1.21, Splice.Wallet.Install:42 -> Splice.Wallet.Install:42:

```diff
        result <- handleCreateExternalPartySetupProposal executionContext externalParty preapprovalExpiresAt
+       handleResultMultiChange COO_CreateExternalPartySetupProposal result
-       handleResult COO_CreateExternalPartySetupProposal result
      CO_AcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt -> do
...
        result <- handleAcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt
+       handleResultMultiChange COO_AcceptTransferPreapprovalProposal result
-       handleResult COO_AcceptTransferPreapprovalProposal result
      CO_RenewTransferPreapproval previousApprovalCid newExpiresAt -> do
        result <- handleRenewTransferPreapproval previousApprovalCid newExpiresAt
+       handleResultMultiChange COO_RenewTransferPreapproval result
-       handleResult COO_RenewTransferPreapproval result
      CO_TransferPreapprovalSend transferPreapprovalCid providerFeaturedAppRightCid amount description -> do
...
  
+     handleResultMultiChange : (r -> AmuletOperationOutcome) -> (r, [TransferInput]) -> Update [AmuletOperationOutcome]
+     handleResultMultiChange resultConstructor (result, newInputs) =
+         executeAmuletOperationRec executionContext newInputs (resultConstructor result :: prevResults) remainingOperations
+ 
      handleResultTap : AmuletRules_DevNet_TapResult -> Update [AmuletOperationOutcome]
...
  
+     handleCreateExternalPartySetupProposal : ExecutionContext -> Party -> Time -> Update (ContractId ExternalPartySetupProposal, [TransferInput])
-     handleCreateExternalPartySetupProposal : ExecutionContext -> Party -> Time -> Update (ContractId ExternalPartySetupProposal, Optional (ContractId Amulet))
      handleCreateExternalPartySetupProposal executionContext externalParty preapprovalExpiresAt = do
...
          expectedDso = Some executionContext.dso
+         optCreatedAt = None
+       pure (proposalCid, inputsFromPreapprovalOperation amuletPaid transferResult)
-       pure (proposalCid, transferResult.senderChangeAmulet)
  
+     handleAcceptTransferPreapprovalProposal : ContractId TransferPreapprovalProposal -> Time -> Update (ContractId TransferPreapproval, [TransferInput])
-     handleAcceptTransferPreapprovalProposal : ContractId TransferPreapprovalProposal -> Time -> Update (ContractId TransferPreapproval, Optional (ContractId Amulet))
      handleAcceptTransferPreapprovalProposal preapprovalProposalCid expiresAt = do
...
          expiresAt
+       pure (transferPreapprovalCid, inputsFromPreapprovalOperation amuletPaid transferResult)
-       pure (transferPreapprovalCid, transferResult.senderChangeAmulet)
  
+     handleRenewTransferPreapproval : ContractId TransferPreapproval -> Time -> Update (ContractId TransferPreapproval, [TransferInput])
-     handleRenewTransferPreapproval : ContractId TransferPreapproval -> Time -> Update (ContractId TransferPreapproval, Optional (ContractId Amulet))
      handleRenewTransferPreapproval previousApprovalCid newExpiresAt = do
...
          newExpiresAt
+       pure (transferPreapprovalCid, inputsFromPreapprovalOperation amuletPaid transferResult)
+ 
+     inputsFromPreapprovalOperation : Decimal -> TransferResult -> [TransferInput]
+     inputsFromPreapprovalOperation amuletPaid transferResult =
+       if amuletPaid <= 0.0
+         then inputs
+         else optional [] (\amulet -> [InputAmulet amulet]) transferResult.senderChangeAmulet
-       pure (transferPreapprovalCid, transferResult.senderChangeAmulet)
  
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.Wallet.MintingDelegation:22:

```text
(source text identical: the difference comes from code this function calls)
```

