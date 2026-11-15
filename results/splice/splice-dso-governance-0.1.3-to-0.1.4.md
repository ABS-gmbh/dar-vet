# dar-vet: splice-dso-governance 0.1.3 -> 0.1.4

Old package ID `b0ae3cc03e418790305a3c15f761fe495572de5827f8d322fb8b96996b783c13`, new package ID `dc24fd18b4d151cd1e0ff6bfb7438bafb2f50fe076d0f16f50565e60b153a0be`.

**11 findings:** 1 HIGH, 6 MEDIUM, 4 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_UpdateSvRewardWeight``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.DsoRules:DsoRules.

Version 0.1.4, Splice.DsoRules:811:

```daml
    choice DsoRules_UpdateSvRewardWeight : DsoRules_UpdateSvRewardWeightResult
      with
        svParty : Party
        newRewardWeight : Int
      controller dso
      do
        require "New reward weight is positive" (newRewardWeight >= 0)
        case Map.lookup svParty svs of
          None -> fail "SV party is not registered"
          Some sv -> do
            let newSv = sv with svRewardWeight = newRewardWeight
            let newSvs = Map.insert svParty newSv svs
            newDsoRules <- create this with svs = newSvs
            return DsoRules_UpdateSvRewardWeightResult with ..
```

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.3 -> 0.1.4, Splice.DSO.DecentralizedSynchronizer:86:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.3 -> 0.1.4, Splice.DsoRules:282:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:DsoRules`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.3 -> 0.1.4, Splice.DsoRules:393:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_UpdateSvRewardWeight. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.3 -> 0.1.4, Splice.DsoRules:1172 -> Splice.DsoRules:1194:

```diff
        SRARC_SetConfig choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_UpdateSvRewardWeight choiceArg -> void $ exercise dsoRulesCid choiceArg
    ARC_AnsEntryContext with .. ->
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_UpdateSvRewardWeight. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.3 -> 0.1.4, Splice.DsoRules:1172 -> Splice.DsoRules:1194:

```diff
        SRARC_SetConfig choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_UpdateSvRewardWeight choiceArg -> void $ exercise dsoRulesCid choiceArg
    ARC_AnsEntryContext with .. ->
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.3 -> 0.1.4, Splice.DsoRules:350:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.3 -> 0.1.4, Splice.DsoRules:667:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ConfirmSvOnboarding``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.3 -> 0.1.4, Splice.DsoRules:912:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.3 -> 0.1.4, Splice.DsoRules:1133:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_OnboardValidator``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.3 -> 0.1.4, Splice.DsoRules:836 -> Splice.DsoRules:853:

```diff
          validator : Party
+         version : Optional Text
+         contactPoint : Optional Text
        controller sponsor
...
          -- create license that records in particular which SV sponsored the onboarding of the validator
+         now <- getTime
          validatorLicense <- create ValidatorLicense with
...
            faucetState = None
+           metadata = fmap (\version -> ValidatorLicenseMetadata with version; contactPoint = fromOptional "" contactPoint; lastUpdatedAt = now) version
+           lastActiveAt = Some now
          return DsoRules_OnboardValidatorResult with ..
```

