# n8n workflow templates

Pre-built **n8n workflow exports** (JSON) that an organizer imports into their own
n8n instance as starting points — login tracking, meal-event side effects, custom-screen
data endpoints, notifications, etc. They pair with the QoNFerenCeR backend's outbound
events and `/internal/*` endpoints.

> **No custom n8n node.** Workflows call the backend with the generic **HTTP Request**
> node + a **Header Auth** credential carrying the internal service token. (A dedicated
> `n8n-nodes-qonferencer` node was considered and dropped — see
> `ALTERNATIVES/N8n/n8n-plugin-design.md` in the BP materials repo.)

## Files

Drop exported workflows here, one JSON per file:

```
n8nTemplates/
  login-tracking.workflow.json
  meal-approved-discord.workflow.json
  sponsors-screen.workflow.json
  ...
```

Naming: `<feature>.workflow.json` (lowercase, hyphenated).

## How to import (manual)

**n8n UI:** Workflows → **Import from File** → pick the `.json`.

**n8n CLI** (bulk):

```bash
n8n import:workflow --separate --input=./n8nTemplates
# or a single file:
n8n import:workflow --input=./n8nTemplates/login-tracking.workflow.json
```

## Note on auto-import

n8n is **not yet** a service in `deploy/docker-compose.yml`, so import is manual for now.
Once an n8n container is added to the compose, these templates can be auto-loaded on
startup via an init step that runs `n8n import:workflow --separate --input=/templates`
(mount this folder into the container). Tracked here so it stays out of scope until then.
