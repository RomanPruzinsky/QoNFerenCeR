#!/usr/bin/env bash
# Builds signed [.apk] and [.aab] android releases

set -e

. scripts/lib.sh

trap 'banner_error; err "Failed at line $LINENO: $BASH_COMMAND"; echo' ERR

KEEP_QUIET=true ./scripts/check.sh

EVENT_ID="$(env_value EVENT_ID)"
OUT_DIR="releases"
mkdir -p "$OUT_DIR"

echo "Building release for EVENT_ID=$EVENT_ID"

cd android

./gradlew --stop
./gradlew :app:assembleRelease :app:bundleRelease

cd ..

cp "android/app/build/outputs/apk/release/app-release.apk" "$OUT_DIR/$EVENT_ID.apk"
cp "android/app/build/outputs/bundle/release/app-release.aab" "$OUT_DIR/$EVENT_ID.aab"

banner_success
ok "$OUT_DIR/$EVENT_ID.apk"
ok "$OUT_DIR/$EVENT_ID.aab"
echo
