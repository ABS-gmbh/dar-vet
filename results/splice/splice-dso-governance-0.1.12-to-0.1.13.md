# dar-vet: splice-dso-governance 0.1.12 -> 0.1.13

Old package ID `0c0b2c4c56a0f30a7d8ce425cb072f3067a4413cbad59fe9bc97c99defef7c2a`, new package ID `8c9c0ee6ecfb815038b1e18ae796e1dd2f0fcf1290f0174f35b0f813fc74f74e`.

**48 findings:** 24 HIGH, 0 MEDIUM, 24 LOW.

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AdvanceOpenMiningRounds``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1090 -> Splice.DsoRules:1110:

```diff
          amuletPriceVoteCids : [ContractId AmuletPriceVote]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          amuletPrice <- fetchMedianAmuletPrice dso (Map.keys svs) amuletPriceVoteCids
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkers``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1316 -> Splice.DsoRules:1361:

```diff
          argument : AmuletRules_ConvertFeaturedAppActivityMarkers
+         sv : Optional Party
+       controller sv
+       do
+         _ <- getAndValidateSvParty this sv
+         result <- exercise amuletRulesCid argument
+         pure DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkersResult with result
-       controller dsoDelegate
-       do result <- exercise amuletRulesCid argument
-          pure DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkersResult with result
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_Amulet_Expire``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1066 -> Splice.DsoRules:1082:

```diff
          choiceArg : Amulet_Expire
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise cid choiceArg
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveOutdatedElectionRequest``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:616 -> Splice.DsoRules:616:

```diff
          requestCid : ContractId ElectionRequest
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          request <- fetchAndArchive (ForDso with dso) requestCid
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveSvOnboardingRequest``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1008 -> Splice.DsoRules:1020:

```diff
          svOnboardingRequestCid: ContractId SvOnboardingRequest
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          -- TODO(#3756) Enable archiving also before the sv is added, after a matching `SvOnboardingConfirmed` contract is created.
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ClaimExpiredRewards``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1204 -> Splice.DsoRules:1233:

```diff
          choiceArg : AmuletRules_ClaimExpiredRewards
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise amuletRulesCid choiceArg
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:748 -> Splice.DsoRules:752:

```diff
          amuletRulesCid : Optional (ContractId AmuletRules)
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          now <- getTime
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CollectEntryRenewalPayment``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1109 -> Splice.DsoRules:1131:

```diff
          choiceArg : AnsEntryContext_CollectEntryRenewalPayment
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          summary <- exercise ansEntryContextCid choiceArg
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:645 -> Splice.DsoRules:647:

```diff
          confirmationCids : [ContractId Confirmation]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          let s = summarizeDso this
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireAnsEntry``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1120 -> Splice.DsoRules:1144:

```diff
          choiceArg : AnsEntry_Expire
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          exercise ansEntryCid choiceArg
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1241 -> Splice.DsoRules:1276:

```diff
          staleConfirmationCid : ContractId Confirmation
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          now <- getTime
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSubscription``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1129 -> Splice.DsoRules:1155:

```diff
          choiceArg : SubscriptionIdleState_ExpireSubscription
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSvOnboardingConfirmed``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1046 -> Splice.DsoRules:1060:

```diff
          cid: ContractId SvOnboardingConfirmed
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          svOnboardingConfirmed <- fetchButArchiveLater (ForDso with dso) cid
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSvOnboardingRequest``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1000 -> Splice.DsoRules:1010:

```diff
          cid: ContractId SvOnboardingRequest
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          exercise cid SvOnboardingRequest_Expire
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireTransferPreapproval``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1308 -> Splice.DsoRules:1351:

```diff
          transferPreapprovalCid : ContractId TransferPreapproval
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          exercise transferPreapprovalCid TransferPreapproval_Expire
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_GarbageCollectAmuletPriceVotes``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:844 -> Splice.DsoRules:850:

```diff
          duplicateVoteCids : [[ContractId AmuletPriceVote]]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          let forDso = ForDso with dso
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_LockedAmulet_ExpireAmulet``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1076 -> Splice.DsoRules:1094:

```diff
          choiceArg : LockedAmulet_ExpireAmulet
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise cid choiceArg
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeMemberTrafficContracts``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1253 -> Splice.DsoRules:1290:

```diff
          trafficCids : [ContractId MemberTraffic]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          require "Number of member traffic contracts is above the configured threshold" $ length trafficCids > config.numMemberTrafficContractsThreshold
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeSvRewardState``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1264 -> Splice.DsoRules:1303:

```diff
          rewardStateCids : [ContractId SvRewardState]
+         sv : Optional Party
+       controller sv
+       do _ <- getAndValidateSvParty this sv
+          require "Number of SvRewardState contracts to merge is >= 2" (length rewardStateCids >= 2)
-       controller dsoDelegate
-       do require "Number of SvRewardState contracts to merge is >= 2" (length rewardStateCids >= 2)
           rewardStates <- forA rewardStateCids $ fetchAndArchive (ForSv this.dso svName)
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeUnclaimedRewards``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1215 -> Splice.DsoRules:1246:

```diff
          unclaimedRewardCids : [ContractId UnclaimedReward]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          require "Number of unclaimed rewards is above the configured threshold" $ length unclaimedRewardCids > config.numUnclaimedRewardsThreshold
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeValidatorLicense``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:963 -> Splice.DsoRules:971:

```diff
          validatorLicenseCids : [ContractId ValidatorLicense]
+         sv : Optional Party
+       controller sv
+       do _ <- getAndValidateSvParty this sv
+          require "Number of validatorLicense contracts to merge is >= 2" (length validatorLicenseCids >= 2)
-       controller dsoDelegate
-       do require "Number of validatorLicense contracts to merge is >= 2" (length validatorLicenseCids >= 2)
           validatorLicenses <- forA validatorLicenseCids $ fetchAndArchive (ForDso this.dso)
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MiningRound_Close``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1226 -> Splice.DsoRules:1259:

```diff
          issuingRoundCid : ContractId IssuingMiningRound
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise amuletRulesCid AmuletRules_MiningRound_Close with ..
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_PruneAmuletConfigSchedule``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1279 -> Splice.DsoRules:1320:

```diff
          amuletRulesCid : ContractId AmuletRules
+         sv : Optional Party
+       controller sv
+       do _ <- getAndValidateSvParty this sv
+          now <- getTime
-       controller dsoDelegate
-       do now <- getTime
           amuletRules <- fetchAndArchive (ForDso dso) amuletRulesCid
```

## HIGH - controller-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_TerminateSubscription``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1140 -> Splice.DsoRules:1167:

```diff
          terminatedSubscriptionCid : ContractId TerminatedSubscription
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          exercise ansEntryContextCid (AnsEntryContext_Terminate dso terminatedSubscriptionCid)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AdvanceOpenMiningRounds``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1090 -> Splice.DsoRules:1110:

```diff
          amuletPriceVoteCids : [ContractId AmuletPriceVote]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          amuletPrice <- fetchMedianAmuletPrice dso (Map.keys svs) amuletPriceVoteCids
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkers``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1316 -> Splice.DsoRules:1361:

```diff
          argument : AmuletRules_ConvertFeaturedAppActivityMarkers
+         sv : Optional Party
+       controller sv
+       do
+         _ <- getAndValidateSvParty this sv
+         result <- exercise amuletRulesCid argument
+         pure DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkersResult with result
-       controller dsoDelegate
-       do result <- exercise amuletRulesCid argument
-          pure DsoRules_AmuletRules_ConvertFeaturedAppActivityMarkersResult with result
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_Amulet_Expire``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1066 -> Splice.DsoRules:1082:

```diff
          choiceArg : Amulet_Expire
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise cid choiceArg
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveOutdatedElectionRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:616 -> Splice.DsoRules:616:

```diff
          requestCid : ContractId ElectionRequest
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          request <- fetchAndArchive (ForDso with dso) requestCid
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveSvOnboardingRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1008 -> Splice.DsoRules:1020:

```diff
          svOnboardingRequestCid: ContractId SvOnboardingRequest
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          -- TODO(#3756) Enable archiving also before the sv is added, after a matching `SvOnboardingConfirmed` contract is created.
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ClaimExpiredRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1204 -> Splice.DsoRules:1233:

```diff
          choiceArg : AmuletRules_ClaimExpiredRewards
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise amuletRulesCid choiceArg
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:748 -> Splice.DsoRules:752:

```diff
          amuletRulesCid : Optional (ContractId AmuletRules)
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          now <- getTime
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CollectEntryRenewalPayment``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1109 -> Splice.DsoRules:1131:

```diff
          choiceArg : AnsEntryContext_CollectEntryRenewalPayment
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          summary <- exercise ansEntryContextCid choiceArg
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ElectDsoDelegate``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:590:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:645 -> Splice.DsoRules:647:

```diff
          confirmationCids : [ContractId Confirmation]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          let s = summarizeDso this
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireAnsEntry``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1120 -> Splice.DsoRules:1144:

```diff
          choiceArg : AnsEntry_Expire
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          exercise ansEntryCid choiceArg
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1241 -> Splice.DsoRules:1276:

```diff
          staleConfirmationCid : ContractId Confirmation
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          now <- getTime
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSvOnboardingConfirmed``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1046 -> Splice.DsoRules:1060:

```diff
          cid: ContractId SvOnboardingConfirmed
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          svOnboardingConfirmed <- fetchButArchiveLater (ForDso with dso) cid
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireSvOnboardingRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1000 -> Splice.DsoRules:1010:

```diff
          cid: ContractId SvOnboardingRequest
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          exercise cid SvOnboardingRequest_Expire
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1308 -> Splice.DsoRules:1351:

```diff
          transferPreapprovalCid : ContractId TransferPreapproval
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          exercise transferPreapprovalCid TransferPreapproval_Expire
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_GarbageCollectAmuletPriceVotes``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:844 -> Splice.DsoRules:850:

```diff
          duplicateVoteCids : [[ContractId AmuletPriceVote]]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          let forDso = ForDso with dso
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_LockedAmulet_ExpireAmulet``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1076 -> Splice.DsoRules:1094:

```diff
          choiceArg : LockedAmulet_ExpireAmulet
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise cid choiceArg
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeMemberTrafficContracts``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1253 -> Splice.DsoRules:1290:

```diff
          trafficCids : [ContractId MemberTraffic]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          require "Number of member traffic contracts is above the configured threshold" $ length trafficCids > config.numMemberTrafficContractsThreshold
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeSvRewardState``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1264 -> Splice.DsoRules:1303:

```diff
          rewardStateCids : [ContractId SvRewardState]
+         sv : Optional Party
+       controller sv
+       do _ <- getAndValidateSvParty this sv
+          require "Number of SvRewardState contracts to merge is >= 2" (length rewardStateCids >= 2)
-       controller dsoDelegate
-       do require "Number of SvRewardState contracts to merge is >= 2" (length rewardStateCids >= 2)
           rewardStates <- forA rewardStateCids $ fetchAndArchive (ForSv this.dso svName)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeUnclaimedRewards``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1215 -> Splice.DsoRules:1246:

```diff
          unclaimedRewardCids : [ContractId UnclaimedReward]
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          require "Number of unclaimed rewards is above the configured threshold" $ length unclaimedRewardCids > config.numUnclaimedRewardsThreshold
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeValidatorLicense``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:963 -> Splice.DsoRules:971:

```diff
          validatorLicenseCids : [ContractId ValidatorLicense]
+         sv : Optional Party
+       controller sv
+       do _ <- getAndValidateSvParty this sv
+          require "Number of validatorLicense contracts to merge is >= 2" (length validatorLicenseCids >= 2)
-       controller dsoDelegate
-       do require "Number of validatorLicense contracts to merge is >= 2" (length validatorLicenseCids >= 2)
           validatorLicenses <- forA validatorLicenseCids $ fetchAndArchive (ForDso this.dso)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MiningRound_Close``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1226 -> Splice.DsoRules:1259:

```diff
          issuingRoundCid : ContractId IssuingMiningRound
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          result <- exercise amuletRulesCid AmuletRules_MiningRound_Close with ..
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_PruneAmuletConfigSchedule``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1279 -> Splice.DsoRules:1320:

```diff
          amuletRulesCid : ContractId AmuletRules
+         sv : Optional Party
+       controller sv
+       do _ <- getAndValidateSvParty this sv
+          now <- getTime
-       controller dsoDelegate
-       do now <- getTime
           amuletRules <- fetchAndArchive (ForDso dso) amuletRulesCid
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_TerminateSubscription``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.12 -> 0.1.13, Splice.DsoRules:1140 -> Splice.DsoRules:1167:

```diff
          terminatedSubscriptionCid : ContractId TerminatedSubscription
+         sv : Optional Party
+       controller sv
-       controller dsoDelegate
        do
+         _ <- getAndValidateSvParty this sv
          exercise ansEntryContextCid (AnsEntryContext_Terminate dso terminatedSubscriptionCid)
```

