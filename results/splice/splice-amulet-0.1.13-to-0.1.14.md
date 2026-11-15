# dar-vet: splice-amulet 0.1.13 -> 0.1.14

Old package ID `6e9fc50fb94e56751b49f09ba2dc84da53a9d7cff08115ebb4f6b7a12d0c990c`, new package ID `3ca1343ab26b453d38c8adb70dca5f1ead8440c42b59b68f070786955cbf9ec1`.

**6 findings:** 0 HIGH, 1 MEDIUM, 5 LOW.

## MEDIUM - ensure-changed - `Splice.AmuletRules:AmuletRules`

Ensure clause changed.

- The change is inside the helper function(s) Splice.AmuletConfig:validAmuletConfig, Splice.AmuletConfig:validTransferConfig, Splice.Fees:positiveFixedFee, Splice.Fees:positiveRatePerRound, Splice.Fees:validSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.AmuletConfig:56:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletConfig:75:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.Fees:62 -> Splice.Fees:62:

```diff
+ positiveFixedFee f = f.fee >= 0.0
- positiveFixedFee f = f.fee > 0.0
```

Version 0.1.13 -> 0.1.14, Splice.Fees:48 -> Splice.Fees:48:

```diff
+ positiveRatePerRound r = r.rate >= 0.0
- positiveRatePerRound r = r.rate > 0.0
```

Version 0.1.13 -> 0.1.14, Splice.Fees:75 -> Splice.Fees:75:

```diff
    all (\(step, rate) -> 0.0 < step && validRate rate) steps &&
-   not (null steps) &&
    strictlyAscending (map fst steps)
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:issueRewards, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1097:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1062 -> Splice.AmuletRules:1064:

```diff
+ issueRewards config csum sender provider beneficiaries summary = do
+     when (config.issueValidatorRewards && usageFees > 0.0) $
+       void $ create ValidatorRewardCoupon with
+         dso = csum.openRound.dso
+         user = sender
+         amount = usageFees
+         round = csum.openRound.round
- issueRewards config csum sender provider beneficiaries summary
-   | usageFees <= 0.0 = pure ()
-   | otherwise = do
-       when (config.issueValidatorRewards) $
-         void $ create ValidatorRewardCoupon with
-           dso = csum.openRound.dso
-           user = sender
-           amount = usageFees
-           round = csum.openRound.round
  
+     if config.issueAppRewards
+       then do
+         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+         validateAppRewardBeneficiaries beneficiaries'
+         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+             totalAmount = usageFees + extraAppReward
+         when (totalAmount > 0.0) $
-       if config.issueAppRewards
-         then do
-           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-           validateAppRewardBeneficiaries beneficiaries'
-           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-               totalAmount = usageFees + extraAppReward
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
...
                round = csum.openRound.round
+       else do
+         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-         else do
-           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1572:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:835 -> Splice.AmuletRules:835:

```diff
  
+     summarizeAndConsumeInput _round s (InputAmulet amuletCid) = do
-     summarizeAndConsumeInput round s (InputAmulet amuletCid) = do
        amulet <- fetchAndArchive forOwner amuletCid
        -- compute balance change
-       let amount = actualAmount round amulet.amount
-           holdingFee = amulet.amount.initialAmount - amount
        return TransferInputsSummary with
...
          totalSvRewardAmount = s.totalSvRewardAmount
+         -- holding fees are only charged on `Amulet_Expire`, which is only
+         -- called when the accrued holding fees surpass the initial amount
+         totalHoldingFees = s.totalHoldingFees
-         totalHoldingFees = s.totalHoldingFees + holdingFee
          amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero + getValueAsOfRound0 amulet.amount
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1010 -> Splice.AmuletRules:1010:

```diff
  summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
+     -- Holding fees are only charged on `Amulet_Expire`
+     require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
      -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
...
        + inp.totalSvRewardAmount
+       - totalOutputAmount - sum outputFees
-       - inp.totalHoldingFees - totalOutputAmount - sum outputFees
      senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateExternalPartySetupProposal``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:issueRewards, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1097:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1062 -> Splice.AmuletRules:1064:

```diff
+ issueRewards config csum sender provider beneficiaries summary = do
+     when (config.issueValidatorRewards && usageFees > 0.0) $
+       void $ create ValidatorRewardCoupon with
+         dso = csum.openRound.dso
+         user = sender
+         amount = usageFees
+         round = csum.openRound.round
- issueRewards config csum sender provider beneficiaries summary
-   | usageFees <= 0.0 = pure ()
-   | otherwise = do
-       when (config.issueValidatorRewards) $
-         void $ create ValidatorRewardCoupon with
-           dso = csum.openRound.dso
-           user = sender
-           amount = usageFees
-           round = csum.openRound.round
  
+     if config.issueAppRewards
+       then do
+         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+         validateAppRewardBeneficiaries beneficiaries'
+         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+             totalAmount = usageFees + extraAppReward
+         when (totalAmount > 0.0) $
-       if config.issueAppRewards
-         then do
-           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-           validateAppRewardBeneficiaries beneficiaries'
-           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-               totalAmount = usageFees + extraAppReward
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
...
                round = csum.openRound.round
+       else do
+         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-         else do
-           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1572:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:835 -> Splice.AmuletRules:835:

```diff
  
+     summarizeAndConsumeInput _round s (InputAmulet amuletCid) = do
-     summarizeAndConsumeInput round s (InputAmulet amuletCid) = do
        amulet <- fetchAndArchive forOwner amuletCid
        -- compute balance change
-       let amount = actualAmount round amulet.amount
-           holdingFee = amulet.amount.initialAmount - amount
        return TransferInputsSummary with
...
          totalSvRewardAmount = s.totalSvRewardAmount
+         -- holding fees are only charged on `Amulet_Expire`, which is only
+         -- called when the accrued holding fees surpass the initial amount
+         totalHoldingFees = s.totalHoldingFees
-         totalHoldingFees = s.totalHoldingFees + holdingFee
          amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero + getValueAsOfRound0 amulet.amount
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1010 -> Splice.AmuletRules:1010:

```diff
  summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
+     -- Holding fees are only charged on `Amulet_Expire`
+     require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
      -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
...
        + inp.totalSvRewardAmount
+       - totalOutputAmount - sum outputFees
-       - inp.totalHoldingFees - totalOutputAmount - sum outputFees
      senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_CreateTransferPreapproval``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:issueRewards, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1097:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1062 -> Splice.AmuletRules:1064:

```diff
+ issueRewards config csum sender provider beneficiaries summary = do
+     when (config.issueValidatorRewards && usageFees > 0.0) $
+       void $ create ValidatorRewardCoupon with
+         dso = csum.openRound.dso
+         user = sender
+         amount = usageFees
+         round = csum.openRound.round
- issueRewards config csum sender provider beneficiaries summary
-   | usageFees <= 0.0 = pure ()
-   | otherwise = do
-       when (config.issueValidatorRewards) $
-         void $ create ValidatorRewardCoupon with
-           dso = csum.openRound.dso
-           user = sender
-           amount = usageFees
-           round = csum.openRound.round
  
+     if config.issueAppRewards
+       then do
+         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+         validateAppRewardBeneficiaries beneficiaries'
+         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+             totalAmount = usageFees + extraAppReward
+         when (totalAmount > 0.0) $
-       if config.issueAppRewards
-         then do
-           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-           validateAppRewardBeneficiaries beneficiaries'
-           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-               totalAmount = usageFees + extraAppReward
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
...
                round = csum.openRound.round
+       else do
+         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-         else do
-           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1572:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:835 -> Splice.AmuletRules:835:

```diff
  
+     summarizeAndConsumeInput _round s (InputAmulet amuletCid) = do
-     summarizeAndConsumeInput round s (InputAmulet amuletCid) = do
        amulet <- fetchAndArchive forOwner amuletCid
        -- compute balance change
-       let amount = actualAmount round amulet.amount
-           holdingFee = amulet.amount.initialAmount - amount
        return TransferInputsSummary with
...
          totalSvRewardAmount = s.totalSvRewardAmount
+         -- holding fees are only charged on `Amulet_Expire`, which is only
+         -- called when the accrued holding fees surpass the initial amount
+         totalHoldingFees = s.totalHoldingFees
-         totalHoldingFees = s.totalHoldingFees + holdingFee
          amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero + getValueAsOfRound0 amulet.amount
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1010 -> Splice.AmuletRules:1010:

```diff
  summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
+     -- Holding fees are only charged on `Amulet_Expire`
+     require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
      -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
...
        + inp.totalSvRewardAmount
+       - totalOutputAmount - sum outputFees
-       - inp.totalHoldingFees - totalOutputAmount - sum outputFees
      senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:issueRewards, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1097:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1062 -> Splice.AmuletRules:1064:

```diff
+ issueRewards config csum sender provider beneficiaries summary = do
+     when (config.issueValidatorRewards && usageFees > 0.0) $
+       void $ create ValidatorRewardCoupon with
+         dso = csum.openRound.dso
+         user = sender
+         amount = usageFees
+         round = csum.openRound.round
- issueRewards config csum sender provider beneficiaries summary
-   | usageFees <= 0.0 = pure ()
-   | otherwise = do
-       when (config.issueValidatorRewards) $
-         void $ create ValidatorRewardCoupon with
-           dso = csum.openRound.dso
-           user = sender
-           amount = usageFees
-           round = csum.openRound.round
  
+     if config.issueAppRewards
+       then do
+         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+         validateAppRewardBeneficiaries beneficiaries'
+         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+             totalAmount = usageFees + extraAppReward
+         when (totalAmount > 0.0) $
-       if config.issueAppRewards
-         then do
-           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-           validateAppRewardBeneficiaries beneficiaries'
-           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-               totalAmount = usageFees + extraAppReward
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
...
                round = csum.openRound.round
+       else do
+         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-         else do
-           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:835 -> Splice.AmuletRules:835:

```diff
  
+     summarizeAndConsumeInput _round s (InputAmulet amuletCid) = do
-     summarizeAndConsumeInput round s (InputAmulet amuletCid) = do
        amulet <- fetchAndArchive forOwner amuletCid
        -- compute balance change
-       let amount = actualAmount round amulet.amount
-           holdingFee = amulet.amount.initialAmount - amount
        return TransferInputsSummary with
...
          totalSvRewardAmount = s.totalSvRewardAmount
+         -- holding fees are only charged on `Amulet_Expire`, which is only
+         -- called when the accrued holding fees surpass the initial amount
+         totalHoldingFees = s.totalHoldingFees
-         totalHoldingFees = s.totalHoldingFees + holdingFee
          amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero + getValueAsOfRound0 amulet.amount
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1010 -> Splice.AmuletRules:1010:

```diff
  summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
+     -- Holding fees are only charged on `Amulet_Expire`
+     require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
      -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
...
        + inp.totalSvRewardAmount
+       - totalOutputAmount - sum outputFees
-       - inp.totalHoldingFees - totalOutputAmount - sum outputFees
      senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
```

## LOW - body-changed - `Splice.AmuletRules:TransferPreapproval, choice `TransferPreapproval_Renew``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:issueRewards, Splice.AmuletRules:splitAndBurn, Splice.AmuletRules:summarizeAndConsumeInputs, Splice.AmuletRules:summarizeTransfer, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1097:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:720:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1062 -> Splice.AmuletRules:1064:

```diff
+ issueRewards config csum sender provider beneficiaries summary = do
+     when (config.issueValidatorRewards && usageFees > 0.0) $
+       void $ create ValidatorRewardCoupon with
+         dso = csum.openRound.dso
+         user = sender
+         amount = usageFees
+         round = csum.openRound.round
- issueRewards config csum sender provider beneficiaries summary
-   | usageFees <= 0.0 = pure ()
-   | otherwise = do
-       when (config.issueValidatorRewards) $
-         void $ create ValidatorRewardCoupon with
-           dso = csum.openRound.dso
-           user = sender
-           amount = usageFees
-           round = csum.openRound.round
  
+     if config.issueAppRewards
+       then do
+         let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
+         validateAppRewardBeneficiaries beneficiaries'
+         let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
+             totalAmount = usageFees + extraAppReward
+         when (totalAmount > 0.0) $
-       if config.issueAppRewards
-         then do
-           let beneficiaries' = fromOptional [AppRewardBeneficiary provider 1.0] beneficiaries
-           validateAppRewardBeneficiaries beneficiaries'
-           let groupedBeneficiaries = Map.fromListWithR (+) (map (\b -> (b.beneficiary, b.weight)) beneficiaries')
-               totalAmount = usageFees + extraAppReward
            forA_ (Map.toList groupedBeneficiaries) $ \(beneficiary, weight) ->
...
                round = csum.openRound.round
+       else do
+         require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
-         else do
-           require "beneficiaries are unset if issueAppRewards is false" (optional True null beneficiaries)
    where
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1572:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:835 -> Splice.AmuletRules:835:

```diff
  
+     summarizeAndConsumeInput _round s (InputAmulet amuletCid) = do
-     summarizeAndConsumeInput round s (InputAmulet amuletCid) = do
        amulet <- fetchAndArchive forOwner amuletCid
        -- compute balance change
-       let amount = actualAmount round amulet.amount
-           holdingFee = amulet.amount.initialAmount - amount
        return TransferInputsSummary with
...
          totalSvRewardAmount = s.totalSvRewardAmount
+         -- holding fees are only charged on `Amulet_Expire`, which is only
+         -- called when the accrued holding fees surpass the initial amount
+         totalHoldingFees = s.totalHoldingFees
-         totalHoldingFees = s.totalHoldingFees + holdingFee
          amountArchivedAsOfRoundZero = s.amountArchivedAsOfRoundZero + getValueAsOfRound0 amulet.amount
```

Version 0.1.13 -> 0.1.14, Splice.AmuletRules:1010 -> Splice.AmuletRules:1010:

```diff
  summarizeTransfer sender openRound transferConfigAmulet inp preprocessedOutputs = do
+     -- Holding fees are only charged on `Amulet_Expire`
+     require "totalHoldingFees == 0" (inp.totalHoldingFees == 0.0)
      -- These traces are shown in the tx view in Daml studio, and greatly aid debugging fee calculations
...
        + inp.totalSvRewardAmount
+       - totalOutputAmount - sum outputFees
-       - inp.totalHoldingFees - totalOutputAmount - sum outputFees
      senderChangeFee = min transferConfigAmulet.createFee.fee leftOverAmount
```

