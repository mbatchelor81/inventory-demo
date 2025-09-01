# Windsurf Rules Writing Guide

A comprehensive guide for writing effective rules in Windsurf to guide Cascade's behavior and maintain consistent development practices.

## What Are Windsurf Rules?

Windsurf rules are declarative instructions that specify how Cascade should behave when working on your project. They serve as persistent guidelines that influence:

- **Code Style Guidelines** - Formatting, naming conventions, and structural patterns
- **Organizational Best Practices** - Project structure, documentation standards, and workflow processes
- **General Usage of Cascade** - Behavioral preferences and interaction patterns

## Rule Categories

### 1. Code Style Guidelines
Rules that enforce consistent coding practices across your project.

### 2. Organizational Best Practices
Rules that maintain project structure and development workflows.

### 3. General Usage of Cascade
Rules that customize how Cascade interacts with you and your project.

## Markdown Formatting for Rules

### Basic Rule Structure

Use clear, imperative language with proper markdown formatting:

```markdown
## Rule Category

**Rule Name**: Brief description of the rule

- Use bullet points for specific requirements
- Keep each point actionable and specific
- Use **bold** for emphasis on key terms
- Use `code formatting` for technical terms
```

### Essential Markdown Elements

#### Headers for Organization
```markdown
# Main Title
## Category Headers
### Sub-categories
```

#### Emphasis and Formatting
- **Bold text** for critical requirements: `**MUST**`, `**NEVER**`, `**ALWAYS**`
- *Italic text* for emphasis: `*preferred*`, `*recommended*`
- `Inline code` for technical terms: `className`, `file.js`, `@Override`

#### Code Blocks
Use fenced code blocks with language specification:

````markdown
```java
// Example of preferred code style
public class BookService {
    private final BookRepository repository;
}
```
````

#### Lists for Requirements
```markdown
- Use unordered lists for requirements
- Each item should be a complete, actionable statement
- Nest sub-requirements with proper indentation
  - Sub-requirement example
  - Another sub-requirement
```

#### Blockquotes for Important Notes
```markdown
> **Important**: This rule takes precedence over default behavior
```

## Best Practices for Writing Rules

### 1. Be Specific and Actionable

❌ **Poor Example:**
```markdown
Write good code
```

✅ **Good Example:**
```markdown
**Code Quality Standards**:
- Use meaningful variable names that describe their purpose
- Limit method length to 20 lines maximum
- Include JavaDoc comments for all public methods
```

### 2. Use Clear Priority Language

Establish hierarchy with specific language:

```markdown
**MUST** - Non-negotiable requirements
**SHOULD** - Strong recommendations
**MAY** - Optional preferences
**NEVER** - Absolute prohibitions
```

### 3. Provide Context and Examples

```markdown
**Naming Conventions**:
- Use camelCase for variables and methods: `bookTitle`, `calculateLateFee()`
- Use PascalCase for classes: `BookService`, `PatronRepository`
- Use UPPER_SNAKE_CASE for constants: `MAX_LOAN_DURATION`

Example:
```java
public class BookLendingService {
    private static final int MAX_LOAN_DURATION = 14;
    
    public Result<Loan> createLoan(BookInstance bookInstance) {
        // Implementation
    }
}
```

### 4. Group Related Rules

```markdown
## Database Operations

**Transaction Management**:
- Always use `@Transactional` for service methods that modify data
- Use read-only transactions for query operations: `@Transactional(readOnly = true)`

**Entity Relationships**:
- Use lazy loading by default: `@OneToMany(fetch = FetchType.LAZY)`
- Explicitly define cascade operations: `cascade = CascadeType.PERSIST`
```

### 5. Include Rationale When Helpful

```markdown
**Exception Handling**:
- Use custom exceptions for domain-specific errors
- Always log exceptions with appropriate context
- Return `Result<T>` types instead of throwing exceptions for expected failures

*Rationale*: This approach provides better error handling and makes the API more predictable for consumers.
```

## Rule Template

Use this template as a starting point:

```markdown
# Project Rules for [Project Name]

## Code Style Guidelines

**[Rule Category]**:
- Specific requirement 1
- Specific requirement 2
- Use `code formatting` for technical terms

Example:
```[language]
// Code example demonstrating the rule
```

## Organizational Best Practices

**[Practice Name]**:
- Requirement with clear action
- Another requirement with measurable outcome

> **Note**: Additional context or important information

## General Usage of Cascade

**[Behavior Preference]**:
- How Cascade should behave in specific situations
- Preferences for interaction patterns
- Guidelines for decision-making
```

## Common Rule Examples

### Code Style Example
```markdown
**Java Coding Standards**:
- Use Spring Boot annotations consistently: `@Service`, `@Repository`, `@Controller`
- Implement proper validation with `@Valid` and custom validators
- Use Lombok annotations to reduce boilerplate: `@Data`, `@Builder`, `@RequiredArgsConstructor`
- Follow the repository pattern with interface-based design
```

### Organizational Example
```markdown
**Project Structure**:
- Organize code by domain modules: `catalogue`, `lending`, `commons`
- Place domain objects in `domain` packages
- Keep infrastructure concerns in separate packages
- **NEVER** mix UI logic with business logic
```

### Cascade Usage Example
```markdown
**Development Workflow**:
- Always run tests before making code changes
- Update documentation when adding new features
- **NEVER** commit code without user approval
- Provide explanations for architectural decisions
```

## Testing Your Rules

After writing rules:

1. **Review for Clarity** - Can someone else understand and follow them?
2. **Check Completeness** - Do they cover the important aspects of your project?
3. **Validate Examples** - Are code examples correct and helpful?
4. **Test with Cascade** - Do the rules produce the desired behavior?

## Maintaining Rules

- **Regular Updates** - Review and update rules as your project evolves
- **Team Alignment** - Ensure all team members understand and agree with the rules
- **Version Control** - Track changes to rules like any other project artifact
- **Documentation** - Keep rules documentation current and accessible

---

*Remember: Well-written rules create consistency, reduce decision fatigue, and help maintain code quality across your project.*
