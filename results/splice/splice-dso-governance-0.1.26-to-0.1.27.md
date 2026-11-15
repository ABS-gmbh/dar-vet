# dar-vet: splice-dso-governance 0.1.26 -> 0.1.27

Old package ID `45099e955ce443f7895125097a62c509b9ad297091c91d6377ffe94f183c0e3e`, new package ID `014473d35514cb36583bdeca269386e46ae4e2283fe9b04beb2f53cdb408e1a2`.

**3 findings:** 1 HIGH, 1 MEDIUM, 1 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireAmuletAllocationsV2``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules.ExternalPartyAmuletRules_ExpireAmuletAllocationsV2, fetch splice-amulet:Splice.ExternalPartyAmuletRules:ExternalPartyAmuletRules.

Version 0.1.27, Splice.DsoRules:1177:

```daml
    nonconsuming choice DsoRules_ExpireAmuletAllocationsV2 : DsoRules_ExpireAmuletAllocationsV2Result
      with
        extAmuletRulesCid : ContractId ExternalPartyAmuletRules
        choiceArg : ExternalPartyAmuletRules_ExpireAmuletAllocationsV2
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        _ <- fetchChecked (ForDso with dso) extAmuletRulesCid
        result <- exercise extAmuletRulesCid choiceArg
        pure DsoRules_ExpireAmuletAllocationsV2Result with result
```

## MEDIUM - dependency-behaviour-changed - `(package)`

9 expressions are unchanged in this package but call dependency code that changed, including 3 that decide authority (controllers, observers, signatories).

- Changed dependencies: daml-stdlib 3.3.0.20250502.13767.0 -> 3.5.2, splice-amulet 0.1.20 -> 0.1.21, splice-amulet-name-service 0.1.21 -> 0.1.22, splice-util 0.1.6 -> 0.1.7, splice-wallet-payments 0.1.20 -> 0.1.21.
- Affected: Splice.DsoBootstrap:DsoBootstrap template (ensure clause); Splice.DsoRules:Confirmation template (ensure clause); Splice.DsoRules:DsoRules DsoRules_AddConfirmedSv (body); Splice.DsoRules:DsoRules DsoRules_CastVote (body); Splice.DsoRules:DsoRules DsoRules_CloseVoteRequest (body); Splice.DsoRules:DsoRules DsoRules_ExecuteConfirmedAction (body); Splice.DsoRules:DsoRules DsoRules_ExpireStaleConfirmation (body); Splice.DsoRules:DsoRules DsoRules_ReceiveSvRewardCoupon (body); and 1 more.
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireAmuletAllocations``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.26 -> 0.1.27, Splice.DsoRules:1158 -> Splice.DsoRules:1164:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         TSU.requireMatchExpected  ("expireAllocations.expectedDso", expireAllocations.expectedDso) dso
  
-         require ("expectedDso " <> show expireAllocations.expectedDso <> " matches the actualDso " <> show dso) (expireAllocations.expectedDso == dso)
- 
          res <- exercise extAmuletRulesCid (expireAllocations with expectedDso = dso)
- 
          pure DsoRules_ExpireAmuletAllocationsResult with result = res
```

