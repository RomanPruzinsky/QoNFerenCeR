#!/usr/bin/env bash
# Install QoNFerenCeR git hooks via symlinks.
#
# Symlinky znamenajú že updates v scripts/git-hooks/ sa prejaví bez opätovného inštal-u.
# Spusti raz po `git clone`. Bezpečné spustiť opakovane — `ln -sf` prepíše.

set -e

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
HOOKS_SRC="$REPO_ROOT/scripts/git-hooks"
HOOKS_DST="$REPO_ROOT/.git/hooks"

if [ ! -d "$HOOKS_DST" ]; then
    echo "❌ $HOOKS_DST neexistuje — si v git repe? Spusti git init najprv."
    exit 1
fi

if [ ! -d "$HOOKS_SRC" ]; then
    echo "❌ $HOOKS_SRC neexistuje — chýba scripts/git-hooks/ folder."
    exit 1
fi

for hook in "$HOOKS_SRC"/*; do
    name=$(basename "$hook")
    chmod +x "$hook"
    ln -sfv "$hook" "$HOOKS_DST/$name"
done

echo
echo "✓ Hooks installed (symlinked):"
ls -la "$HOOKS_DST" | grep -v '\.sample$'
