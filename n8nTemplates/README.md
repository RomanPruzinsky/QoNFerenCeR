# n8n workflow templates

**N8n workflow templates** that you can import into your n8n

1 **workflow** per **file**, named: `<feature>.workflow.json`

## How to import

- **n8n UI**: Workflows → **Import from File** → pick wanted `.json`
- **n8n CLI all**: `n8n import:workflow --separate --input=./n8nTemplates`
- **n8n CLI single**: `n8n import:workflow --input=./n8nTemplates/<feature>.workflow.json`
