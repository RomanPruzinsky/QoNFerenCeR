# scripts/

Developer tooling for the QoNFerenCeR repo.

| File                                          | What it does                                                 |
|-----------------------------------------------|--------------------------------------------------------------|
| `git-hooks/pre-commit`                        | Pre-commit hook: runs `ktlintFormat` on staged Kotlin files. |
| `templates/android-build.gradle.kts.template` | ktlint integration snippet for `android/build.gradle.kts`.   |
| `templates/backend-build.gradle.kts.template` | ktlint integration snippet for `backend/build.gradle.kts`.   |

## The pre-commit hook

On `git commit`, for each staged `.kt`/`.kts` file it runs `./gradlew ktlintFormat` in the owning
module (`android`/`backend`/`shared`), re-stages the result, and blocks the commit on any violation
it can't auto-fix. Modules without a `gradlew` yet are skipped.

## ktlint templates

`templates/*.gradle.kts.template` are copy-paste snippets for wiring ktlint into each Gradle project
when you first create it (steps are in the file comments). Delete or keep them once integrated.
