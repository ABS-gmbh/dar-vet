# dar-vet: splice-dso-governance 0.1.13 -> 0.1.14

Old package ID `8c9c0ee6ecfb815038b1e18ae796e1dd2f0fcf1290f0174f35b0f813fc74f74e`, new package ID `0d35cac28a2b22b3092b4f242246b040ba39a52b0c5602cf4dce1d87b7cb3b81`.

**25 findings:** 4 HIGH, 10 MEDIUM, 11 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_AllocateUnallocatedUnclaimedActivityRecord``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create splice-amulet:Splice.Amulet:UnclaimedActivityRecord, create splice-amulet:Splice.Amulet:UnclaimedReward, exercise self:Splice.DsoRules:UnallocatedUnclaimedActivityRecord.Archive, exercise splice-amulet:Splice.Amulet:UnclaimedReward.Archive, fetch self:Splice.DsoRules:UnallocatedUnclaimedActivityRecord, fetch splice-amulet:Splice.Amulet:UnclaimedReward.

Version 0.1.14, Splice.DsoRules:1437:

```daml
    nonconsuming choice DsoRules_AllocateUnallocatedUnclaimedActivityRecord : DsoRules_AllocateUnallocatedUnclaimedActivityRecordResult
      with
        unallocatedUnclaimedActivityRecordCid : ContractId UnallocatedUnclaimedActivityRecord
        amuletRulesCid : ContractId AmuletRules
        unclaimedRewardsToBurnCids : [ContractId UnclaimedReward]
          -- ^ A sufficient list of `UnclaimedReward` to be archived.
          -- It must cover at least the requested amount.
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        UnallocatedUnclaimedActivityRecord{..} <- fetchAndArchive (ForDso with dso) unallocatedUnclaimedActivityRecordCid
        assertWithinDeadline "UnallocatedUnclaimedActivityRecord.expiresAt" expiresAt

        totalUnclaimedRewards <- sum <$> forA unclaimedRewardsToBurnCids \cid -> do
          unclaimedReward <- fetchAndArchive (ForDso with dso) cid
          pure unclaimedReward.amount
        let leftover = totalUnclaimedRewards - amount
        optUnclaimedRewardCid <-
          if | leftover < 0.0 -> abort $ "insufficient unclaimed rewards: " <> show (negate leftover)
             | leftover == 0.0 -> pure None
             | otherwise -> Some <$> create UnclaimedReward with dso; amount = leftover

        unclaimedActivityRecordCid <- create UnclaimedActivityRecord with
          dso
          beneficiary
          amount
          reason
          expiresAt

        pure $ DsoRules_AllocateUnallocatedUnclaimedActivityRecordResult with ..
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_CreateUnallocatedUnclaimedActivityRecord``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.DsoRules:UnallocatedUnclaimedActivityRecord.

Version 0.1.14, Splice.DsoRules:1417:

```daml
    nonconsuming choice DsoRules_CreateUnallocatedUnclaimedActivityRecord : DsoRules_CreateUnallocatedUnclaimedActivityRecordResult
      with
        beneficiary : Party
        amount : Decimal
        reason : Text
        expiresAt : Time
      controller dso
      do
        now <- getTime
        -- Sanity check
        require "UnclaimedActivityRecord expires after current time" $ expiresAt > now
        unallocatedUnclaimedActivityRecordCid <-
          create UnallocatedUnclaimedActivityRecord with
            dso
            beneficiary
            amount
            reason
            expiresAt
        pure $ DsoRules_CreateUnallocatedUnclaimedActivityRecordResult with ..
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireUnallocatedUnclaimedActivityRecord``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise self:Splice.DsoRules:UnallocatedUnclaimedActivityRecord.Archive, fetch self:Splice.DsoRules:UnallocatedUnclaimedActivityRecord.

Version 0.1.14, Splice.DsoRules:1469:

```daml
    nonconsuming choice DsoRules_ExpireUnallocatedUnclaimedActivityRecord : DsoRules_ExpireUnallocatedUnclaimedActivityRecordResult
      with
        unallocatedUnclaimedActivityRecordCid : ContractId UnallocatedUnclaimedActivityRecord
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        unallocatedUnclaimedActivityRecord <- fetchAndArchive (ForDso with dso) unallocatedUnclaimedActivityRecordCid
        assertDeadlineExceeded "UnallocatedUnclaimedActivityRecord.expiresAt" unallocatedUnclaimedActivityRecord.expiresAt
        pure $ DsoRules_ExpireUnallocatedUnclaimedActivityRecordResult
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireUnclaimedActivityRecord``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.Amulet:UnclaimedActivityRecord.UnclaimedActivityRecord_DsoExpire.

Version 0.1.14, Splice.DsoRules:1480:

```daml
    nonconsuming choice DsoRules_ExpireUnclaimedActivityRecord : DsoRules_ExpireUnclaimedActivityRecordResult
      with
        unclaimedActivityRecordCid : ContractId UnclaimedActivityRecord
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        UnclaimedActivityRecord_DsoExpireResult unclaimedRewardCid <-
          exercise unclaimedActivityRecordCid UnclaimedActivityRecord_DsoExpire
        pure DsoRules_ExpireUnclaimedActivityRecordResult with unclaimedRewardCid
```

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.13 -> 0.1.14, Splice.DSO.DecentralizedSynchronizer:88:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.13 -> 0.1.14, Splice.DsoRules:334:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - guard-removed - `Splice.DsoRules:Confirmation, choice `Confirmation_Expire``

Fewer checks in the choice body (assertions or aborts): 2 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:326 -> Splice.DsoRules:343:

```diff
        controller dso
+       do assertDeadlineExceeded "expiresAt" expiresAt
-       do now <- getTime
-          require "Contract has expired" (expiresAt <= now)
           return Confirmation_ExpireResult
```

## MEDIUM - ensure-changed - `Splice.DsoRules:DsoRules`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.13 -> 0.1.14, Splice.DsoRules:454:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateUnallocatedUnclaimedActivityRecord, fetch self:Splice.DsoRules:DsoRules, fetch splice-amulet-name-service:Splice.Ans:AnsEntryContext, fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:1404 -> Splice.DsoRules:1523:

```diff
+ executeActionRequiringConfirmation dso dsoRulesCid amuletRulesCid act = case act of
- executeActionRequiringConfirmation dsoRulesCid amuletRulesCid act = case act of
    ARC_AmuletRules with .. ->
...
        Some amuletRulesCid -> do
+         -- prudent engineering: check that the DSO party is as expected
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          case amuletRulesAction of
...
            CRARC_UpdateFutureAmuletConfigSchedule choiceArg -> void $ exercise amuletRulesCid choiceArg
+   ARC_DsoRules with .. -> do
+     void $ fetchChecked (ForDso with dso) dsoRulesCid
-   ARC_DsoRules with .. ->
      case dsoAction of
...
        SRARC_CreateTransferCommandCounter choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateUnallocatedUnclaimedActivityRecord choiceArg -> void $ exercise dsoRulesCid choiceArg
+   ARC_AnsEntryContext with .. -> do
+     void $ fetchChecked (ForDso with dso) ansEntryContextCid
-   ARC_AnsEntryContext with .. ->
      case ansEntryContextAction of
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateUnallocatedUnclaimedActivityRecord, fetch self:Splice.DsoRules:DsoRules, fetch splice-amulet-name-service:Splice.Ans:AnsEntryContext, fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:1404 -> Splice.DsoRules:1523:

```diff
+ executeActionRequiringConfirmation dso dsoRulesCid amuletRulesCid act = case act of
- executeActionRequiringConfirmation dsoRulesCid amuletRulesCid act = case act of
    ARC_AmuletRules with .. ->
...
        Some amuletRulesCid -> do
+         -- prudent engineering: check that the DSO party is as expected
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          case amuletRulesAction of
...
            CRARC_UpdateFutureAmuletConfigSchedule choiceArg -> void $ exercise amuletRulesCid choiceArg
+   ARC_DsoRules with .. -> do
+     void $ fetchChecked (ForDso with dso) dsoRulesCid
-   ARC_DsoRules with .. ->
      case dsoAction of
...
        SRARC_CreateTransferCommandCounter choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateUnallocatedUnclaimedActivityRecord choiceArg -> void $ exercise dsoRulesCid choiceArg
+   ARC_AnsEntryContext with .. -> do
+     void $ fetchChecked (ForDso with dso) ansEntryContextCid
-   ARC_AnsEntryContext with .. ->
      case ansEntryContextAction of
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Ledger effects changed. Added: exercise self:Splice.DsoRules:Confirmation.Confirmation_Expire. Removed: exercise self:Splice.DsoRules:Confirmation.Archive.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:1276 -> Splice.DsoRules:1318:

```diff
          _ <- getAndValidateSvParty this sv
+         fetchChecked (ForDso with dso) staleConfirmationCid
+         exercise staleConfirmationCid Confirmation_Expire
-         now <- getTime
-         staleConfirmation <- fetchAndArchive (ForDso with dso) staleConfirmationCid
-         require "staleConfirmation is past expiresAt" (staleConfirmation.expiresAt <= now)
          return DsoRules_ExpireStaleConfirmationResult
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Fewer checks in the choice body (assertions or aborts): 5 before, 4 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:1276 -> Splice.DsoRules:1318:

```diff
          _ <- getAndValidateSvParty this sv
+         fetchChecked (ForDso with dso) staleConfirmationCid
+         exercise staleConfirmationCid Confirmation_Expire
-         now <- getTime
-         staleConfirmation <- fetchAndArchive (ForDso with dso) staleConfirmationCid
-         require "staleConfirmation is past expiresAt" (staleConfirmation.expiresAt <= now)
          return DsoRules_ExpireStaleConfirmationResult
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_RevokeFeaturedAppRight``

Ledger effects changed. Added: exercise splice-amulet:Splice.Amulet:FeaturedAppRight.FeaturedAppRight_Withdraw. Removed: exercise splice-amulet:Splice.Amulet:FeaturedAppRight.Archive.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:939 -> Splice.DsoRules:979:

```diff
        do
+         void $ fetchChecked (ForDso with dso) rightCid
+         exercise rightCid FeaturedAppRight_Withdraw with
+           reason = "revoked by governance decision"
-         void $ fetchAndArchive (ForDso with dso) rightCid
          return DsoRules_RevokeFeaturedAppRightResult
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.13 -> 0.1.14, Splice.DsoRules:374:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - dependency-behaviour-changed - `(package)`

2 expressions are unchanged in this package but call dependency code that changed.

- Changed dependencies: splice-amulet 0.1.9 -> 0.1.10, splice-amulet-name-service 0.1.9 -> 0.1.10, splice-wallet-payments 0.1.9 -> 0.1.10.
- Affected: Splice.DsoRules:DsoRules DsoRules_PruneAmuletConfigSchedule (body); Splice.DsoRules:DsoRules DsoRules_ReceiveSvRewardCoupon (body).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - body-changed - `Splice.DsoBootstrap:DsoBootstrap, choice `DsoBootstrap_Bootstrap``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DsoRules:createPerSvPartyContracts, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:1607 -> Splice.DsoRules:1731:

```diff
+ createPerSvPartyContracts dso newSvParty newSvName synchronizerNodes amuletPrice voteCooldownTime = do
- createPerSvPartyContracts dso newSvParty newSvName synchronizerNodes amuletPrice = do
-   now <- getTime
    -- Note: we currently track the amulet-price vote on a per operator basis.
    -- This implies it will be reset when an operator is offboarded and re-onboarded.
+   now <- getTime
    void $ create AmuletPriceVote with
...
      amuletPrice
+     lastUpdatedAt =
+       -- allow casting a vote immediately after onboarding
+       now `addRelTime` negate voteCooldownTime
-     lastUpdatedAt = now
    void $ create SvStatusReport with
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AddConfirmedSv``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:512 -> Splice.DsoRules:535:

```diff
          amuletRulesCid: ContractId AmuletRules
+         -- No longer used, but must be kept for smart-contract upgrading compatibility.
+         -- (This was used when the current round number was tracked in the AmuletRules contract.)
        controller sv
...
          svOnboardingConfirmed <- fetchAndArchive (ForOwner with dso; owner = sv) svOnboardingConfirmedCid
+         assertWithinDeadline "svOnboardingConfirmed.expiresAt" svOnboardingConfirmed.expiresAt
  
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AddSv``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.DsoRules:createPerSvPartyContracts, Splice.DsoRules:dsoRules_addSv, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:1607 -> Splice.DsoRules:1731:

```diff
+ createPerSvPartyContracts dso newSvParty newSvName synchronizerNodes amuletPrice voteCooldownTime = do
- createPerSvPartyContracts dso newSvParty newSvName synchronizerNodes amuletPrice = do
-   now <- getTime
    -- Note: we currently track the amulet-price vote on a per operator basis.
    -- This implies it will be reset when an operator is offboarded and re-onboarded.
+   now <- getTime
    void $ create AmuletPriceVote with
...
      amuletPrice
+     lastUpdatedAt =
+       -- allow casting a vote immediately after onboarding
+       now `addRelTime` negate voteCooldownTime
-     lastUpdatedAt = now
    void $ create SvStatusReport with
```

Version 0.1.13 -> 0.1.14, Splice.DsoRules:1560 -> Splice.DsoRules:1684:

```diff
    let initialAmuletPriceVote = None
+   createPerSvPartyContracts dso newSvParty newSvName noSynchronizerNodes initialAmuletPriceVote (getVoteCooldownTime config)
-   createPerSvPartyContracts dso newSvParty newSvName noSynchronizerNodes initialAmuletPriceVote
    -- register the new operator in the DsoRules
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:711 -> Splice.DsoRules:741:

```diff
          request <- fetchAndArchive (ForDso with dso) requestCid
+         -- rate limit casting of votes by the same SV to avoid them blocking others from making progress
+         -- Note: we currently ignore the optional self-declared vote casting time
+         -- `vote.optCastAt`. We'll use that in the future when adding support for larger
+         -- prepare-submission delays for external parties.
+         enforceCooldown ("voteCooldownTime for " <> partyToText vote.sv) (getVoteCooldownTime config) $ do
+           pastVote <- Map.lookup voterName request.votes
+           pastVote.optCastAt
          -- store vote
+         now <- getTime
          voteRequest <- create request with
+           votes = Map.insert voterName (vote with optCastAt = Some now) request.votes
-           votes = Map.insert voterName vote request.votes
            trackingCid = Some (fromOptional requestCid request.trackingCid)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ElectDsoDelegate``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:590 -> Splice.DsoRules:617:

```diff
              numVoters = numVotes
-             -- TODO(#7649): consider whether that is the right number of votes to require
              -- as there might be more votes being taken into consideration
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeValidatorLicense``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:1013:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_RequestVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:673 -> Splice.DsoRules:703:

```diff
                reason = Reason with url = ""; body = "I accept, as I requested the vote."
+               optCastAt = Some now
          voteRequest <- create VoteRequest with
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:936:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_UpdateAmuletPriceVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.13 -> 0.1.14, Splice.DsoRules:833 -> Splice.DsoRules:871:

```diff
        do
+         pastVote <- fetchAndArchive (ForOwner with dso; owner = sv) voteCid
+         -- rate limit, so that an SV cannot block round advancement by changing their price vote too often
+         enforceCooldown ("voteCooldownTime for " <> partyToText sv) (getVoteCooldownTime config) (Some pastVote.lastUpdatedAt)
-         void $ fetchAndArchive (ForOwner with dso; owner = sv) voteCid
          now <- getTime
```

## LOW - template-added - `Splice.DsoRules:UnallocatedUnclaimedActivityRecord`

New template. Review it like a new package.

