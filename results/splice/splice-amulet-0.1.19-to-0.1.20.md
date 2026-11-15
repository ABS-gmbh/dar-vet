# dar-vet: splice-amulet 0.1.19 -> 0.1.20

Old package ID `90987abecbcb1d004b063ddfe3b4b5d46cf3814ce89114a86c8cd75ff3cb8a4b`, new package ID `23f47481dab6b1ec01339d6e14494d85bb2844c25f45b26fc5c9ef4cd4942d1f`.

**19 findings:** 0 HIGH, 5 MEDIUM, 14 LOW.

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.AmuletConfig:126:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Ledger effects changed. Added: fetch self:Splice.AmuletRules:AmuletRules. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1101:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1117:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:2095:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Ledger effects changed. Added: fetch self:Splice.AmuletRules:AmuletRules. Removed: none.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1101:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1117:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:2095:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Expire``

Fewer checks in the choice body (assertions or aborts): 2 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:2030 -> Splice.AmuletRules:2037:

```diff
        controller dso
+       do
+          assertDeadlineExceeded "TransferPreapproval.expiresAt" expiresAt
-       do now <- getTime
-          require "Contract has expired" (now >= expiresAt)
           pure TransferPreapproval_ExpireResult
```

## MEDIUM - effects-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Ledger effects changed. Added: fetch self:Splice.AmuletRules:AmuletRules. Removed: exercise self:Splice.AmuletRules:AmuletRules.AmuletRules_Fetch.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1101:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1117:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:2095:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Amulet.RewardAccountingV2:ProcessRewardsV2, choice `ProcessRewardsV2_ProcessBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Amulet.CryptoHash:hashText, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.Amulet.CryptoHash:101:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_AllocateDevelopmentFundCoupon``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:795:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, Splice.AmuletRules:splitAndBurn, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1101:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1117:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:2095:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ClaimExpiredRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:617:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:886:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_DevNet_Tap``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:352 -> Splice.AmuletRules:358:

```diff
          require "isDevNet flag is true" isDevNet
+         -- Limit the maximal amount that can be tapped in a single transaction to avoid hitting
+         -- limits in downstream processing, e.g., when computing the total supply.
+         require "Tapped amount <= 100,000,000" (amount <= 100000000.0)
          result <- exercise self AmuletRules_Mint with ..
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:297:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:846:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:executeTransfer$u0027, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1101:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1117:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_UpdateExternalPartyConfigStates``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:948:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_SendV2``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeExternalPartyTransfer, Splice.AmuletRules:executeTransfer$u0027, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1108:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.19 -> 0.1.20, Splice.AmuletRules:1117:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_RecordValidatorLivenessActivity``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.ValidatorLicense:74 -> Splice.ValidatorLicense:81:

```diff
  
+         licenseCid <- create this with faucetState = newFaucetState; lastActiveAt = Some now; sponsor = validator
-         licenseCid <- create this with faucetState = newFaucetState; lastActiveAt = Some now
  
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_ReportActive``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.ValidatorLicense:137 -> Splice.ValidatorLicense:145:

```diff
      choice ValidatorLicense_ReportActive : ValidatorLicense_ReportActiveResult
+       -- ^ Choice for validators that do not receive FaucetCoupons to report themselves as active.
-       -- ^ Choice for validators with disabled wallets to report themselves as active.
        -- Validators that receive amulets will report through ReceiveFaucetCoupon.
...
             lastActiveAt = Some now
+            sponsor = validator
           pure (ValidatorLicense_ReportActiveResult licenseCid)
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_UpdateMetadata``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.19 -> 0.1.20, Splice.ValidatorLicense:118 -> Splice.ValidatorLicense:125:

```diff
               lastActiveAt = Some now
+              sponsor = validator
             pure (ValidatorLicense_UpdateMetadataResult licenseCid)
```

