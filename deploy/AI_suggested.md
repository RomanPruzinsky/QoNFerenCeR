# AI suggestions — deploy / Docker

Concerns and suggestions for the backend Dockerfile that will live in `deploy/`, now that
the backend depends on the `shared` module through a Gradle composite build.

## Why this needs attention

`backend/settings.gradle.kts` now has `includeBuild("../shared")` and the backend depends
on `tr.qonferencer:shared`. So **building the backend requires the `shared/` sources to be
reachable at `../shared` relative to `backend/`.** Any Docker build that compiles the
backend from source must include *both* directories in its build context — `backend/`
alone is no longer enough.

The current `.github/workflows/backend-docker-push.yml` uses `context: ./backend`, which
(a) won't see a Dockerfile in `deploy/`, and (b) won't see `shared/`. Both need fixing.

## Concerns

- **Build-context scope.** A source-building image needs `backend/` *and* `shared/`, with
  their relative layout preserved so `includeBuild("../shared")` still resolves.
- **Dockerfile vs context location.** Dockerfile lives in `deploy/`, but the context must
  be the repo root — so the workflow needs `context: .` plus `file: deploy/Dockerfile`.
- **Image bloat / slow builds.** Copying Gradle and both source trees and compiling inside
  the image is heavy without dependency-layer caching.
- **Missing `.dockerignore`.** Without one, `build/`, `.gradle/`, and IDE files land in the
  build context — slower builds and busted cache.

## Suggested approach A — build the jar in CI, COPY it (recommended)

The composite build already resolves `shared` into the backend (verified by compiling the
backend against it). Let CI produce the jar and keep the Dockerfile trivial — no Gradle or
`shared/` source in the image build.

`deploy/Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY backend/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

`backend-docker-push.yml` — add a build step before the image build:

```yaml
- name: Setup JDK 21
  uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: "21"
- name: Build bootJar
  run: ./gradlew bootJar
  working-directory: backend
- name: Build and push
  uses: docker/build-push-action@v6
  with:
    context: .
    file: deploy/Dockerfile
    push: true
    # tags / labels unchanged
```

Pros: small, fast, reproducible; composite complexity stays in Gradle, where it is already
verified. Con: the image build depends on a prior CI step (no standalone `docker build`
from a clean checkout).

## Suggested approach B — multi-stage build from source (self-contained)

If you want `docker build` to work from a clean checkout without a separate jar step:

`deploy/Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY backend/ backend/
COPY shared/ shared/
WORKDIR /workspace/backend
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/backend/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

The `COPY backend/ shared/` layout keeps them siblings under `/workspace`, so
`includeBuild("../shared")` resolves exactly as it does on disk. Workflow uses the same
`context: .` plus `file: deploy/Dockerfile`, with no separate jar step.

Pros: self-contained, one source of truth. Cons: bigger build context, slower without
BuildKit cache mounts; the build JDK layer is discarded by the multi-stage split.

## Either way

- Base the runtime image on **JRE 21** — matches the backend toolchain.
- Add a `.dockerignore` (repo root) excluding `**/build`, `**/.gradle`, `**/.idea`,
  `**/*.iml`, and `.git`.
- Change the workflow from `context: ./backend` to `context: .` plus
  `file: deploy/Dockerfile`, regardless of approach.

These are suggestions only — no Dockerfile created and the workflow is untouched.
