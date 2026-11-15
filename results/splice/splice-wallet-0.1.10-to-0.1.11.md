# dar-vet: splice-wallet 0.1.10 -> 0.1.11

Old package ID `9824ffa414d5803d17efafd75c69c1dd53591ac5197d07048b24dbac1720a462`, new package ID `991842eee48ec3caa3a649e8f47e3544dd7b688ce4b363aa934a83db7dada427`.

**5 findings:** 0 HIGH, 1 MEDIUM, 4 LOW.

## MEDIUM - ensure-changed - `Splice.Wallet.TransferPreapproval:TransferPreapprovalProposal`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.10 -> 0.1.11, Splice.Wallet.TransferPreapproval:9:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.BuyTrafficRequest:BuyTrafficRequest, choice `BuyTrafficRequest_Complete``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.Wallet.BuyTrafficRequest:44 -> Splice.Wallet.BuyTrafficRequest:44:

```diff
          require "Contract has not expired" (now < expiresAt)
+         result <- exercise context.amuletRules AmuletRules_BuyMemberTraffic with
+           inputs
+           context = context.context
+           provider = endUserParty
+           memberId
+           synchronizerId
+           migrationId
+           trafficAmount
+           expectedDso = Some dso
-         result <- exercise context.amuletRules (AmuletRules_BuyMemberTraffic inputs context.context endUserParty memberId synchronizerId migrationId trafficAmount)
          let trackingInfo = BuyTrafficRequestTrackingInfo with ..
```

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeSynchronizerFees, Splice.AmuletRules:exercisePaymentTransfer, Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.10 -> 0.1.11, Splice.Wallet.Install:52 -> Splice.Wallet.Install:52:

```diff
          let transfer = mkMergeAmuletAndRewardsTransfer executionContext.endUser inputs
+         result <- catchAll (exercisePaymentTransfer executionContext.dso context transfer)
-         result <- catchAll (exercisePaymentTransfer context transfer)
          handleResultMergeInputs result
...
        -- creates ExternalPartySetupProposal for externally hosted parties
+       resultE <- catchAll $ handleCreateExternalPartySetupProposal executionContext externalParty preapprovalExpiresAt
-       resultE <- catchAll $ handleCreateExternalPartySetupProposal externalParty preapprovalExpiresAt
        handleResult COO_CreateExternalPartySetupProposal resultE
...
              -- Calculate Amulet required to buy extra traffic
+             (trafficCostAmulet, _) <- computeSynchronizerFees executionContext.dso executionContext.validator trafficAmount amuletRules context.context
-             (trafficCostAmulet, _) <- computeSynchronizerFees executionContext.validator trafficAmount amuletRules context.context
              -- Tap enough Amulet to cover extra traffic purchase
...
        -- purchase extra traffic for sequencer member
+       result <- exercise amuletRulesCid AmuletRules_BuyMemberTraffic with
+         inputs = newInputs
+         context = context.context
+         provider = executionContext.validator
+         memberId
+         synchronizerId
+         migrationId
+         trafficAmount
+         expectedDso = Some executionContext.dso
-       result <- exercise amuletRulesCid
-         (AmuletRules_BuyMemberTraffic newInputs context.context executionContext.validator memberId synchronizerId migrationId trafficAmount)
        pure (result.purchasedTraffic, result.senderChangeAmulet)
  
+     handleCreateExternalPartySetupProposal : ExecutionContext -> Party -> Time -> Update (ContractId ExternalPartySetupProposal, Optional (ContractId Amulet))
+     handleCreateExternalPartySetupProposal executionContext externalParty preapprovalExpiresAt = do
-     handleCreateExternalPartySetupProposal : Party -> Time -> Update (ContractId ExternalPartySetupProposal, Optional (ContractId Amulet))
-     handleCreateExternalPartySetupProposal externalParty preapprovalExpiresAt = do
        require "Only a validator can create a proposal to host an external party" (executionContext.endUser == executionContext.validator)
...
          preapprovalExpiresAt
+         expectedDso = Some executionContext.dso
        pure (proposalCid, transferResult.senderChangeAmulet)
```

## LOW - body-changed - `Splice.Wallet.TransferOffer:AcceptedTransferOffer, choice `AcceptedTransferOffer_Complete``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:exercisePaymentTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

## LOW - body-changed - `Splice.Wallet.TransferPreapproval:TransferPreapprovalProposal, choice `TransferPreapprovalProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.10 -> 0.1.11, Splice.Wallet.TransferPreapproval:17 -> Splice.Wallet.TransferPreapproval:20:

```diff
              expiresAt
+             expectedDso
          return TransferPreapprovalProposal_AcceptResult with ..
```

