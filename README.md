# QoNFerenCeR 🎪

Self-hosted **highly customizable** conference product for small (<1000 people) conferences

Contains **android app**, **backend** and **scripts** to setup everything easily

## 🌟 Highly customizable 🛠️

**n8n integration**: Create _own endpoints_ and use them in mobile app

**Outbound events**: _Backend_ tracks every notable change and sends them to specific URLs

**Custom data**: User's table has column called `custom_data`, where you can put any data that is not predefined yet

**Mobile app**: During conference, you may not have time to connect your PC and change anything, so lot of customizability options are available simply from app.

## Key parts

### Roles

### Outbound events

### Keycloak

### SCAN - qr/nfc/bar

### Simple config

### First Admin

### Mobile app

- customscreens
- translations
- meal scanning

Has few pre-defined functionalities and screens, but you can create right _in app in middle of event_ own screen using predefined elements. You have also various options for emoji-per-screen and can select which audience screen will be shown to.

You can also add _own langugage_ and to each languagge _own translations_, which will be applied to all elements

Right from mobile app you can also modify any user's data

## Repository layout

| Path            | What                                |
| --------------- | ----------------------------------- |
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
git config core.hooksPath scripts/gitHooks
```

Points to the `scripts/gitHooks/` folder, enabling a **pre-commit** hook that auto-formats
**Kotlin** files

## Setup checklist

Manual steps before running / deploying:

- Git hooks (once) - see [Getting started](#getting-started).
- **Secrets (`config/QoNFerenCeR.env`)** — tracked with dev values; if you edit real deploy values
  locally, run `git update-index --skip-worktree config/QoNFerenCeR.env` first to prevent accidental commits.
- **`config/`** — single place for all per-event custom files (icon, …). The organizer puts
  everything here; the build/deploy reads only from `config/`.

TODO: Spomenúť nech si importuju data sami cez formular

![QoNFerenCeR logo](QoNFerenCeR_logo.png)
