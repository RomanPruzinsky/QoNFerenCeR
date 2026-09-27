# n8n workflow templates

**N8n workflow templates** that you can import into your n8n

1 **workflow** per **file**, named: `<feature>.workflow.json`

## How to import

- **n8n UI**: Workflows → **Import from File** → pick wanted `.json`
- **n8n CLI all**: `n8n import:workflow --separate --input=./n8nTemplates`
- **n8n CLI single**: `n8n import:workflow --input=./n8nTemplates/<feature>.workflow.json`

Each file has a sticky note inside listing setup steps (credentials, spreadsheet/folder ids) - read it after import.

## Files

### [`APP_LAUNCHED_trackingInGoogleSheets.workflow.json`](APP_LAUNCHED_trackingInGoogleSheets.workflow.json)

- listens for backend's `APP_LAUNCHED` outbound event
- appends 1 row per app launch to Google Sheet and sets background color to:
  - **green** if user was logged in
  - **white** if user was anonymous
  - **red** if error occurred

### [`createUserFromGoogleSheets.workflow.json`](createUserFromGoogleSheets.workflow.json)

- Watches Google Sheet for new attendee rows
- calls backend's `preregister` endpoint to create user accounts
- writes generated username/password back into that row
- generates login QR and uploads it to Drive folder `qrs/`, also notes link to same row

### [`MEAL_DENIED_alertsSlack.workflow.json`](MEAL_DENIED_alertsSlack.workflow.json)

- listens for backend's `MEAL_DENIED` outbound event
- posts alert to Slack channel with the deny reason, meal window, and who scanned it
