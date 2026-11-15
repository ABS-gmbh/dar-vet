# dar-vet: splice-validator-lifecycle 0.1.3 -> 0.1.4

Old package ID `33cbc8ef9f2937b581f33e0a19cdafc632084bd6e2f34d73174774fc519f8930`, new package ID `9333354aaaf7378ff228a4c8e51fadec8f4b82308f3343b1c4b68ae13dafa849`.

**2 findings:** 0 HIGH, 1 MEDIUM, 1 LOW.

## MEDIUM - guard-removed - `Splice.ValidatorOnboarding:ValidatorOnboarding, choice `ValidatorOnboarding_Expire``

Fewer checks in the choice body (assertions or aborts): 2 before, 1 now.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.3 -> 0.1.4, Splice.ValidatorOnboarding:41 -> Splice.ValidatorOnboarding:44:

```diff
        do
+         assertDeadlineExceeded "ValidatorOnboarding.expiresAt" expiresAt
-         now <- getTime
-         require "ValidatorOnboarding has expired" (now >= expiresAt)
          pure ValidatorOnboarding_ExpireResult
```

## LOW - body-changed - `Splice.ValidatorOnboarding:ValidatorOnboarding, choice `ValidatorOnboarding_Match``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.3 -> 0.1.4, Splice.ValidatorOnboarding:28 -> Splice.ValidatorOnboarding:30:

```diff
        do
+         assertWithinDeadline "ValidatorOnboarding.expiresAt" expiresAt
          require "provided secret matches the stored secret" (providedSecret == candidateSecret)
```

