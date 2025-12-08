---
trigger: manual
---

# Atlassian Jira Integration Rules

## Default Jira Board Configuration

When working with Jira tickets through the Atlassian MCP server, **ALWAYS** use the following configuration:

### Project Details
- **Cloud ID**: `fa5d8dc5-d6ef-4a34-8893-71f636ad67a1`
- **Site URL**: `https://mason-cognition-demo.atlassian.net`
- **Project Key**: `ID`
- **Project Name**: `Inventory Demo`

### Rules for Jira Operations

1. **Default Project**: Unless explicitly specified otherwise, all Jira operations should target the **Inventory Demo** project (key: `ID`)

2. **Ticket References**: When asked about "tickets", "issues", or "Jira items" without a specific project mentioned, assume the user is referring to the Inventory Demo board

3. **Search Scope**: When searching for issues, filter by project key `ID` to ensure results are limited to the Inventory Demo project

4. **Ticket Creation**: When creating new tickets, default to the Inventory Demo project unless the user explicitly specifies a different project

5. **Issue Key Format**: Inventory Demo tickets follow the format `ID-{number}` (e.g., ID-1, ID-8, ID-123)

## Example Usage

### ✅ Correct Approach
```
User: "Show me ticket 1"
Action: Fetch ID-1 from Inventory Demo project
```

```
User: "Create a new bug ticket"
Action: Create issue in project key "ID" (Inventory Demo)
```

```
User: "List all open tickets"
Action: Search issues with JQL: project = ID AND status = "To Do"
```

### ❌ Avoid
- Asking which project to use when context is clear
- Searching across all projects when user asks about "tickets"
- Creating tickets in wrong projects

## Available Scopes
The Atlassian MCP server has access to:
- **Jira**: read:jira-work, write:jira-work
- **Confluence**: read/write permissions for pages, comments, spaces

## Quick Reference Commands

### Fetch Ticket
```
Issue Key: ID-{number}
Cloud ID: fa5d8dc5-d6ef-4a34-8893-71f636ad67a1
```

### Search Issues
```
JQL: project = ID AND [additional filters]
Cloud ID: fa5d8dc5-d6ef-4a34-8893-71f636ad67a1
```

### Create Issue
```
Project Key: ID
Cloud ID: fa5d8dc5-d6ef-4a34-8893-71f636ad67a1
```

---

**Note**: This configuration is specific to the inventory-demo repository and should be followed consistently for all Jira-related operations unless explicitly overridden by the user.
