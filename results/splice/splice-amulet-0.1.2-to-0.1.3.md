# dar-vet: splice-amulet 0.1.2 -> 0.1.3

Old package ID `1446ffdf23326cef2de97923df96618eb615792bea36cf1431f03639448f1645`, new package ID `0d89016d5a90eb8bced48bbac99e81c57781b3a36094b8d48b8e4389851e19af`.

**6 findings:** 2 HIGH, 1 MEDIUM, 3 LOW.

## HIGH - new-choice - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_ReportActive``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.ValidatorLicense:ValidatorLicense.

Version 0.1.3, Splice.ValidatorLicense:121:

```daml
    choice ValidatorLicense_ReportActive : ValidatorLicense_ReportActiveResult
      -- ^ Choice for validators with disabled wallets to report themselves as active.
      -- Validators that receive amulets will report through ReceiveFaucetCoupon.
      controller validator
      do now <- getTime
         Foldable.forA_ lastActiveAt $ \activeAt -> do
           require
             ("At least <> show activityReportMinInterval " <> " has passed since the last activity report")
             (activityReportAllowed activeAt now)
         licenseCid <- create this with
           lastActiveAt = Some now
         pure (ValidatorLicense_ReportActiveResult licenseCid)
```

## HIGH - new-choice - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_UpdateMetadata``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.ValidatorLicense:ValidatorLicense.

Version 0.1.3, Splice.ValidatorLicense:102:

```daml
    choice ValidatorLicense_UpdateMetadata : ValidatorLicense_UpdateMetadataResult
      with
        version : Text
        contactPoint : Text
      controller validator
        do now <- getTime
           Foldable.forA_ metadata $ \metadata -> do
             require "Metadata has changed" (version /= metadata.version || contactPoint /= metadata.contactPoint)
             require
               ("At least " <> show metadataUpdateMinInterval <> " has passed since the last metadata update")
               (metadataUpdateAllowed metadata.lastUpdatedAt now)
           licenseCid <- create this with
             metadata = Some ValidatorLicenseMetadata with
               version
               lastUpdatedAt = now
               contactPoint
             lastActiveAt = Some now
           pure (ValidatorLicense_UpdateMetadataResult licenseCid)
```

## MEDIUM - ensure-changed - `Splice.ValidatorLicense:ValidatorLicense`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.2 -> 0.1.3, Splice.ValidatorLicense:13 -> Splice.ValidatorLicense:55:

```diff
+     ensure validValidatorLicense this
-   deriving (Eq, Show)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.2 -> 0.1.3, Splice.AmuletRules:915:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.2 -> 0.1.3, Splice.AmuletRules:605:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.2 -> 0.1.3, Splice.AmuletRules:915:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.2 -> 0.1.3, Splice.AmuletRules:605:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.ValidatorLicense:ValidatorLicense, choice `ValidatorLicense_ReceiveFaucetCoupon``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.2 -> 0.1.3, Splice.ValidatorLicense:37 -> Splice.ValidatorLicense:58:

```diff
  
+         licenseCid <- create this with faucetState = newFaucetState; lastActiveAt = Some now
-         licenseCid <- create this with faucetState = newFaucetState
  
```

