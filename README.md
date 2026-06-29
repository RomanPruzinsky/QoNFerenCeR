# QoNFerenCeR

Self-hosted conference platform for ~800-people conferences

## Repository layout

| Path            | What                                |
|-----------------|-------------------------------------|
| `android/`      | Android app                         |
| `backend/`      | Spring Boot backend                 |
| `shared/`       | Common code for backend and android |
| `deploy/`       | Docker                              |
| `n8nTemplates/` | n8n workflow templates              |
| `scripts/`      | Dev tooling                         |
| `config/`       | All per-event custom files          |

## Getting started

After cloning, enable the git hooks **once**:

```bash
git config core.hooksPath scripts/git-hooks
```

Points to the `scripts/git-hooks/` folder, enabling a **pre-commit** hook that auto-formats  *
*kotlin** files

## Setup checklist

Manual steps before running / deploying:

- Git hooks (once) - see [Getting started](#getting-started)).
- **n8n encryption key** — dev key is in `deploy/docker-compose.yml` (`N8N_ENCRYPTION_KEY`).
  For prod, change it and move it into a `.env` file.
- **`config/`** — single place for all per-event custom files (icon, …). The organizer puts
  everything here; the build/deploy reads only from `config/`.
