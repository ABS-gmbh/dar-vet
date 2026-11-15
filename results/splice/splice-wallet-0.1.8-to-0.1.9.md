# dar-vet: splice-wallet 0.1.8 -> 0.1.9

Old package ID `882f6438ef69a4fbbe36e32e020d98f3eb1982ac484686e3ca1e2577d677626a`, new package ID `940b9fd181c0cf358dd8c38934a8c908d2a3ee5a4728f72481b4de6720f3f8d5`.

**8 findings:** 6 HIGH, 1 MEDIUM, 1 LOW.

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_AllocationFactory_Allocate``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-allocation-instruction-v1:Splice.Api.Token.AllocationInstructionV1:AllocationFactory.AllocationFactory_Allocate.

Version 0.1.9, Splice.Wallet.Install:624:

```daml
    nonconsuming choice WalletAppInstall_AllocationFactory_Allocate : Splice.Api.Token.AllocationInstructionV1.AllocationInstructionResult
      with
        allocationFactory : ContractId Splice.Api.Token.AllocationInstructionV1.AllocationFactory
        allocateArg : Splice.Api.Token.AllocationInstructionV1.AllocationFactory_Allocate
      controller validatorParty
      do require ("expected admin " <> show allocateArg.expectedAdmin <> " is dso " <> show dsoParty) (allocateArg.expectedAdmin == dsoParty)
         let sender = allocateArg.allocation.transferLeg.sender
         require ("sender " <> show sender <> " is endUserParty " <> show endUserParty) (sender == endUserParty)
         let legInstrumentId = allocateArg.allocation.transferLeg.instrumentId
         require ("instrumentId " <> show legInstrumentId <> " is amuletInstrumentId " <> show (amuletInstrumentId dsoParty)) (legInstrumentId == amuletInstrumentId dsoParty)
         exercise allocationFactory allocateArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_Allocation_Withdraw``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-allocation-v1:Splice.Api.Token.AllocationV1:Allocation.Allocation_Withdraw, fetch splice-api-token-allocation-v1:Splice.Api.Token.AllocationV1:Allocation.

Version 0.1.9, Splice.Wallet.Install:636:

```daml
    nonconsuming choice WalletAppInstall_Allocation_Withdraw : Splice.Api.Token.AllocationV1.Allocation_WithdrawResult
      with
        allocationCid : ContractId Splice.Api.Token.AllocationV1.Allocation
        withdrawArg : Splice.Api.Token.AllocationV1.Allocation_Withdraw
      controller validatorParty
      do allocation <- fetchCheckedInterface (ForDso dsoParty) allocationCid
         let sender = (view allocation).allocation.transferLeg.sender
         require ("sender " <> show sender <> " must match endUserParty " <> show endUserParty) (sender == endUserParty)
         exercise allocationCid withdrawArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferFactory_Transfer``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferFactory.TransferFactory_Transfer.

Version 0.1.9, Splice.Wallet.Install:580:

```daml
    nonconsuming choice WalletAppInstall_TransferFactory_Transfer : Splice.Api.Token.TransferInstructionV1.TransferInstructionResult
      with
        transferFactoryCid : ContractId Splice.Api.Token.TransferInstructionV1.TransferFactory
        transferArg : Splice.Api.Token.TransferInstructionV1.TransferFactory_Transfer
      controller validatorParty
      do let transfer = transferArg.transfer
         require ("sender " <> show transfer.sender <> " is endUserParty " <> show endUserParty) (transfer.sender == endUserParty)
         require ("instrumentId " <> show transfer.instrumentId <> " is amuletInstrumentId " <> show (amuletInstrumentId dsoParty)) (transfer.instrumentId == amuletInstrumentId dsoParty)
         require ("expected admin " <> show transferArg.expectedAdmin <> " is dso " <> show dsoParty) (transferArg.expectedAdmin == dsoParty)
         exercise transferFactoryCid transferArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstruction_Accept``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferInstruction.TransferInstruction_Accept, fetch splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferInstruction.

Version 0.1.9, Splice.Wallet.Install:591:

```daml
    nonconsuming choice WalletAppInstall_TransferInstruction_Accept : Splice.Api.Token.TransferInstructionV1.TransferInstructionResult
      with
        transferInstructionCid : ContractId Splice.Api.Token.TransferInstructionV1.TransferInstruction
        acceptArg : Splice.Api.Token.TransferInstructionV1.TransferInstruction_Accept
      controller validatorParty
      do instruction <- fetchCheckedInterface (ForDso dsoParty) transferInstructionCid
         let receiver = (view instruction).transfer.receiver
         require ("receiver " <> show receiver <> " must match endUserParty " <> show endUserParty) (receiver == endUserParty)
         exercise transferInstructionCid acceptArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstruction_Reject``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferInstruction.TransferInstruction_Reject, fetch splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferInstruction.

Version 0.1.9, Splice.Wallet.Install:601:

```daml
    nonconsuming choice WalletAppInstall_TransferInstruction_Reject : Splice.Api.Token.TransferInstructionV1.TransferInstructionResult
      with
        transferInstructionCid : ContractId Splice.Api.Token.TransferInstructionV1.TransferInstruction
        rejectArg : Splice.Api.Token.TransferInstructionV1.TransferInstruction_Reject
      controller validatorParty
      do instruction <- fetchCheckedInterface (ForDso dsoParty) transferInstructionCid
         let receiver = (view instruction).transfer.receiver
         require ("receiver " <> show receiver <> " must match endUserParty " <> show endUserParty) (receiver == endUserParty)
         exercise transferInstructionCid rejectArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstruction_Withdraw``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferInstruction.TransferInstruction_Withdraw, fetch splice-api-token-transfer-instruction-v1:Splice.Api.Token.TransferInstructionV1:TransferInstruction.

Version 0.1.9, Splice.Wallet.Install:611:

```daml
    nonconsuming choice WalletAppInstall_TransferInstruction_Withdraw : Splice.Api.Token.TransferInstructionV1.TransferInstructionResult
      with
        transferInstructionCid : ContractId Splice.Api.Token.TransferInstructionV1.TransferInstruction
        withdrawArg : Splice.Api.Token.TransferInstructionV1.TransferInstruction_Withdraw
      controller validatorParty
      do instruction <- fetchCheckedInterface (ForDso dsoParty) transferInstructionCid
         let sender = (view instruction).transfer.sender
         require ("sender " <> show sender <> " must match endUserParty " <> show endUserParty) (sender == endUserParty)
         exercise transferInstructionCid withdrawArg
```

## MEDIUM - effects-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Ledger effects changed. Added: none. Removed: fetch splice-amulet:Splice.AmuletRules:AmuletRules.

- The change is inside the helper function(s) Splice.AmuletRules:computeSynchronizerFees, Splice.Util:checkContractGroupId, Splice.Util:fetchAndArchive, Splice.Util:fetchChecked, Splice.Util:fetchPublicReferenceData, Splice.Wallet.Install:executeAmuletOperationRec, Splice.Wallet.Install:mkMergeAmuletAndRewardsTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.Wallet.Install:46 -> Splice.Wallet.Install:52:

```diff
        handleResult COO_RenewTransferPreapproval resultE
+     CO_TransferPreapprovalSend transferPreapprovalCid providerFeaturedAppRightCid amount description -> do
-     CO_TransferPreapprovalSend transferPreapprovalCid providerFeaturedAppRightCid amount -> do
        -- The designated app provider for pre-approved transfers is the host of the receiver's party and
...
          amount
+         description
        handleResultTransferPreapprovalSend result
...
        Left errorMsg -> handleLeft errorMsg
+       Right (TransferPreapproval_SendResult transferResult _meta) -> do
-       Right (TransferPreapproval_SendResult transferResult) -> do
          let senderChange = transferResult.senderChangeAmulet
```

Version 0.1.8 -> 0.1.9, Splice.Wallet.Install:239 -> Splice.Wallet.Install:245:

```diff
          outputs = []
+         beneficiaries = None
    transfer
```

## LOW - body-changed - `Splice.Wallet.TransferOffer:AcceptedTransferOffer, choice `AcceptedTransferOffer_Complete``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.Wallet.TransferOffer:112 -> Splice.Wallet.TransferOffer:112:

```diff
                ]
+             beneficiaries = None -- no beneficiaries for p2p transfers
          result <- exercisePaymentTransfer transferContext transfer
```

