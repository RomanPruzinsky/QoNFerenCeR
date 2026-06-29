# deploy

Docker Compose stack for QoNFerenCeR: PostgreSQL + Keycloak + backend + n8n.

## Databases

A single PostgreSQL instance hosts three **separate** databases — each service owns its own schema,
so they can be backed up, migrated and wiped independently.

| Database      | Holds                                                                                 | Created by                                                                |
|---------------|---------------------------------------------------------------------------------------|---------------------------------------------------------------------------|
| `qonferencer` | App domain data: `User` (anchor), `ProfileFieldDef`, `CustomScreen`, meal entities, … | PostgreSQL image from `POSTGRES_DB=qonferencer` — **not** the init script |
| `keycloak`    | Identity: realms, users, roles, sessions (~90 Keycloak tables)                        | `postgres/initdb/01-create-databases.sh`                                  |
| `n8n`         | Workflow state: workflows, encrypted credentials, execution history                   | `postgres/initdb/01-create-databases.sh`                                  |

### Who creates the tables

- **`qonferencer`** — tables are created and evolved by **Flyway** migrations in the backend (
  `backend/src/main/resources/db/migration/`).
- **`keycloak`** and **`n8n`** — each service creates and manages its own tables on first start.

So the init script only creates the empty `keycloak` and `n8n` databases; `qonferencer` comes from
the `POSTGRES_DB` env var, and all tables come from Flyway / the respective services.

> ⚠️ The init script runs **only on an empty data volume** (first start). After changing it you must
> wipe the volume for it to re-run: `make inf-reset` (deletes all dev data).
