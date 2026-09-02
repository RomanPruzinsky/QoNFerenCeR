# scripts/

Developer tooling for the QoNFerenCeR repo.

| File                    | What it does                                                 |
|-------------------------|---------------------------------------------------------------|
| `git-hooks/pre-commit`  | Pre-commit hook: runs `ktlintFormat` on staged Kotlin files. |

## The pre-commit hook

On `git commit`, for each staged `.kt`/`.kts` file it runs `./gradlew ktlintFormat` in the owning
module (`android`/`backend`/`shared`), re-stages the result, and blocks the commit on any violation
it can't auto-fix. Modules without a `gradlew` yet are skipped.
