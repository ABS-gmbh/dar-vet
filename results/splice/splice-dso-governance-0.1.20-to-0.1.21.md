# dar-vet: splice-dso-governance 0.1.20 -> 0.1.21

Old package ID `996a3b619d6b65ca7812881978c44c650cac119de78f5317d1f317658943001c`, new package ID `2d306cfe8cdb3daf2d21f84dfecc3e2f26a41504e58fe25cb7fe5cc65683d11f`.

**10 findings:** 2 HIGH, 3 MEDIUM, 5 LOW.

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireDevelopmentFundCoupon``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.Amulet:DevelopmentFundCoupon.DevelopmentFundCoupon_DsoExpire.

Version 0.1.21, Splice.DsoRules:1339:

```daml
    nonconsuming choice DsoRules_ExpireDevelopmentFundCoupon : DsoRules_ExpireDevelopmentFundCouponResult
      -- ^ Expires a DevelopmentFundCoupon and produces an UnclaimedDevelopmentFundCoupon with the same amount.
      with
        developmentFundCouponCid : ContractId DevelopmentFundCoupon
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        result <- exercise developmentFundCouponCid DevelopmentFundCoupon_DsoExpire
        pure $ DsoRules_ExpireDevelopmentFundCouponResult with result
```

## HIGH - new-choice - `Splice.DsoRules:DsoRules, choice `DsoRules_MergeUnclaimedDevelopmentFundCoupons``

New non-consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: exercise splice-amulet:Splice.AmuletRules:AmuletRules.AmuletRules_MergeUnclaimedDevelopmentFundCoupons.

Version 0.1.21, Splice.DsoRules:1327:

```daml
    nonconsuming choice DsoRules_MergeUnclaimedDevelopmentFundCoupons : DsoRules_MergeUnclaimedDevelopmentFundCouponsResult
      -- ^ Batch merge of of development fund coupons.
      with
        amuletRulesCid : ContractId AmuletRules
        choiceArg : AmuletRules_MergeUnclaimedDevelopmentFundCoupons
        sv : Party
      controller sv
      do
        _ <- getAndValidateSvParty this (Some sv)
        result <- exercise amuletRulesCid choiceArg
        return DsoRules_MergeUnclaimedDevelopmentFundCouponsResult with result
```

## MEDIUM - dependency-behaviour-changed - `(package)`

3 expressions are unchanged in this package but call dependency code that changed, including 1 that decide authority (controllers, observers, signatories).

- Changed dependencies: splice-amulet 0.1.14 -> 0.1.15, splice-amulet-name-service 0.1.15 -> 0.1.16, splice-util 0.1.4 -> 0.1.5, splice-wallet-payments 0.1.14 -> 0.1.15.
- Affected: Splice.DsoBootstrap:DsoBootstrap template (ensure clause); Splice.DsoRules:DsoRules DsoRules_AddConfirmedSv (body); Splice.DsoRules:DsoRules DsoRules_ReceiveSvRewardCoupon (body).
- Run dar-vet on the dependency packages to see what changed there.

## MEDIUM - ensure-changed - `Splice.DsoRules:Confirmation`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.20 -> 0.1.21, Splice.DsoRules:337:

```text
(source text identical: the difference comes from code this function calls)
```

## MEDIUM - ensure-changed - `Splice.DsoRules:VoteRequest`

Ensure clause changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 0.1.20 -> 0.1.21, Splice.DsoRules:376:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CastVote``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.DsoRules:718:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_CloseVoteRequest``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.DsoRules:767:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExecuteConfirmedAction``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.DsoRules:653:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_ExpireStaleConfirmation``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.DsoRules:1367:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.DsoRules:DsoRules, choice `DsoRules_SetConfig``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.20 -> 0.1.21, Splice.DsoRules:913:

```text
(source text identical: the difference comes from code this function calls)
```

