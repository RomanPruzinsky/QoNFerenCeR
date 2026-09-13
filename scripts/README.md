# SCRIPTS

Useful tools for QoNFerenCeR setup

All required scripts can be ran from root's `Makefile`

---

## installGitHooks.sh

> run from root: `make first-setup`

Sets path for **pre-commit** git hooks to `scripts/gitHooks/pre-commit`

- (On `git commit`, for each staged `.kt` / `.kts` file it runs `./gradlew ktlintFormat`, re-stages the result, and blocks commit on any violation)

Is for unified formatting accross all _kotlin_ files

Is idempotent, **recommended** to run right after obtaining this repo, but repeated executes of this command won't do anything bad

---

## check.sh

> run from root: `make check`

Verifies that running **backend** and **android app** + **releasing android app** won't _crash_ / _misread_ on missing / mismatched value

Checks that required:

- files exists
- values in QoNFerenCeR exists
- key pairs on specific files are same
- release signing key exists
- TODO_CHANGEME markers are gone

Exits `non-zero` if anything didn't pass -> usable as pre-deploy validation

- Writes list of what went wrong as well as hints how to fix it

---

## releaseAndroid.sh

> run from root: `make release-android`

Creates signed distributable releases (`.apk` and `.aab`) of android app

Firstly verifies using `check.sh`, then (on success) runs gradle tasks and finally copies generated files into `releases/`
