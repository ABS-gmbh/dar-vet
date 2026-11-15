# dar-vet: splice-dso-governance 0.1.7 -> 0.1.8

Old package ID `d406eba1132d464605f4dae3edf8cf5ecbbb34bd8edef0e047e7e526d328718c`, new package ID `1790a114f83d5f290261fae1e7e46fba75a861a3dd603c6b4ef6b67b49053948`.

**3 findings:** 1 HIGH, 0 MEDIUM, 2 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeValidatorLicense``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create splice-amulet:Splice.ValidatorLicense:ValidatorLicense, exercise splice-amulet:Splice.ValidatorLicense:ValidatorLicense.Archive, fetch splice-amulet:Splice.ValidatorLicense:ValidatorLicense.

Version 0.1.8, Splice.DsoRules:883:

```daml
    nonconsuming choice DsoRules_MergeValidatorLicense : DsoRules_MergeValidatorLicenseResult
      -- ^ Note: removes the old duplicated licenses and creates a new one with the highest lastReceivedFor round
      -- There should never be duplicates going forward.
      with
        validatorLicenseCids : [ContractId ValidatorLicense]
      controller dsoDelegate
      do require "Number of validatorLicense contracts to merge is >= 2" (length validatorLicenseCids >= 2)
         validatorLicenses <- forA validatorLicenseCids $ fetchAndArchive (ForDso this.dso)
         require "All validatorLicenses map to the same validator" ( length (dedup (map (.validator) validatorLicenses)) == 1)
         -- We don't attempt to merge the fields in the validator licence since there is no good
         -- way to do so and none of the fields other than lastReceivedFor round are crucial and for that
         -- taking the max is the correct merge.
         cid <- create $ maximumOn (\license ->
                     case license.faucetState of
                         None    -> Round 0
                         Some r  -> r.lastReceivedFor) validatorLicenses
         pure $ DsoRules_MergeValidatorLicenseResult cid
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.7 -> 0.1.8, Splice.DsoRules:700:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ConfirmSvOnboarding``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.7 -> 0.1.8, Splice.DsoRules:940:

```text
(source text identical: the difference comes from code this function calls)
```

