# dar-vet: splice-wallet 0.1.23 -> 0.1.24

Old package ID `727c0fb9711d59b506fbb3cff2342e55eb583bb88682a946220525c304feca4a`, new package ID `d42261314d98cc04bf50bcc58930af98b9f33aeed11339465925de697d3ba66e`.

**2 findings:** 0 HIGH, 0 MEDIUM, 2 LOW.

## LOW - body-changed - `Splice.Wallet.Install:WalletAppInstall, choice `WalletAppInstall_ExecuteBatch``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- The change is inside the helper function(s) Splice.Wallet.Install:executeAmuletOperationRec, so the expression in the choice looks unchanged. The SDK upgrade check does not warn about this case.

Version 0.1.23 -> 0.1.24, Splice.Wallet.Install:50:

```text
(source text identical: the difference comes from code this function calls)
```

## LOW - body-changed - `Splice.Wallet.MintingDelegation:MintingDelegationProposal, choice `MintingDelegationProposal_Accept``

Choice body changed (same ledger effects, same number of checks). Review the logic, e.g. who receives created contracts.

- Direct change. The SDK upgrade check does not warn about this.

Version 0.1.23 -> 0.1.24, Splice.Wallet.MintingDelegation:22:

```text
(source text identical: the difference comes from code this function calls)
```

