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

## Getting started

After cloning, enable the git hooks **once**:

```bash
git config core.hooksPath scripts/git-hooks
```

Points to the `scripts/git-hooks/` folder, enabling a **pre-commit** hook that auto-formats  *
*kotlin** files
