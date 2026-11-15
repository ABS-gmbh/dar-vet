# dar-vet: splice-dso-governance 0.1.8 -> 0.1.9

Old package ID `1790a114f83d5f290261fae1e7e46fba75a861a3dd603c6b4ef6b67b49053948`, new package ID `9ee83bfd872f91e659b8a8439c5b4eaf240bcf6f19698f884d7d7993ab48c401`.

**11 findings:** 3 HIGH, 6 MEDIUM, 2 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_CreateExternalPartyAmuletRules``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create splice-amulet:Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules.

Version 0.1.9, Splice.DsoRules:1230:

```daml
    nonconsuming choice DsoRules_CreateExternalPartyAmuletRules : DsoRules_CreateExternalPartyAmuletRulesResult
      controller dso
      do cid <- create ExternalPartyAmuletRules with dso
         pure (DsoRules_CreateExternalPartyAmuletRulesResult cid)
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_CreateTransferCommandCounter``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create splice-amulet:Splice.ExternalPartyAmuletRules:TransferCommandCounter.

Version 0.1.9, Splice.DsoRules:1235:

```daml
    nonconsuming choice DsoRules_CreateTransferCommandCounter : DsoRules_CreateTransferCommandCounterResult
      with
        sender : Party
      controller dso
      do cid <- create TransferCommandCounter with
           dso
           sender
           nextNonce = 0
         pure (DsoRules_CreateTransferCommandCounterResult cid)
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireTransferPreapproval``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:TransferPreapproval.TransferPreapproval_Expire.

Version 0.1.9, Splice.DsoRules:1245:

```daml
    nonconsuming choice DsoRules_ExpireTransferPreapproval : DsoRules_ExpireTransferPreapprovalResult
      with
        transferPreapprovalCid : ContractId TransferPreapproval
      controller dsoDelegate
      do
        exercise transferPreapprovalCid TransferPreapproval_Expire
        pure DsoRules_ExpireTransferPreapprovalResult
```

## MEDIUM - dependency-behaviour-changed - `(package)`

2 expressions are unchanged in this package but call dependency code that changed, including 1 that decide authority (controllers, observers, signatories).

- Changed dependencies: splice-amulet 0.1.5 -> 0.1.6, splice-amulet-name-service 0.1.5 -> 0.1.6, splice-wallet-payments 0.1.5 -> 0.1.6.
- Affected: Splice.DsoBootstrap:DsoBootstrap template (ensure clause); Splice.DsoRules:DsoRules DsoRules_PruneAmuletConfigSchedule (body).
- Run dar-vet on the dependency packages to see what changed there.

## MEDIUM - effects-changed - `Splice.DsoBootstrap:DsoBootstrap, choice `DsoBootstrap_Bootstrap``

Ledger effects changed. Added: create splice-amulet:Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.DsoBootstrap:39 -> Splice.DsoBootstrap:40:

```diff
            isDevNet
+         _ <- create ExternalPartyAmuletRules with dso
          _ <- create AnsRules with
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.8 -> 0.1.9, Splice.DsoRules:309:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateExternalPartyAmuletRules, exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateTransferCommandCounter. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.DsoRules:1242 -> Splice.DsoRules:1284:

```diff
        SRARC_UpdateSvRewardWeight choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateExternalPartyAmuletRules choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateTransferCommandCounter choiceArg -> void $ exercise dsoRulesCid choiceArg
    ARC_AnsEntryContext with .. ->
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateExternalPartyAmuletRules, exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateTransferCommandCounter. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.8 -> 0.1.9, Splice.DsoRules:1242 -> Splice.DsoRules:1284:

```diff
        SRARC_UpdateSvRewardWeight choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateExternalPartyAmuletRules choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateTransferCommandCounter choiceArg -> void $ exercise dsoRulesCid choiceArg
    ARC_AnsEntryContext with .. ->
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.8 -> 0.1.9, Splice.DsoRules:377:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.DsoRules:694:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.8 -> 0.1.9, Splice.DsoRules:1178:

```text
(source text identical: the difference comes from code this function calls)
```

