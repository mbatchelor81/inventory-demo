---
trigger: manual
---

# Product Requirements Document (PRD) - Java Repository

## 1. Overview
Briefly describe the purpose of this Java application. Outline what it does and the problem it addresses.

## 2. Goals & Success Criteria
Define the high-level goals of the project and how success will be measured (e.g. correctness, performance, ease of use).

## 3. Technical Scope

### 3.1 Functional Requirements
- List of core features and expected behaviors
- Examples of CLI inputs/outputs (if applicable)

### 3.2 Non-Functional Requirements
- Java version compatibility (e.g. Java 17)
- Build tool (e.g. Maven or Gradle)
- Target performance or memory constraints (if any)

## 4. Code Structure & Organization

> **Folder Structure Guidelines**  
> Use standard Java conventions. Group related logic into meaningful packages.  
> Example layout:

```
/src
  /main
    /java
      /com/example/app
        /service        ← business logic
        /model          ← data classes
        /util           ← helper functions
        /config         ← configuration
  /test
    /java
      /com/example/app  ← unit tests mirror src/main
```

- Keep each class focused and small.
- Avoid circular dependencies between packages.
- Document any unconventional structure decisions.
- The Notion MCP server does not support resources, only use the tools available to you