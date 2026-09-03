#!/usr/bin/env bash
# QoNFerenCeR git hooks installer

set -e

. scripts/lib.sh

trap 'banner_error; err "Failed at line $LINENO: $BASH_COMMAND"; echo' ERR

git config core.hooksPath scripts/git-hooks

banner_success
ok "core.hooksPath -> scripts/git-hooks"
echo
