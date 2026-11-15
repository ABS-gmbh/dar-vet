# dar-vet: cov 1.0.0 -> 2.0.0

Old package ID `10f6149150e873ad321860a8dfbf6a79593c7c1c9962870c1989b6a6242f28c4`, new package ID `cd27a260f453dfe6b5937bc21aa1c493dacd6ff49694ce7c8206e26c1729752a`.

**6 findings:** 3 HIGH, 2 MEDIUM, 1 LOW.

## HIGH - effects-changed - `Cov:Cov, choice `Peek``

Ledger effects changed. Added: create self:Cov:Cov. Removed: none.

- The choice is non-consuming, so it can be repeated: each call now creates a contract.
- Direct change. The SDK upgrade check does not warn about this.

Version 1.0.0 -> 2.0.0, Cov:45 -> Cov:37:

```diff
        controller owner
+       do
+         _ <- create this
+         pure ()
-       do pure ()
```

## HIGH - new-choice - `Cov:Cov, choice `Reclaim``

New consuming choice on an existing template. It can be exercised on contracts created under the old version.

- Effects: create self:Cov:Cov.

Version 2.0.0, Cov:44:

```daml
    choice Reclaim : ContractId Cov
      controller issuer
      do create this with owner = issuer
```

## HIGH - controller-changed - `Cov:Cov, choice `ViaHelper``

Who can exercise this choice has changed. It applies to contracts created under the old version.

- The change is inside the helper function(s) Cov:approver, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 1.0.0 -> 2.0.0, Cov:5 -> Cov:4:

```diff
+ approver issuer owner = issuer
- approver issuer owner = owner
```

## MEDIUM - choice-observers-changed - `Cov:Cov, choice `Ping``

Who is informed when this choice is exercised has changed.

- Direct change in the choice. The SDK upgrade check warns about it (upgraded-choice-expression-changed).

Version 1.0.0 -> 2.0.0, Cov:39 -> Cov:32:

```diff
      choice Ping : ()
+       observer [issuer, owner]
-       observer [] : [Party]
        controller owner
```

## MEDIUM - guard-removed - `Cov:Cov, choice `Split``

Fewer checks in the choice body (assertions or aborts): 2 before, 0 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 1.0.0 -> 2.0.0, Cov:31 -> Cov:26:

```diff
        do
-         assertMsg "part must be positive and smaller" (part > 0.0 && part < amount)
          create this with amount = part
```

## LOW - body-changed - `Cov:Cov, choice `Redeem``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Cov:payoutTo, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 1.0.0 -> 2.0.0, Cov:9 -> Cov:7:

```diff
+ payoutTo issuer owner = issuer
- payoutTo issuer owner = owner
```

