# n8n-nodes-qonferencer

Custom n8n nodes for the QoNFerenCeR conference platform.

## Nodes provided

| Node | Type | Purpose |
|------|------|---------|
| **QoNFerenCeR** | action | Health check (stub). Future: scan decisions, user lookup, notifications. |

## Credential

**QoNFerenCeR API** — `baseUrl` + `apiToken`. The token is the internal service token issued by QoNFerenCeR admin (`/admin/n8n/tokens`).

## Build

```bash
npm install
npm run build           # → dist/
```

## Use in n8n (Docker)

Mount `dist/` into the n8n container at `/data/custom/n8n-nodes-qonferencer`
and set `N8N_CUSTOM_EXTENSIONS=/data/custom`. See the root `docker-compose.yml`.

After rebuild, restart n8n: `make restart` (in the repo root).

## Watch mode (development)

```bash
npm run dev
# in another shell:
make restart    # whenever you want n8n to pick up changes
```

## Lint

```bash
npm run lint
npm run lintfix
```

## Publishing (optional)

If you want this in the n8n Community Nodes panel:

```bash
npm publish --access public
```

Note: the package name is reserved for the author; rename if forking.
