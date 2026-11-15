# dar-vet: splice-amulet 0.1.1 -> 0.1.2

Old package ID `67bc95402ad7b08fcdff0ed478308d39c70baca2238c35c5d425a435a8a9e7f7`, new package ID `1446ffdf23326cef2de97923df96618eb615792bea36cf1431f03639448f1645`.

**3 findings:** 0 HIGH, 0 MEDIUM, 3 LOW.

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_BuyMemberTraffic``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, Splice.Fees:chargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:915:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:605:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:806:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.Fees:71 -> Splice.Fees:71:

```diff
  chargeSteppedRate SteppedRate{initialRate, steps} amount =
+   goChargeSteppedRate (amount, initialRate, 0.0) stepDifferences
+   where
+     -- turn [(100.0, 0.001), (1000.0, 0.0001), (1000000, 0.00001)]
+     -- into [(100.0, 0.001), (900.0, 0.0001), (998900, 0.00001)]
+     -- i.e., the step is the remainder after applying all the
+     -- previous steps and not the absolute value.
+     stepDifferences = reverse $ snd $
+       foldl (\(total, xs) (step, rate) -> (total + step, (step - total, rate) :: xs)) (0.0, []) steps
-   goChargeSteppedRate (amount, initialRate, 0.0) steps
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_ComputeFees``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeFees, Splice.AmuletRules:computeOutputFee, Splice.Fees:chargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:557:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.Fees:71 -> Splice.Fees:71:

```diff
  chargeSteppedRate SteppedRate{initialRate, steps} amount =
+   goChargeSteppedRate (amount, initialRate, 0.0) stepDifferences
+   where
+     -- turn [(100.0, 0.001), (1000.0, 0.0001), (1000000, 0.00001)]
+     -- into [(100.0, 0.001), (900.0, 0.0001), (998900, 0.00001)]
+     -- i.e., the step is the remainder after applying all the
+     -- previous steps and not the absolute value.
+     stepDifferences = reverse $ snd $
+       foldl (\(total, xs) (step, rate) -> (total + step, (step - total, rate) :: xs)) (0.0, []) steps
-   goChargeSteppedRate (amount, initialRate, 0.0) steps
```

## LOW - body-changed - `Splice.AmuletRules:AmuletRules, choice `AmuletRules_Transfer``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeOutputFee, Splice.AmuletRules:createTransferOutputs, Splice.AmuletRules:executeTransfer, Splice.AmuletRules:preprocessOutputs, Splice.Fees:chargeSteppedRate, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:563:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:915:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:605:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.AmuletRules:806:

```text
(source text identical: the difference comes from code this function calls)
```

Version 0.1.1 -> 0.1.2, Splice.Fees:71 -> Splice.Fees:71:

```diff
  chargeSteppedRate SteppedRate{initialRate, steps} amount =
+   goChargeSteppedRate (amount, initialRate, 0.0) stepDifferences
+   where
+     -- turn [(100.0, 0.001), (1000.0, 0.0001), (1000000, 0.00001)]
+     -- into [(100.0, 0.001), (900.0, 0.0001), (998900, 0.00001)]
+     -- i.e., the step is the remainder after applying all the
+     -- previous steps and not the absolute value.
+     stepDifferences = reverse $ snd $
+       foldl (\(total, xs) (step, rate) -> (total + step, (step - total, rate) :: xs)) (0.0, []) steps
-   goChargeSteppedRate (amount, initialRate, 0.0) steps
```

