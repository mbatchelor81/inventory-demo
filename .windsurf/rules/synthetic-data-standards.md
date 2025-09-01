---
trigger: manual
---

# Synthetic Data Generation Standards & Best Practices

## Overview
This document establishes comprehensive standards for generating high-quality synthetic data that is generic, repeatable, and suitable for API testing across different domains and projects.

## Core Principles

### 1. Schema Compliance First
- **Always analyze target entity models** before generating data
- **Respect all validation constraints** (@NotNull, @NotBlank, @Positive, etc.)
- **Match field types exactly** (String, BigDecimal, LocalDateTime, etc.)
- **Honor relationship mappings** (foreign keys, associations)

### 2. Realistic Data Quality
- **Use domain-appropriate data** (realistic names, prices, dates)
- **Maintain logical consistency** (product names match categories)
- **Apply realistic distributions** (80/20 rule for optional fields)
- **Ensure uniqueness** where required (SKUs, email addresses)

### 3. Reproducibility & Control
- **Support seeded generation** for consistent test data
- **Version control data schemas** and generation logic
- **Document data patterns** and business rules
- **Enable deterministic outputs** for CI/CD pipelines

## Data Generation Architecture

### Required Components

#### 1. Data Generator Class
```python
class EntityDataGenerator:
    def __init__(self, seed: Optional[int] = None)
    def generate_entity(self, **kwargs) -> Dict
    def generate_entities(self, count: int) -> List[Dict]
    def get_available_categories(self) -> List[str]
```

#### 2. API Client Class  
```python
class EntityAPIClient:
    def __init__(self, base_url: str, timeout: int = 30)
    def create_entity(self, entity: Dict) -> Dict
    def get_all_entities(self) -> Dict
    def test_connection(self) -> Dict
    def batch_create_entities(self, entities: List[Dict]) -> Dict
    def cleanup_test_data(self, patterns: List[str]) -> Dict
```

#### 3. CLI Interface
```python
# Required arguments
--count: Number of entities to generate
--seed-api: Primary mode for API seeding
--test-endpoints: Validate all CRUD operations
--cleanup: Remove test data safely
```

## Data Quality Standards

### Field Generation Rules

#### String Fields
- **Names**: Use realistic faker providers (person.name, company.name)
- **Descriptions**: Context-aware, category-specific templates
- **Codes/SKUs**: Structured patterns with prefixes (PROD-ABC123)
- **Addresses**: Complete, properly formatted addresses

#### Numeric Fields
- **Prices**: Realistic ranges per category, proper decimal precision
- **Quantities**: Business-logical ranges (1-1000 for inventory)
- **IDs**: Auto-generated, exclude from POST requests
- **Percentages**: Valid ranges (0-100), appropriate precision

#### Date/Time Fields
- **Created dates**: Recent past, logical ordering
- **Updated dates**: After created dates, realistic intervals
- **Future dates**: Business-appropriate lead times
- **Time zones**: Consistent, appropriate for locale

#### Boolean Fields
- **Realistic distributions**: Not 50/50 unless appropriate
- **Business logic**: Active/inactive ratios match reality
- **Dependent relationships**: Consistent with other fields

### Category-Based Generation

#### Define Realistic Categories
```python
categories = {
    'category_name': {
        'prefix': 'CODE',
        'items': ['Item1', 'Item2'],
        'value_ranges': {'price': (min, max)},
        'brands': ['Brand1', 'Brand2'],
        'attributes': {'specific': 'values'}
    }
}
```

#### Category Distribution
- **Balanced representation** across categories
- **Weighted selection** based on real-world frequency
- **Category-specific validation** rules
- **Cross-category relationships** where applicable

## API Integration Standards

### Connection Management
- **Test connectivity** before bulk operations
- **Implement retry logic** with exponential backoff
- **Handle rate limiting** appropriately
- **Use connection pooling** for large datasets

### Error Handling
- **Validate responses** for all HTTP status codes
- **Log detailed error information** with context
- **Continue processing** on individual failures
- **Provide clear error summaries** to users

### Batch Operations
- **Optimal batch sizes** (50-100 entities typically)
- **Progress reporting** for long-running operations
- **Partial failure handling** with detailed reporting
- **Memory-efficient processing** for large datasets

## Testing & Validation Standards

### Endpoint Coverage
- **Test all CRUD operations** (Create, Read, Update, Delete)
- **Validate response schemas** match expectations
- **Test edge cases** (empty responses, large datasets)
- **Verify error handling** for invalid data

### Data Validation
- **Schema compliance** checking after generation
- **Business rule validation** (prices > 0, dates logical)
- **Uniqueness verification** for required unique fields
- **Relationship integrity** for associated entities

### Performance Testing
- **Measure generation speed** (entities per second)
- **Monitor API response times** during seeding
- **Test with various dataset sizes** (10, 100, 1000+)
- **Validate cleanup efficiency** and completeness

## Project Structure Standards

### Directory Organization
```
data_generator/
├── __init__.py                    # Package initialization
├── {entity}_generator.py          # Core generation logic
├── api_client.py                  # HTTP client wrapper
├── generate_{entity}_data.py      # Main CLI script
├── requirements.txt               # Dependencies
├── README.md                      # Usage documentation
└── venv/                          # Virtual environment
```

### File Naming Conventions
- **Generator classes**: `{Entity}DataGenerator`
- **API clients**: `{Entity}APIClient`
- **CLI scripts**: `generate_{entity}_data.py`
- **Output files**: `{entity}_{purpose}.json`

### Documentation Requirements
- **README.md**: Installation, usage examples, troubleshooting
- **Workflow documentation**: Step-by-step procedures
- **Code comments**: Business logic explanations
- **API documentation**: Endpoint descriptions and examples

## Security & Privacy Standards

### Data Sensitivity
- **No real personal data** in synthetic datasets
- **Avoid realistic SSNs, credit cards** or sensitive identifiers
- **Use obviously fake email domains** (@example.com)
- **Generate safe test passwords** if required

### API Security
- **Never hardcode API keys** or credentials
- **Use environment variables** for configuration
- **Implement proper authentication** handling
- **Respect API rate limits** and terms of service

### Data Cleanup
- **Implement reliable cleanup** mechanisms
- **Use identifiable test patterns** (SKU prefixes)
- **Provide cleanup verification** and reporting
- **Never delete production data** accidentally

## Reusability Guidelines

### Generic Design Patterns
- **Abstract common functionality** into base classes
- **Use configuration-driven** category definitions
- **Implement pluggable** validation rules
- **Support multiple output formats** (JSON, CSV, XML)

### Cross-Domain Adaptability
- **Parameterize entity-specific** logic
- **Use template-based** description generation
- **Support custom field** mappings
- **Enable domain-specific** business rules

### Extension Points
- **Custom faker providers** for specialized data
- **Pluggable validation** strategies
- **Configurable API endpoints** and authentication
- **Extensible category** and attribute systems

## Quality Assurance Checklist

### Before Implementation
- [ ] Analyze target entity schema thoroughly
- [ ] Identify all validation constraints
- [ ] Define realistic categories and ranges
- [ ] Plan test data cleanup strategy

### During Development
- [ ] Implement comprehensive error handling
- [ ] Add progress reporting for long operations
- [ ] Create realistic test scenarios
- [ ] Document all configuration options

### Before Release
- [ ] Test with various dataset sizes
- [ ] Validate all API endpoints work correctly
- [ ] Verify cleanup removes only test data
- [ ] Review documentation completeness

### Maintenance
- [ ] Update generators when schemas change
- [ ] Monitor API compatibility over time
- [ ] Refresh realistic data patterns periodically
- [ ] Gather feedback from development teams

## Integration with Development Workflows

### CI/CD Pipeline Integration
- **Automated test data** generation in pipelines
- **Consistent seed values** for reproducible tests
- **Environment-specific** configuration
- **Cleanup automation** after test completion

### Development Environment Setup
- **One-command setup** for new developers
- **Consistent data across** development environments
- **Easy refresh** of stale test data
- **Integration with** local development servers

### Testing Strategies
- **Unit tests** for generation logic
- **Integration tests** with live APIs
- **Performance benchmarks** for large datasets
- **End-to-end validation** of complete workflows

## Monitoring & Metrics

### Generation Metrics
- **Entities generated per second**
- **Success/failure rates** for API operations
- **Data quality scores** (validation pass rates)
- **Category distribution** accuracy

### API Performance
- **Response time percentiles** during seeding
- **Error rate monitoring** across endpoints
- **Rate limit compliance** tracking
- **Connection stability** metrics

### Usage Analytics
- **Most common use cases** and patterns
- **Dataset size preferences** by team
- **Category popularity** and usage frequency
- **Cleanup effectiveness** and frequency

## Conclusion

Following these standards ensures synthetic data generation is:
- **Consistent** across projects and teams
- **Reliable** for automated testing and CI/CD
- **Maintainable** as APIs and schemas evolve
- **Scalable** for various dataset sizes and use cases

Regular review and updates of these standards help maintain their effectiveness as development practices and technologies evolve.
