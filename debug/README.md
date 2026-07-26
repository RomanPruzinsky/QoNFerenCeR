# debug/

Development-only artifacts. **Nothing here runs in production** — no code path imports it, the
stack does not depend on it, and it is safe to delete. It exists so the app has believable data and
so the n8n side has something to import while demoing.

| Folder | What |
|---|---|
| [`seed/`](seed/) | Fills a running dev stack with a fake "DevConf 2026": content + ~50 attendees + a few served meals |
| [`n8n/`](n8n/) | Importable n8n workflow templates + the `QN-Token` credential |

## Seed a running stack

```sh
make inf-start          # bring up postgres + keycloak + backend + n8n
./debug/seed/seed.sh    # content via SQL, attendees via the real /admin/slots API
```

`seed.sh` prints demo logins at the end (an ADMIN and a VOLUNTEER with a real password). Everything
it creates goes through the same provisioning path production uses — the 50 attendees are genuine
Keycloak users with anchors, roles and reservations, not rows faked into the database.

Needs `curl`, `jq` and `docker compose` on PATH. Wipe and start over with `make inf-reset`.

## Why here and not in `src/`

A `@Profile("dev")` seeder inside the app would work too, but this keeps demo data as data — SQL you
can read and a shell script you can watch hit the API — instead of hiding it in a bean that only
runs under the right profile. It also makes the n8n templates land where you import them from, next
to the seed that produces the events they react to.
