# dar-vet: splice-dso-governance 0.1.25 -> 0.1.26

Old package ID `dfe102514acd41f79945b8363ca84d8243be97b2b235522ce77c754a36bb8479`, new package ID `45099e955ce443f7895125097a62c509b9ad297091c91d6377ffe94f183c0e3e`.

**2 findings:** 0 HIGH, 1 MEDIUM, 1 LOW.

## MEDIUM - dependency-behaviour-changed - `(package)`

7 expressions are unchanged in this package but call dependency code that changed, including 3 that decide authority (controllers, observers, signatories).

- Changed dependencies: splice-amulet 0.1.19 -> 0.1.20, splice-amulet-name-service 0.1.20 -> 0.1.21, splice-wallet-payments 0.1.19 -> 0.1.20.
- Affected: Splice.DsoBootstrap:DsoBootstrap template (ensure clause); Splice.DsoRules:Confirmation template (ensure clause); Splice.DsoRules:DsoRules DsoRules_CastVote (body); Splice.DsoRules:DsoRules DsoRules_CloseVoteRequest (body); Splice.DsoRules:DsoRules DsoRules_ExecuteConfirmedAction (body); Splice.DsoRules:DsoRules DsoRules_ExpireStaleConfirmation (body); Splice.DsoRules:VoteRequest template (ensure clause).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_OnboardValidator``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.25 -> 0.1.26, Splice.DsoRules:1027 -> Splice.DsoRules:1027:

```diff
          require "Sponsor is an SV" (sponsor `Map.member` svs)
-         -- create license that records in particular which SV sponsored the onboarding of the validator
          now <- getTime
...
            dso
+           -- Sponsoring is no longer tracked since the network is out of the bootstrapping phase.
+           sponsor = validator
-           sponsor
            validator
```

