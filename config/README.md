# CONFIG

## logo.png

Logo in `.png` format is required for android build

It will be used as

- app's icon on phone's screen
- logo at navigation bar and splash screen

No fixed scale or max size required

---

## QoNFerenCeR.env

Later defined values are _required_:

### API_VERSION

Backend API version prefix, currently `1`

> upgrade on each API change

### EVENT_ID

Identifier of conference event

> must match `[a-zA-Z][a-zA-Z0-9_]*`

---

### BACKEND_BASE_URL

**Public** base URL Android app talks to for **backend** API

### KEYCLOAK_BASE_URL

**Public** base URL Android app uses to reach Keycloak

### KEYCLOAK_HOSTNAME

Base URL Keycloak uses for itself

Backend validates JWT `iss` claim against it

> Don't have to be public, only **backend** needs to access it

---

### KC_CONSOLEADMIN_USERNAME

Keycloak admin console login username

### KC_CONSOLEADMIN_PASSWORD

Keycloak admin console login password

### KC_BEADMIN_CLIENT_SECRET

Client secret backend uses to call Keycloak admin API

> must match "secret" of "qonferencer-backend-admin" client in `realm-export`

---

### POSTGRES_USER

Postgres **superuser** name, used for all databases created by initdb script

### POSTGRES_PASSWORD

Postgres **superuser** password

---

### N8N_ENABLED

Whether **n8n** is used

### N8N_PATH_PREFIX

Path prefix **backend** uses when calling **n8n** endpoints (for Outbound events)

### N8N_TIMEOUT_MS

Timeout in ms for **backend** to **n8n** calls

### BE_N8N_COMMS\_\_AUTH_TOKEN

Shared auth token **backend** and **n8n** use to authenticate calls between them

### N8N_ENCRYPTION_KEY

Encryption key **n8n** uses for its own stored credentials/data

---

### RELEASE_KEYSTORE_PATH

Path to Android release signing keystore, relative to repo root

### RELEASE_KEY_ALIAS

Alias of signing key inside release keystore

### RELEASE_KEYSTORE_PASSWORD

Password of release keystore

### RELEASE_KEY_PASSWORD

Password of signing key inside release keystore

---

## Must change

These entries are placeholders and must be set before real use:

- `EVENT_ID`
- `BE_N8N_COMMS__AUTH_TOKEN`
- `N8N_ENCRYPTION_KEY`

- `RELEASE_KEYSTORE_PATH`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEYSTORE_PASSWORD`
- `RELEASE_KEY_PASSWORD`

- `BACKEND_BASE_URL`
- `KEYCLOAK_BASE_URL`
