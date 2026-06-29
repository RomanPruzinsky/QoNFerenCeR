# Keycloak realm import

`realm-export.json` is imported on startup via `start-dev --import-realm` (mounted read-only).
It is **strict JSON** — Keycloak's import rejects `//` / `#` comments, so config notes live here.

## TODO (prod)

- **`sslRequired`**: currently `"none"` (localhost has no TLS). Switch to **`"external"`** for
  prod. Don't use `"all"` (breaks the internal backend → keycloak HTTP hop). Behind a
  TLS-terminating proxy also set `KC_PROXY_HEADERS=xforwarded` and a real `KC_HOSTNAME`.
- **Seeded `admin` user**: the realm ships an `admin` user with a cleartext password for dev
  convenience (reproducible ROPC logins). **Don't ship it for non-dev** — remove the user from
  this file and provision the admin out-of-band (Keycloak Admin API / `kcadm.sh` / env-injected
  bootstrap). Note: marking the password `temporary: true` is *not* a fix here — required actions
  can't be completed through the direct-grant (ROPC) flow this client uses, so it would break login.
