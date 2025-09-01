---
description: Workflow to assist in synthetic data generation
auto_execution_mode: 3
---

# Generate Synthetic Data Workflow

## Overview
This workflow provides a structured approach to generating realistic synthetic product data for the Inventory Management API using the integrated Python data generator.

## Prerequisites
- Spring Boot application running on `http://localhost:8080`
- Python 3.x installed
- Virtual environment setup completed

## Workflow Steps

### 1. Setup Environment
```bash
# Navigate to data generator directory
cd data_generator

# Create and activate virtual environment (if not already done)
python3 -m venv venv
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt
```

### 2. Generate and Seed Data to API

**Primary Workflow**: Generate synthetic data and populate API database directly
```bash
# Generate and seed products to local API
python generate_product_data.py --count 100 --seed-api

# Generate specific categories only
python generate_product_data.py --count 50 --categories electronics clothing --seed-api

# Generate with reproducible seed
python generate_product_data.py --count 75 --seed 12345 --seed-api

# Seed to custom API URL
python generate_product_data.py --count 50 --seed-api --api-url http://localhost:9090

# Generate and backup to file while seeding
python generate_product_data.py --count 100 --seed-api --output backup.json
```

### 3. Test API Endpoints
```bash
# Validate all Product API endpoints with sample data
python generate_product_data.py --test-endpoints

# Test with verbose logging
python generate_product_data.py --test-endpoints --verbose
```

### 4. Data Categories Available
- **Electronics** (`ELEC-*`): Laptops, phones, tablets, accessories
- **Clothing** (`CLTH-*`): Shirts, jeans, shoes, jackets  
- **Home & Garden** (`HOME-*`): Furniture, decor, tools
- **Books** (`BOOK-*`): Novels, textbooks, guides
- **Sports** (`SPRT-*`): Equipment, apparel, accessories

### 5. Cleanup Test Data
```bash
# Remove all test products (based on SKU patterns)
python generate_product_data.py --cleanup

# Cleanup with verbose output
python generate_product_data.py --cleanup --verbose
```

### 6. Verification Steps
1. **Check API Response**: Verify products appear in GET `/api/products`
2. **Validate Data**: Ensure all required fields are populated
3. **Test Endpoints**: Confirm CRUD operations work with generated data
4. **Review Logs**: Check for any validation or creation errors

## Common Commands Quick Reference

```bash
# Basic generation (10 products to file)
python generate_product_data.py

# Large dataset for performance testing
python generate_product_data.py --count 500 --seed-api

# Category-specific testing
python generate_product_data.py --count 20 --categories electronics --seed-api

# Full workflow: generate, test, cleanup
python generate_product_data.py --count 50 --seed-api
python generate_product_data.py --test-endpoints
python generate_product_data.py --cleanup
```

## Troubleshooting

### API Connection Issues
- Ensure Spring Boot application is running
- Check API URL configuration (`--api-url` parameter)
- Verify CORS settings allow requests from localhost

### Data Validation Errors
- Review Product entity constraints in Java code
- Check generated data matches required schema
- Ensure SKU uniqueness across dataset

### Performance Considerations
- Use smaller batches (50-100 products) for initial testing
- Monitor database performance with large datasets
- Consider cleanup between test runs

## Integration with Development Workflow

### During Development
1. Generate small test datasets (10-20 products)
2. Test new API features with synthetic data
3. Validate business logic with diverse product types

### For Testing
1. Create comprehensive test datasets (100+ products)
2. Test edge cases with specific categories
3. Validate API performance under load

### For Demos
1. Generate visually appealing product mix
2. Use reproducible seeds for consistent demos
3. Include variety across all categories

## File Structure
```
data_generator/
├── __init__.py                 # Package initialization
├── product_generator.py        # Core data generation logic
├── api_client.py              # HTTP client for API interaction
├── generate_product_data.py    # Main CLI script
├── demo_generator.py          # Demo and example usage
├── requirements.txt           # Python dependencies
└── venv/                      # Virtual environment
```

## Next Steps
- Customize product categories for your domain
- Extend generator for other entities (Inventory, Orders)
- Integrate with CI/CD pipeline for automated testing
- Add data export formats (CSV, XML) as needed