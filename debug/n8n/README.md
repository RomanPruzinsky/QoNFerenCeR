# n8n workflow templates

Ready-to-import demos of the backend's outbound events. Each is a Webhook whose path matches an
`EventType`, guarded by the shared `QN-Token` header, wired to a harmless external notification.

| File | Listens on | Does |
|---|---|---|
| `meal-approved-to-discord.json` | `qonferencer_base/MEAL_APPROVED` | posts a line to a Discord webhook |
| `slot-created-to-ntfy.json` | `qonferencer_base/SLOT_CREATED` | pushes a note to an [ntfy.sh](https://ntfy.sh) topic |

Both need one edit after import: the external URL (`REPLACE_ME` Discord webhook / the ntfy topic).
Everything else — path, header auth, field mapping — is already set.

## The shared secret (`QN-Token`)

The backend stamps every outbound request with a `QN-Token` header (`N8N_PATH_TOKEN` in
`config/example.env`). The Webhook nodes reference an n8n **Header Auth** credential named `QN-Token`
that must hold the same value, or every delivery gets rejected.

Two ways to create it:

**A. Click once (manual).** Credentials → New → *Header Auth* → Name `QN-Token`, Header Name
`QN-Token`, Value = the token from `config/example.env`.

**B. Seed it from the file (no clicks).** So the organizer never touches the n8n UI, the installer
imports the credential straight into n8n's database:

```sh
docker compose -f deploy/docker-compose.yml --env-file config/example.env \
	exec n8n n8n import:credentials --input=/dev/stdin < debug/n8n/qn-token-credential.json
```

n8n encrypts it on import with `N8N_ENCRYPTION_KEY` (already in the stack), so the plaintext token
never lands in n8n's DB. Keep `qn-token-credential.json`'s value in sync with `config/example.env`
— in a real install the installer generates both from one source.

> If `import:credentials` can't read `/dev/stdin` on your n8n build, copy the file into the
> container first (`docker compose cp debug/n8n/qn-token-credential.json n8n:/tmp/`) and point
> `--input` at that path.

## Import the workflows

```sh
docker compose ... exec n8n n8n import:workflow --input=/tmp/meal-approved-to-discord.json
```

…or just drag each JSON into the n8n canvas (Workflows → Import from File), set the external URL,
and activate.
