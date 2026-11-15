#!/bin/sh
# Reproduces the controller-change upgrade on a fresh Canton sandbox.
# Usage: start `dpm sandbox` in another terminal, then run ./run-demo.sh
set -e
JSON=${JSON_API:-http://localhost:6864}
HOST=${LEDGER_HOST:-localhost}
PORT=${LEDGER_PORT:-6865}
OUT=$(mktemp -d)

upload() {
  code=$(curl -s -o "$OUT/upload.txt" -w "%{http_code}" -X POST \
    -H 'Content-Type: application/octet-stream' --data-binary @"$1" "$JSON/v2/packages")
  echo "upload $(basename "$1"): HTTP $code"
  [ "$code" = 200 ] || { cat "$OUT/upload.txt"; exit 1; }
}

dpm build --all

echo "== Step 1: only version 1 on the ledger"
upload asset-v1/.daml/dist/asset-1.0.0.dar
upload test-v1/.daml/dist/asset-test-v1-1.0.0.dar
dpm script --dar test-v1/.daml/dist/asset-test-v1-1.0.0.dar \
  --script-name Before:beforeUpgrade --ledger-host "$HOST" --ledger-port "$PORT" \
  --output-file "$OUT/setup.json"

echo "== Step 2: version 2 uploaded and vetted"
upload asset-v2/.daml/dist/asset-2.0.0.dar
upload test-v2/.daml/dist/asset-test-v2-1.0.0.dar
dpm script --dar test-v2/.daml/dist/asset-test-v2-1.0.0.dar \
  --script-name After:afterUpgrade --ledger-host "$HOST" --ledger-port "$PORT" \
  --input-file "$OUT/setup.json"

echo "== Done: a v1 contract was transferred by its issuer through the v2 choice"
