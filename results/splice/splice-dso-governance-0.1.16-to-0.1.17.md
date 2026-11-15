# dar-vet: splice-dso-governance 0.1.16 -> 0.1.17

Old package ID `5c96913f6c441129f7df33f402efe737194b90a69784521cb6841c499479fb18`, new package ID `a0ae8e3d9897f163b56e7299af0cae86313f0b1ff10264d1fa73462c67c1f8a9`.

**14 findings:** 0 HIGH, 8 MEDIUM, 6 LOW.

## MEDIUM - ensure-changed - `Splice.DsoBootstrap:DsoBootstrap`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.DSO.DecentralizedSynchronizer:88:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:DsoRules`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.16 -> 0.1.17, Splice.DsoRules:454 -> Splice.DsoRules:452:

```diff
      ensure
+       config.numUnclaimedRewardsThreshold > 0
-       dsoDelegate `Map.member` svs &&
-         unique (map (.name) (Map.values svs)) &&
-         config.numUnclaimedRewardsThreshold > 0
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveOutdatedElectionRequest``

Ledger effects changed. Added: none. Removed: exercise self:Splice.DsoRules:ElectionRequest.Archive, fetch self:Splice.DsoRules:ElectionRequest.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:645 -> Splice.DsoRules:619:

```diff
        do
+         deprecatedChoice "splice-dso-governance" "0.1.17" "DsoRules_ArchiveOutdatedElectionRequest"
-         _ <- getAndValidateSvParty this sv
-         request <- fetchAndArchive (ForDso with dso) requestCid
-         require "Election request is outdated" (request.epoch < epoch)
-         return DsoRules_ArchiveOutdatedElectionRequestResult
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_ArchiveOutdatedElectionRequest``

Fewer checks in the choice body (assertions or aborts): 5 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:645 -> Splice.DsoRules:619:

```diff
        do
+         deprecatedChoice "splice-dso-governance" "0.1.17" "DsoRules_ArchiveOutdatedElectionRequest"
-         _ <- getAndValidateSvParty this sv
-         request <- fetchAndArchive (ForDso with dso) requestCid
-         require "Election request is outdated" (request.epoch < epoch)
-         return DsoRules_ArchiveOutdatedElectionRequestResult
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ElectDsoDelegate``

Ledger effects changed. Added: none. Removed: create self:Splice.DsoRules:DsoRules, exercise self:Splice.DsoRules:ElectionRequest.Archive, fetch self:Splice.DsoRules:ElectionRequest.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:617 -> Splice.DsoRules:608:

```diff
        do
+         deprecatedChoice "splice-dso-governance" "0.1.17" "DsoRules_ElectDsoDelegate"
-         let summary = summarizeDso this
-         -- fetch, check, and archive all requests
-         reqs <- forA requestCids $ \requestCid -> do
-           req <- fetchAndArchive (ForDso with dso) requestCid
-           require "Request is for this epoch" (req.epoch == epoch)
-           require "Requester is an SV" (req.requester `Map.member` svs)
-           pure req
-         let numVotes = length reqs
-             numVoters = numVotes
-             -- as there might be more votes being taken into consideration
-             numVotesForMajority = (summary.requiredNumVotes + 1) / 2
-         require "Requests are from different svs" (unique $ map (.requester) reqs)
-         require "There are enough requests" (numVotes >= summary.requiredNumVotes)
-         let rankings = [ (req.requester, req.ranking) | req <- reqs ]
-             newDsoDelegate = instantRunOffVoting numVotesForMajority (numVoters - 1) rankings
-         let newDsoRules = DsoRules with epoch = epoch + 1, dsoDelegate = newDsoDelegate, ..
-         newDsoRules <- create newDsoRules
-         return DsoRules_ElectDsoDelegateResult with ..
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_ElectDsoDelegate``

Fewer checks in the choice body (assertions or aborts): 7 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:617 -> Splice.DsoRules:608:

```diff
        do
+         deprecatedChoice "splice-dso-governance" "0.1.17" "DsoRules_ElectDsoDelegate"
-         let summary = summarizeDso this
-         -- fetch, check, and archive all requests
-         reqs <- forA requestCids $ \requestCid -> do
-           req <- fetchAndArchive (ForDso with dso) requestCid
-           require "Request is for this epoch" (req.epoch == epoch)
-           require "Requester is an SV" (req.requester `Map.member` svs)
-           pure req
-         let numVotes = length reqs
-             numVoters = numVotes
-             -- as there might be more votes being taken into consideration
-             numVotesForMajority = (summary.requiredNumVotes + 1) / 2
-         require "Requests are from different svs" (unique $ map (.requester) reqs)
-         require "There are enough requests" (numVotes >= summary.requiredNumVotes)
-         let rankings = [ (req.requester, req.ranking) | req <- reqs ]
-             newDsoDelegate = instantRunOffVoting numVotesForMajority (numVoters - 1) rankings
-         let newDsoRules = DsoRules with epoch = epoch + 1, dsoDelegate = newDsoDelegate, ..
-         newDsoRules <- create newDsoRules
-         return DsoRules_ElectDsoDelegateResult with ..
```

## MEDIUM - effects-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_RequestElection``

Ledger effects changed. Added: none. Removed: create self:Splice.DsoRules:ElectionRequest.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:602 -> Splice.DsoRules:598:

```diff
        do
+         deprecatedChoice "splice-dso-governance" "0.1.17" "DsoRules_RequestElection"
-         require "Requester is an SV" (requester `Map.member` svs)
-         require "All svs are ranked exactly once" (sort ranking == sort (Map.keys svs))
-         electionRequestCid <- create ElectionRequest with
-           ..
-         return DsoRules_RequestElectionResult with ..
```

## MEDIUM - guard-removed - `Splice.DsoRules:DsoRules, choice `DsoRules_RequestElection``

Fewer checks in the choice body (assertions or aborts): 3 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:602 -> Splice.DsoRules:598:

```diff
        do
+         deprecatedChoice "splice-dso-governance" "0.1.17" "DsoRules_RequestElection"
-         require "Requester is an SV" (requester `Map.member` svs)
-         require "All svs are ranked exactly once" (sort ranking == sort (Map.keys svs))
-         electionRequestCid <- create ElectionRequest with
-           ..
-         return DsoRules_RequestElectionResult with ..
```

## LOW - body-changed - `Splice.DsoBootstrap:DsoBootstrap, choice `DsoBootstrap_Bootstrap``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoBootstrap:40 -> Splice.DsoBootstrap:42:

```diff
          -- bootstrap rounds
+         result <- exercise amuletRulesCid AmuletRules_Bootstrap_Rounds with
-         exercise amuletRulesCid AmuletRules_Bootstrap_Rounds with
            round0Duration
            amuletPrice
+           initialRound
          -- create the DSO rules with the SV1 as the sole sv
...
                name = sv1Name
+               joinedAsOfRound = fromOptional (Round 0) result.initialRound
-               joinedAsOfRound = Round 0
                svRewardWeight = sv1RewardWeight
...
                newSvParticipantId = sv1ParticipantId
+               joinedAsOfRound = fromOptional (Round 0) result.initialRound
-               joinedAsOfRound = Round 0
                newSvRewardWeight = sv1RewardWeight
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_AllocateUnallocatedUnclaimedActivityRecord``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:1404:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:761:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ConfirmSvOnboarding``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:1047:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:647:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeValidatorLicense``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.16 -> 0.1.17, Splice.DsoRules:984:

```text
(source text identical: the difference comes from code this function calls)
```

