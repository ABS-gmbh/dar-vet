# dar-vet: splice-wallet 0.1.21 -> 0.1.22

Old package ID `f799a58fa53dfe48bae52bd5dbcc2b578a7d4dfee3ae3f4eb7635fe9a8cc67d3`, new package ID `a78daeeb1a4a2405828b29f8c04814725dbd97ee5a60bd02e839f32518640afa`.

**16 findings:** 8 HIGH, 0 MEDIUM, 8 LOW.

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_AllocationFactoryV2_Allocate``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-allocation-instruction-v2:Splice.Api.Token.AllocationInstructionV2:AllocationFactory.AllocationFactory_Allocate, exercise splice-api-token-allocation-request-v2:Splice.Api.Token.AllocationRequestV2:AllocationRequest.AllocationRequest_Accept, fetch splice-api-token-allocation-request-v2:Splice.Api.Token.AllocationRequestV2:AllocationRequest.

Version 0.1.22, Splice.Wallet.Install:680:

```daml
    nonconsuming choice WalletAppInstall_AllocationFactoryV2_Allocate : Splice.Api.Token.AllocationInstructionV2.AllocationInstructionResult
      with
        allocationFactory : ContractId Splice.Api.Token.AllocationInstructionV2.AllocationFactory
        allocateArg : Splice.Api.Token.AllocationInstructionV2.AllocationFactory_Allocate
        allocationReqCid : Optional (ContractId Splice.Api.Token.AllocationRequestV2.AllocationRequest)
          -- ^ Allocation request to accept at the same time as creating the allocation.
          --
          -- Useful for replay protection when creating allocations that do not require funding.
          --
          -- Optional in case the allocation request should be left as-is, e.g., because
          -- it is used in another registry-specific UI to create an allocation.
      controller validatorParty
      do
        TokenStandard.Utils.checkActors allocateArg.actors [[endUserParty]]
        checkAllocationSpecificationV2 allocateArg.allocation endUserParty
        forA_ allocationReqCid $ \reqCid -> do
          req <- fetchUncheckedButArchiveLater reqCid
          TokenStandard.Utils.requireMatchExpected
            ("allocationReq.settlement", (view req).settlement) allocateArg.settlement
          exercise reqCid Splice.Api.Token.AllocationRequestV2.AllocationRequest_Accept with
            actors = [endUserParty]
            extraArgs = ExtraArgs emptyChoiceContext emptyMetadata
        exercise allocationFactory allocateArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_AllocationInstructionV2_Accept``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-allocation-instruction-v2:Splice.Api.Token.AllocationInstructionV2:AllocationInstruction.AllocationInstruction_Accept, fetch splice-api-token-allocation-instruction-v2:Splice.Api.Token.AllocationInstructionV2:AllocationInstruction.

Version 0.1.22, Splice.Wallet.Install:715:

```daml
    nonconsuming choice WalletAppInstall_AllocationInstructionV2_Accept : Splice.Api.Token.AllocationInstructionV2.AllocationInstructionResult
      with
        allocationInstructionCid : ContractId Splice.Api.Token.AllocationInstructionV2.AllocationInstruction
        acceptArg : Splice.Api.Token.AllocationInstructionV2.AllocationInstruction_Accept
      controller validatorParty
      do
        TokenStandard.Utils.checkActors acceptArg.actors [[endUserParty]]
        allocationInstruction <- fetchCheckedInterface (ForDso dsoParty) allocationInstructionCid
        checkAllocationSpecificationV2 (view allocationInstruction).allocation endUserParty
        exercise allocationInstructionCid acceptArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_AllocationInstructionV2_Withdraw``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-allocation-instruction-v2:Splice.Api.Token.AllocationInstructionV2:AllocationInstruction.AllocationInstruction_Withdraw, fetch splice-api-token-allocation-instruction-v2:Splice.Api.Token.AllocationInstructionV2:AllocationInstruction.

Version 0.1.22, Splice.Wallet.Install:726:

```daml
    nonconsuming choice WalletAppInstall_AllocationInstructionV2_Withdraw : Splice.Api.Token.AllocationInstructionV2.AllocationInstructionResult
      with
        allocationInstructionCid : ContractId Splice.Api.Token.AllocationInstructionV2.AllocationInstruction
        withdrawArg : Splice.Api.Token.AllocationInstructionV2.AllocationInstruction_Withdraw
      controller validatorParty
      do
        TokenStandard.Utils.checkActors withdrawArg.actors [[endUserParty]]
        allocationInstruction <- fetchCheckedInterface (ForDso dsoParty) allocationInstructionCid
        checkAllocationSpecificationV2 (view allocationInstruction).allocation endUserParty
        exercise allocationInstructionCid withdrawArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_AllocationV2_Withdraw``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-allocation-v2:Splice.Api.Token.AllocationV2:Allocation.Allocation_Withdraw, fetch splice-api-token-allocation-v2:Splice.Api.Token.AllocationV2:Allocation.

Version 0.1.22, Splice.Wallet.Install:704:

```daml
    nonconsuming choice WalletAppInstall_AllocationV2_Withdraw : Splice.Api.Token.AllocationV2.AllocationResult
      with
        allocationCid : ContractId Splice.Api.Token.AllocationV2.Allocation
        withdrawArg : Splice.Api.Token.AllocationV2.Allocation_Withdraw
      controller validatorParty
      do
        TokenStandard.Utils.checkActors withdrawArg.actors [[endUserParty]]
        allocation <- fetchCheckedInterface (ForDso dsoParty) allocationCid
        checkAllocationSpecificationV2 (view allocation).allocation endUserParty
        exercise allocationCid withdrawArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferFactoryV2_Transfer``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferFactory.TransferFactory_Transfer.

Version 0.1.22, Splice.Wallet.Install:648:

```daml
    nonconsuming choice WalletAppInstall_TransferFactoryV2_Transfer : Splice.Api.Token.TransferInstructionV2.TransferInstructionResult
      with
        transferFactoryCid : ContractId Splice.Api.Token.TransferInstructionV2.TransferFactory
        transferArg : Splice.Api.Token.TransferInstructionV2.TransferFactory_Transfer
      controller validatorParty
      do TokenStandard.Utils.requireMatchExpected ("transfer.instrumentId", transferArg.transfer.instrumentId) (amuletInstrumentIdV2 dsoParty)
         exercise transferFactoryCid transferArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstructionV2_Accept``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferInstruction.TransferInstruction_Accept, fetch splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferInstruction.

Version 0.1.22, Splice.Wallet.Install:656:

```daml
    nonconsuming choice WalletAppInstall_TransferInstructionV2_Accept : Splice.Api.Token.TransferInstructionV2.TransferInstructionResult
      with
        transferInstructionCid : ContractId Splice.Api.Token.TransferInstructionV2.TransferInstruction
        acceptArg : Splice.Api.Token.TransferInstructionV2.TransferInstruction_Accept
      controller validatorParty
      do checkTransferInstructionV2 dsoParty transferInstructionCid acceptArg.actors endUserParty
         exercise transferInstructionCid acceptArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstructionV2_Reject``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferInstruction.TransferInstruction_Reject, fetch splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferInstruction.

Version 0.1.22, Splice.Wallet.Install:664:

```daml
    nonconsuming choice WalletAppInstall_TransferInstructionV2_Reject : Splice.Api.Token.TransferInstructionV2.TransferInstructionResult
      with
        transferInstructionCid : ContractId Splice.Api.Token.TransferInstructionV2.TransferInstruction
        rejectArg : Splice.Api.Token.TransferInstructionV2.TransferInstruction_Reject
      controller validatorParty
      do checkTransferInstructionV2 dsoParty transferInstructionCid rejectArg.actors endUserParty
         exercise transferInstructionCid rejectArg
```

## HIGH - new-choice - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstructionV2_Withdraw``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferInstruction.TransferInstruction_Withdraw, fetch splice-api-token-transfer-instruction-v2:Splice.Api.Token.TransferInstructionV2:TransferInstruction.

Version 0.1.22, Splice.Wallet.Install:672:

```daml
    nonconsuming choice WalletAppInstall_TransferInstructionV2_Withdraw : Splice.Api.Token.TransferInstructionV2.TransferInstructionResult
      with
        transferInstructionCid : ContractId Splice.Api.Token.TransferInstructionV2.TransferInstruction
        withdrawArg : Splice.Api.Token.TransferInstructionV2.TransferInstruction_Withdraw
      controller validatorParty
      do checkTransferInstructionV2 dsoParty transferInstructionCid withdrawArg.actors endUserParty
         exercise transferInstructionCid withdrawArg
```

## LOW - dependency-behaviour-changed - `(package)`

2 expressions are unchanged in this package but call dependency code that changed.

- Changed dependencies: daml-stdlib 3.3.0.20250502.13767.0 -> 3.5.2, splice-amulet 0.1.20 -> 0.1.21, splice-util 0.1.6 -> 0.1.7, splice-wallet-payments 0.1.20 -> 0.1.21.
- Affected: Splice.Wallet.Install:WalletAppInstall WalletAppInstall_AllocationFactory_Allocate (body); Splice.Wallet.Install:WalletAppInstall WalletAppInstall_TransferFactory_Transfer (body).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_Allocation_Withdraw``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.21 -> 0.1.22, Splice.Wallet.Install:635:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.21 -> 0.1.22, Splice.Wallet.Install:49:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstruction_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.21 -> 0.1.22, Splice.Wallet.Install:587:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstruction_Reject``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.21 -> 0.1.22, Splice.Wallet.Install:597:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_TransferInstruction_Withdraw``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.21 -> 0.1.22, Splice.Wallet.Install:607:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.21 -> 0.1.22, Splice.Wallet.MintingDelegation:22:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.TransferOffer:AcceptedTransferOffer, choice `AcceptedTransferOffer_Complete``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.21 -> 0.1.22, Splice.Wallet.TransferOffer:118 -> Splice.Wallet.TransferOffer:121:

```diff
                    receiverFeeRatio = 0.0
+                   meta = Some (TSU.reasonToMeta "transfer offer accepted" emptyMetadata)
                ]
```

