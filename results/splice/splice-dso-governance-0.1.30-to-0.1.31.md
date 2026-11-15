# dar-vet: splice-dso-governance 0.1.30 -> 0.1.31

Old package ID `428481839b01066a341207a865c77dcf2f16e7058a080d779527f506d39bb983`, new package ID `0ff9141699e5eb734de1e1a8d11f97233ffd3892d1108872600cb3019fd4a86e`.

**43 findings:** 7 HIGH, 27 MEDIUM, 9 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_CreateFeaturedAppConfigUpdateInstruction``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Splice.DSO.Instruction:FeaturedAppConfigUpdateInstruction.

Version 0.1.31, Splice.DsoRules:1138:

```daml
    nonconsuming choice DsoRules_CreateFeaturedAppConfigUpdateInstruction : DsoRules_CreateFeaturedAppConfigUpdateInstructionResult
      with
        updates : [(Party, FeaturedAppRight_UpdateConfig)]
      controller dso
      do
        now <- getTime
        let expiresAt = now `addRelTime` config.voteRequestTimeout
        -- We don't check for duplicate provider here as that would prevent vote execution.
        -- The sequence of execution of update instructions is not guaranteed,
        -- and thus more than one update for a provider should be avoided.
        instructionCids <- forA updates $ \(provider, update) ->
          create FeaturedAppConfigUpdateInstruction with
            dso; provider; update; expiresAt
        pure DsoRules_CreateFeaturedAppConfigUpdateInstructionResult with
          instructionCids
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteFeaturedAppConfigUpdateInstruction``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise self:Splice.DSO.Instruction:FeaturedAppConfigUpdateInstruction.Archive, exercise splice-amulet:Splice.Amulet:FeaturedAppRight.FeaturedAppRight_UpdateConfig, fetch self:Splice.DSO.Instruction:FeaturedAppConfigUpdateInstruction, fetch splice-amulet:Splice.Amulet:FeaturedAppRight.

Version 0.1.31, Splice.DsoRules:1154:

```daml
    nonconsuming choice DsoRules_ExecuteFeaturedAppConfigUpdateInstruction : DsoRules_ExecuteFeaturedAppConfigUpdateInstructionResult
      with
        instructionCid : ContractId FeaturedAppConfigUpdateInstruction
        rightCid : ContractId FeaturedAppRight
        sv : Party
      controller sv
      do
        void $ getAndValidateSvParty this (Some sv)
        instruction <- fetchAndArchive (ForDso dso) instructionCid
        void $ fetchChecked (ForOwner with dso; owner = instruction.provider) rightCid
        result <- exercise rightCid instruction.update
        pure DsoRules_ExecuteFeaturedAppConfigUpdateInstructionResult with result
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireFeaturedAppConfigUpdateInstruction``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise self:Splice.DSO.Instruction:FeaturedAppConfigUpdateInstruction.FeaturedAppConfigUpdateInstruction_Expire, fetch self:Splice.DSO.Instruction:FeaturedAppConfigUpdateInstruction.

Version 0.1.31, Splice.DsoRules:1167:

```daml
    nonconsuming choice DsoRules_ExpireFeaturedAppConfigUpdateInstruction : DsoRules_ExpireFeaturedAppConfigUpdateInstructionResult
      with
        instructionCid : ContractId FeaturedAppConfigUpdateInstruction
        sv : Party
      controller sv
      do
        void $ getAndValidateSvParty this (Some sv)
        void $ fetchChecked (ForDso dso) instructionCid
        void $ exercise instructionCid FeaturedAppConfigUpdateInstruction_Expire
        pure DsoRules_ExpireFeaturedAppConfigUpdateInstructionResult
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_GovernanceLockSubstitution_DsoExpire``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.GovernanceLock.Substitution:GovernanceLockSubstitution.GovernanceLockSubstitution_DsoExpire, fetch splice-amulet:Splice.GovernanceLock.Substitution:GovernanceLockSubstitution.

Version 0.1.31, Splice.DsoRules:1730:

```daml
    nonconsuming choice DsoRules_GovernanceLockSubstitution_DsoExpire : DsoRules_GovernanceLockSubstitution_DsoExpireResult
      -- ^ Withdraws an expired substitution proposal, so that SVs can garbage-collect
      -- proposals that nobody accepted or withdrew, returning the funds to their owner.
      with
        substitutionCid : ContractId GovernanceLockSubstitution
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        void $ fetchChecked (ForDso with dso) substitutionCid
        _ <- exercise substitutionCid GovernanceLockSubstitution_DsoExpire
        pure $ DsoRules_GovernanceLockSubstitution_DsoExpireResult
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_GovernanceLockTransfer_DsoExpire``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.GovernanceLock.Transfer:GovernanceLockTransfer.GovernanceLockTransfer_DsoExpire, fetch splice-amulet:Splice.GovernanceLock.Transfer:GovernanceLockTransfer.

Version 0.1.31, Splice.DsoRules:1743:

```daml
    nonconsuming choice DsoRules_GovernanceLockTransfer_DsoExpire : DsoRules_GovernanceLockTransfer_DsoExpireResult
      -- ^ Expires a lock transfer proposal that the receiver never accepted, so
      -- that SVs can garbage-collect proposals left behind by their senders.
      with
        transferCid : ContractId GovernanceLockTransfer
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        void $ fetchChecked (ForDso with dso) transferCid
        _ <- exercise transferCid GovernanceLockTransfer_DsoExpire
        pure $ DsoRules_GovernanceLockTransfer_DsoExpireResult
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_GovernanceLock_ConvertProvisionalFeaturedAppLock``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.GovernanceLock:GovernanceLock.GovernanceLock_DsoConvertProvisionalFeaturedAppLock, fetch splice-amulet:Splice.GovernanceLock:GovernanceLock.

Version 0.1.31, Splice.DsoRules:1704:

```daml
    nonconsuming choice DsoRules_GovernanceLock_ConvertProvisionalFeaturedAppLock : DsoRules_GovernanceLock_ConvertProvisionalFeaturedAppLockResult
      -- ^ Converts a provisional featured app `GovernanceLock` to a true featured app lock.
      with
        governanceLockCid : ContractId GovernanceLock
        featuredAppRightCid : ContractId FeaturedAppRight
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        void $ fetchChecked (ForDso with dso) governanceLockCid
        exercise governanceLockCid GovernanceLock_DsoConvertProvisionalFeaturedAppLock with featuredAppRightCid
        pure DsoRules_GovernanceLock_ConvertProvisionalFeaturedAppLockResult
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_GovernanceLock_DsoExpire``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.GovernanceLock:GovernanceLock.GovernanceLock_DsoExpire, fetch splice-amulet:Splice.GovernanceLock:GovernanceLock.

Version 0.1.31, Splice.DsoRules:1717:

```daml
    nonconsuming choice DsoRules_GovernanceLock_DsoExpire : DsoRules_GovernanceLock_DsoExpireResult
      -- ^ Expires a fully vested `GovernanceLock`, so that SVs can garbage-collect
      -- locks that nobody has bothered to withdraw.
      with
        governanceLockCid : ContractId GovernanceLock
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        void $ fetchChecked (ForDso with dso) governanceLockCid
        exercise governanceLockCid GovernanceLock_DsoExpire
        pure $ DsoRules_GovernanceLock_DsoExpireResult
```

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.30 -> 0.1.31, Splice.DSO.DecentralizedSynchronizer:80:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.30 -> 0.1.31, Splice.DsoRules:479:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AdvanceOpenMiningRounds``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1325 -> Splice.DsoRules:1403:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          amuletPrice <- fetchMedianAmuletPrice dso (Map.keys svs) amuletPriceVoteCids
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkers``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1708 -> Splice.DsoRules:1855:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          result <- exercise amuletRulesCid argument
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_Amulet_ExpireV2``

Ledger effects changed. Added: fetch splice-amulet:Splice.Amulet:Amulet. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1285 -> Splice.DsoRules:1360:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) cid
          result <- exercise cid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveDryRunRewardAccountingV2``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1562 -> Splice.DsoRules:1650:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          result <- exercise amuletRulesCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ClaimExpiredRewardsV2``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1525 -> Splice.DsoRules:1610:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          result <- exercise amuletRulesCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateFeaturedAppConfigUpdateInstruction. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1849 -> Splice.DsoRules:1998:

```diff
        SRARC_CreateBootstrapExternalPartyConfigStateInstruction choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateFeaturedAppConfigUpdateInstruction choiceArg -> void $ exercise dsoRulesCid choiceArg
    ARC_AnsEntryContext with .. -> do
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CollectEntryRenewalPayment``

Ledger effects changed. Added: fetch splice-amulet-name-service:Splice.Ans:AnsEntryContext. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1360 -> Splice.DsoRules:1440:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) ansEntryContextCid
          summary <- exercise ansEntryContextCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Ledger effects changed. Added: exercise self:Splice.DsoRules:DsoRules.DsoRules_CreateFeaturedAppConfigUpdateInstruction. Removed: none.

- The change is inside the helper function(s) Splice.DsoRules:executeActionRequiringConfirmation, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1849 -> Splice.DsoRules:1998:

```diff
        SRARC_CreateBootstrapExternalPartyConfigStateInstruction choiceArg -> void $ exercise dsoRulesCid choiceArg
+       SRARC_CreateFeaturedAppConfigUpdateInstruction choiceArg -> void $ exercise dsoRulesCid choiceArg
    ARC_AnsEntryContext with .. -> do
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireAnsEntry``

Ledger effects changed. Added: fetch splice-amulet-name-service:Splice.Ans:AnsEntry. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1373 -> Splice.DsoRules:1454:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) ansEntryCid
          exercise ansEntryCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireDevelopmentFundCoupon``

Ledger effects changed. Added: fetch splice-amulet:Splice.Amulet:DevelopmentFundCoupon. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1601 -> Splice.DsoRules:1692:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         void $ fetchChecked (ForDso with dso) developmentFundCouponCid
          result <- exercise developmentFundCouponCid DevelopmentFundCoupon_DsoExpire
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSubscription``

Ledger effects changed. Added: fetch splice-amulet-name-service:Splice.Ans:AnsEntryContext, fetch splice-wallet-payments:Splice.Wallet.Subscriptions:SubscriptionIdleState. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1384 -> Splice.DsoRules:1466:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) subscriptionIdleStateCid
+         void $ fetchChecked (ForDso with dso) ansEntryContextCid
          result <- exercise subscriptionIdleStateCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSvOnboardingRequest``

Ledger effects changed. Added: fetch self:Splice.SvOnboarding:SvOnboardingRequest. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1168 -> Splice.DsoRules:1241:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) cid
          exercise cid SvOnboardingRequest_Expire
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireTransferPreapproval``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:TransferPreapproval. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1698 -> Splice.DsoRules:1844:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) transferPreapprovalCid
          exercise transferPreapprovalCid TransferPreapproval_Expire
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireUnclaimedActivityRecord``

Ledger effects changed. Added: fetch splice-amulet:Splice.Amulet:UnclaimedActivityRecord. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1785 -> Splice.DsoRules:1933:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         void $ fetchChecked (ForDso with dso) unclaimedActivityRecordCid
          UnclaimedActivityRecord_DsoExpireResult unclaimedRewardCid <-
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_LockedAmulet_ExpireAmulet``

Ledger effects changed. Added: fetch splice-amulet:Splice.Amulet:LockedAmulet. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1297 -> Splice.DsoRules:1373:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) cid
          result <- exercise cid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_LockedAmulet_ExpireAmuletV2``

Ledger effects changed. Added: fetch splice-amulet:Splice.Amulet:LockedAmulet. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1309 -> Splice.DsoRules:1386:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) cid
          result <- exercise cid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeMemberTrafficContracts``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1642 -> Splice.DsoRules:1787:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          require "Number of member traffic contracts is above the configured threshold" $ length trafficCids > config.numMemberTrafficContractsThreshold
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeUnclaimedDevelopmentFundCoupons``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1589 -> Splice.DsoRules:1679:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          result <- exercise amuletRulesCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeUnclaimedRewards``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1576 -> Splice.DsoRules:1665:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          require "Number of unclaimed rewards is above the configured threshold" $ length unclaimedRewardCids > config.numUnclaimedRewardsThreshold
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MiningRound_Close``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1612 -> Splice.DsoRules:1756:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          result <- exercise amuletRulesCid AmuletRules_MiningRound_Close with ..
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ProcessRewardsV2_ProcessBatch``

Ledger effects changed. Added: fetch splice-amulet:Splice.Amulet.RewardAccountingV2:ProcessRewardsV2. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1537 -> Splice.DsoRules:1623:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         void $ fetchChecked (ForDso with dso) processRewardsCid
          result <- exercise processRewardsCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_TerminateSubscription``

Ledger effects changed. Added: fetch splice-amulet-name-service:Splice.Ans:AnsEntryContext. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1397 -> Splice.DsoRules:1481:

```diff
          _ <- getAndValidateSvParty this sv
+         void $ fetchChecked (ForDso with dso) ansEntryContextCid
          exercise ansEntryContextCid (AnsEntryContext_Terminate dso terminatedSubscriptionCid)
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_UnhideRewardCouponsV2``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1549 -> Splice.DsoRules:1636:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          result <- exercise amuletRulesCid choiceArg
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_UpdateExternalPartyConfigStates``

Ledger effects changed. Added: fetch splice-amulet:Splice.AmuletRules:AmuletRules. Removed: none.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1342 -> Splice.DsoRules:1421:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         void $ fetchChecked (ForDso with dso) amuletRulesCid
          result <- exercise amuletRulesCid AmuletRules_UpdateExternalPartyConfigStates with ..
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.30 -> 0.1.31, Splice.DsoRules:518:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - dependency-behaviour-changed - `(package)`

4 expressions are unchanged in this package but call dependency code that changed.

- Changed dependencies: daml-stdlib 3.5.2 -> 3.4.11, splice-amulet 0.1.24 -> 0.1.25, splice-amulet-name-service 0.1.25 -> 0.1.26, splice-token-standard-utils 2.0.0 -> 2.0.1, splice-util 0.1.8 -> 0.1.9, splice-wallet-payments 0.1.24 -> 0.1.25.
- Affected: Splice.DsoRules:DsoRules DsoRules_AddConfirmedSv (body); Splice.DsoRules:DsoRules DsoRules_ReceiveSvRewardCoupon (body); Splice.DsoRules:DsoRules DsoRules_RevokeFeaturedAppRight (body); Splice.DsoRules:DsoRules DsoRules_UpdateFeaturedAppRight (body).
- Run dar-vet on the dependency packages to see what changed there.

## LOW - template-added - `Splice.DSO.Instruction:FeaturedAppConfigUpdateInstruction`

New template. Review it like a new package.

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AllocateUnallocatedUnclaimedActivityRecord``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1890:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_Amulet_ExpireTransferInstructions``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1265 -> Splice.DsoRules:1340:

```diff
          _ <- getAndValidateSvParty this (Some sv)
+         TSU.requireMatchExpected ("transferInsts.expectedDso", transferInsts.expectedDso) dso
-         require ("expectedDso " <> show transferInsts.expectedDso <> " matches the actualDso " <> show dso) (transferInsts.expectedDso == dso)
          res <- exercise amuletRulesCid (transferInsts with expectedDso = dso)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:880:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ClaimExpiredRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1548:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ConfirmSvOnboarding``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1192 -> Splice.DsoRules:1266:

```diff
            else pure ()
+         require "Participant is not already used by another SV" (newParticipantId `notElem` map (.participantId) (Map.values svs))
          now <- getTime
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1774:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_GrantFeaturedAppRight``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.30 -> 0.1.31, Splice.DsoRules:1073 -> Splice.DsoRules:1102:

```diff
          activityWeight : Optional Decimal
+         appKind : Optional FeaturedAppKind
+         opsParties : Optional [Party]
        controller dso
...
            activityWeight
+           appKind
+           opsParties
          return DsoRules_GrantFeaturedAppRightResult with ..
```

