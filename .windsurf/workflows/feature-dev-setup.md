---
description: Setup the repository for feature development
---

# Simple Feature Development Workflow

## Quick Summary
**What it does**: Prepares the repository for new feature development by creating a feature branch, verifying the build, and understanding the codebase structure.

**Time**: ~5-10 minutes
**Prerequisites**: Java 11+, Maven 3.6+, Git

## Workflow Steps

### Step 1: Create Feature Branch
```bash
# Sync with latest
git checkout master
git pull origin master

# Create feature branch (use descriptive name)
git checkout -b feature/your-feature-name

# Verify clean state
git status
```

### Step 2: Verify Build Baseline
```bash
# Navigate to backend directory
cd backend

# Clean build to ensure starting point is solid
mvn clean compile

# Run all tests to verify baseline
mvn test

# Start the application to verify it runs
mvn spring-boot:run &
sleep 10
curl http://localhost:8080/api/ping
kill %1
```

**Expected Output**: 
- Clean compilation of Spring Boot application
- All tests passing (including validation tests)
- Application starts successfully on port 8080
- Ping endpoint returns successful response

### Step 3: Explore Inventory Service Structure
```bash
# Examine the inventory service domain structure
find backend/src/main/java/com/example/inventory_service_demo -type d | sort

# Look at core domain models
ls backend/src/main/java/com/example/inventory_service_demo/model/
ls backend/src/main/java/com/example/inventory_service_demo/controller/
ls backend/src/main/java/com/example/inventory_service_demo/service/

# Check existing test patterns
find backend/src/test/java -name "*Test.java" | head -5
```

**Key Packages**:
- `model/` - Core entities: Product, Inventory, PurchaseOrder, OrderItem
- `controller/` - REST API endpoints for products, inventory, orders
- `service/` - Business logic layer with validation and processing
- `repository/` - JPA data access layer with custom queries
- `config/` - Application configuration and data initialization

### Step 4: Understand Inventory Service Features
```bash
# Check the database schema and sample data
cd backend
mvn spring-boot:run &
sleep 15

# Access H2 console to see database structure
echo "H2 Console available at: http://localhost:8080/h2-console"
echo "JDBC URL: jdbc:h2:mem:inventorydb"
echo "Username: sa, Password: password"

# Test key API endpoints
curl http://localhost:8080/api/products
curl http://localhost:8080/api/inventory
curl http://localhost:8080/api/purchase-orders

# Stop the application
kill %1
```

**Core Features**:
- **Product Management**: CRUD operations with SKU validation
- **Inventory Tracking**: Real-time quantity management
- **Purchase Orders**: Order lifecycle with status tracking
- **Data Validation**: Jakarta validation with custom error messages
- **Sample Data**: Auto-populated test data on startup

### Step 5: Ready for Development
```bash
# Confirm feature branch is active
git branch --show-current

# Confirm clean working directory
git status

# Ready to start feature development
echo "✅ Repository prepared for feature development"
echo "📁 Working on branch: $(git branch --show-current)"
echo "🏗️ Domain packages identified"
echo "🧪 Test baseline verified"
```

## Next Steps for Agent
- Identify which layer your feature belongs to (controller, service, repository, model)
- Follow Spring Boot best practices and existing patterns
- Use TDD approach: write tests first, then implementation
- Run `mvn test` frequently during development in the `backend/` directory
- Test API endpoints using curl or Postman
- Use H2 console for database inspection during development
- Keep commits atomic and descriptive
- Consider impact on frontend React components if API changes are made

## Domain Guidelines
- Follow Spring Boot layered architecture (controller → service → repository → model)
- Business logic goes in service classes (ProductService, InventoryService, etc.)
- Use JPA entities for data persistence (Product, Inventory, PurchaseOrder, OrderItem)
- Validation logic uses Jakarta validation annotations on models
- Controllers handle HTTP concerns and delegate to services
- Services contain business rules and coordinate repository operations
- Repositories provide data access abstraction using Spring Data JPA
- Use DTOs for complex request/response objects when needed
- Error handling should provide meaningful messages (current improvement task)
- Configuration classes handle cross-cutting concerns (CORS, data initialization)