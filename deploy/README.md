# DEPLOY

Docker Compose stack for QoNFerenCeR: **PostgreSQL** + **Keycloak** + **backend** + **n8n**.

## Databases

Single PostgreSQL instance hosts three **separate** databases

| Database      | Holds                                                                                    | Created by                                      |
| ------------- | ---------------------------------------------------------------------------------------- | ----------------------------------------------- |
| `qonferencer` | App domain data: `User` (anchor), `CustomElementDef`, `CustomScreen`, meal entities, ... | PostgreSQL image from `POSTGRES_DB=qonferencer` |
| `keycloak`    | Identity: realms, users, roles, sessions                                                 | `postgres/initdb/01-create-databases.sh`        |
| `n8n`         | Workflow state: workflows, encrypted credentials, execution history                      | `postgres/initdb/01-create-databases.sh`        |

> ⚠️ [Init script](postgres/initdb/01-create-databases.sh) runs **only on empty data volume**. After changing it, clear volume then restart for it to re-run: `make inf-restart-new`
