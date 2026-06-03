# AI audit

Brief log of AI-assisted changes: what was asked · what was done · date.

## 2026-06-03 — Shared module

**Asked:** Add a `shared` module (DTO / API / constants) consumed by android + backend, with CI matching the existing setup, and without dropping Gradle files into the repo root.

**Done:**

- New self-contained `shared/` Gradle build (Kotlin 2.3.21, JVM 11 target, ktlint, own wrapper) — no source yet.
- Wired as a composite build via `includeBuild("../shared")` in android + backend settings, consumed as `implementation("tr.qonferencer:shared")`. Repo root left untouched.
- CI: added `shared-ci.yml`, a dependabot `/shared` gradle entry, a pre-commit ktlint branch, and `shared/**` to the android + backend CI path filters.
- Verified: `shared` standalone build + backend & android composite compile (Gradle 8.14.5 and 9.2.1).
