# dar-vet: splice-wallet 0.1.17 -> 0.1.18

Old package ID `176c2924cd7aa12bc81ffd1a8d6cfaf46e70378f653eb5f19f2d6b9599cfd45c`, new package ID `94d88246f69d8a4b69333d1f993e3280deaca19b70511ea7687f01e4328a34a4`.

**2 findings:** 0 HIGH, 0 MEDIUM, 2 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.AmuletRules:computeSynchronizerFees, Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.17 -> 0.1.18, Splice.Wallet.Install:52:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.17 -> 0.1.18, Splice.Wallet.MintingDelegation:20:

```text
(source text identical: the difference comes from code this function calls)
```

