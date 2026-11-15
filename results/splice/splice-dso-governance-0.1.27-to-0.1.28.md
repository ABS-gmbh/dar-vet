# dar-vet: splice-dso-governance 0.1.27 -> 0.1.28

Old package ID `014473d35514cb36583bdeca269386e46ae4e2283fe9b04beb2f53cdb408e1a2`, new package ID `eeb461e9e430d8aaaa232f23058a28c9858d559abd083c391d7ab7e73efb4e13`.

**9 findings:** 1 HIGH, 4 MEDIUM, 4 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_UpdateFeaturedAppRight``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.Amulet:FeaturedAppRight.FeaturedAppRight_Update, fetch splice-amulet:Splice.Amulet:FeaturedAppRight.

Version 0.1.28, Splice.DsoRules:1036:

```daml
    nonconsuming choice DsoRules_UpdateFeaturedAppRight : DsoRules_UpdateFeaturedAppRightResult
      with
        rightCid : ContractId FeaturedAppRight
        update : FeaturedAppRight_Update
      controller dso
      do
        void $ fetchChecked (ForDso with dso) rightCid
        result <- exercise rightCid update
        return DsoRules_UpdateFeaturedAppRightResult with result
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.27 -> 0.1.28, Splice.DsoRules:393:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_UpdateFeaturedAppRight. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.27 -> 0.1.28, Splice.DsoRules:1773 -> Splice.DsoRules:1790:

```diff
        SRARC_RevokeFeaturedAppRight choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_UpdateFeaturedAppRight choiceArg -> void $ exercise dsoRulesCid choiceArg
        SRARC_SetConfig choiceArg -> void $ exercise dsoRulesCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_UpdateFeaturedAppRight. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.27 -> 0.1.28, Splice.DsoRules:1773 -> Splice.DsoRules:1790:

```diff
        SRARC_RevokeFeaturedAppRight choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_UpdateFeaturedAppRight choiceArg -> void $ exercise dsoRulesCid choiceArg
        SRARC_SetConfig choiceArg -> void $ exercise dsoRulesCid choiceArg
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.27 -> 0.1.28, Splice.DsoRules:432:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.27 -> 0.1.28, Splice.DsoRules:792:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.27 -> 0.1.28, Splice.DsoRules:1570:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_GrantFeaturedAppRight``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.27 -> 0.1.28, Splice.DsoRules:1009 -> Splice.DsoRules:1014:

```diff
          provider : Party
+         activityWeight : Optional Decimal
        controller dso
...
            provider
+           activityWeight
          return DsoRules_GrantFeaturedAppRightResult with ..
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_RevokeFeaturedAppRight``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.27 -> 0.1.28, Splice.DsoRules:1026:

```text
(source text identical: the difference comes from code this function calls)
```

