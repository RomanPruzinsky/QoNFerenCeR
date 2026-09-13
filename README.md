# QoNFerenCeR 🎪

Self-hosted **highly customizable** conference product for _small_ (<1000 people) _starting_ conferences

Contains **android app**, **backend** and **scripts** to setup everything easily + **templates** and **demos** for inspiration

> Main technologies used: **Kotlin**, **Jetpack Compose**, **SpringBoot**, **PostgreSQL**, **Keycloak**, **Retrofit**, **n8n**, **Docker**

## 🌟 Highly customizable 🛠️

**n8n integration**: Create _own endpoints_ and use them in mobile app

**Outbound events**: _Backend_ tracks notable changes and sends them to specific URLs

**Custom data**: User's table has column called `custom_data`, where you can put any data that is not predefined yet

**Mobile app**: During conference, you may not have time to connect your PC and change anything, so lot of customizability options are available simply from app.

**Scanning types**: Pick which option you want to authenticate users: _BAR CODE_, _QR CODE_, _NFC_ (with also manual writing of UserID as fallback)

## Key parts

### Roles

Supporting 6 privilege roles, which are in **linear** order meaning that highest role has its own permissions + _all_ permissions that lower ones have

These roles are (in `shared/src/main/kotlin/tr/qonferencer/shared/enums/Role.kt`):

- `ANONYM`
- `VISITOR`
- `VOLUNTEER`
- `LEADER`
- `ORGANISER`
- `ADMIN`

### Outbound events (n8n)

Every notable **action** is tracked with its content and sent (using _fire&forget_) to _custom_ **n8n** endpoints

### Keycloak

This service takes care of _user credentials_ and _logging in_, which comes with _validating JWT tokens_ as well as support for _revoking_ or _reissuing_ new credentials

### Meal checking

There are predefined tables for **reserved meals**, along with available **portions** and **windows**. Managing user's portions can be done via app too (as `ADMIN`). How users are validated whether they have reserved portion for specific window is described in following section:

### SCAN options

QoNFerenCeR supports multiple validating options:

- **QR**: Scan QR codes, which can be displayed from user's mobile phone. They are being rotated every 30 seconds
- **BAR**: static alternative for QR codes: contains `UserID` data and can be printed on nametags before conference, so users wouldn't have to have their phones always with them (specifically small kids)
- **NFC**: Also rotated as QR codes, but can be emitted from phone, in case scanner phone don't have camera (or has it broken)

### Simple config

For minimal setup you only need to:

- change `config/QoNFerenCeR.env` variables (which are described in its README)
- upload your conference's `logo.png` (again into `config/`)

### First Admin

Keycloak's realm comes with prepared first user who has all privileges.
Its credentials are:

- username: `First Admin`
- password: `TODO_CHANGEME`

You (programmer / organizer) can manage other users through its credentials (untill you create your own ADMIN account)

Change this password (by using LOGIN endpoint) or delete it (by using DELETE endpoint) - both doable via mobile app by another real ADMIN

### Migrations

Backend uses **Flyway** defined in `V1__init.sql`, which after first run cannot be edited, so `V<version>__<description>.sql` is required for every new version

Newest migrations are applied after backend restarts

## Mobile app

Has few pre-defined functionalities and screens, but you can create right _in app in middle of event_ own screen using _predefined elements_. You also have various options for screen's icon and can select which audience screen will be shown to

You can also add _own language_ and assign to each language _own translations_, which will be applied to all elements

Right from mobile app you can also modify any user's data

## Repository layout

| Folder          | Description                         |
| --------------- | ----------------------------------- |
| `android/`      | Android app                         |
| `backend/`      | Spring Boot backend                 |
| `shared/`       | Common code for backend and android |
| `config/`       | All per-event custom files          |
| `deploy/`       | Docker                              |
| `n8nTemplates/` | N8n workflow templates              |
| `scripts/`      | Dev tooling / helpers               |

---

## Getting started

After cloning, enable git hooks:

```bash
make first-setup
```

Rotate critical secrets: search for all occurences of `TODO_CHANGEME` and change them

Upload `config/logo.png`

Modify `config/QoNFerenCeR.env` (for explanation check `config/README.md`)

Check useful READMEs in:

- `config/`
- `scripts/`
- `deploy/`
- `n8nTemplates/`

![QoNFerenCeR logo](QoNFerenCeR_logo.png)
