# Inventory Management System

A full-stack inventory management application built by **Windsurf Enterprises, Inc** that provides comprehensive product tracking, inventory management, and purchase order processing capabilities.

## 🚀 Quick Start

Get the entire application running with a single command:

```bash
./start.sh
```

This will start both the backend (Spring Boot) and frontend (React) services, perform health checks, and provide you with access URLs.

**Access URLs:**
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Backend Health**: http://localhost:8080/actuator/health
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:inventorydb`
  - Username: `sa`
  - Password: `password`

## 📋 Features

### Dashboard
- Real-time inventory statistics
- Low stock alerts (items with quantity < 10)
- Pending orders tracking
- Quick action buttons for common tasks

### Product Management
- Create, read, update, and delete products
- SKU-based product identification
- Price and description management
- Product validation with Jakarta Bean Validation

### Inventory Tracking
- Real-time inventory levels
- Inventory adjustments (increase/decrease quantities)
- Product-based inventory lookup
- Automatic inventory updates from orders

### Purchase Order Processing
- Create and manage purchase orders
- Order status tracking (CREATED, PROCESSING, COMPLETED, CANCELLED)
- Customer information management
- Order item management with automatic total calculation
- Order processing and cancellation workflows

## 🛠 Technology Stack

### Backend
- **Spring Boot 3.5.0** - Main application framework
- **Spring Data JPA** - Data persistence layer
- **Spring Boot Validation** - Input validation
- **H2 Database** - In-memory database for development
- **Java 21** - Programming language
- **Maven** - Build and dependency management

### Frontend
- **React 19.1.0** - UI framework
- **React Router DOM 7.7.1** - Client-side routing
- **Create React App** - Build tooling
- **CSS3** - Styling
- **Fetch API** - HTTP client for API communication

## 📁 Project Structure

```
inventory-demo/
├── backend/                          # Spring Boot application
│   ├── src/main/java/com/example/inventory_service_demo/
│   │   ├── controller/              # REST API controllers
│   │   │   ├── ProductController.java
│   │   │   ├── InventoryController.java
│   │   │   ├── PurchaseOrderController.java
│   │   │   └── PingController.java
│   │   ├── service/                 # Business logic layer
│   │   ├── repository/              # Data access layer
│   │   ├── model/                   # JPA entities
│   │   │   ├── Product.java
│   │   │   ├── Inventory.java
│   │   │   ├── PurchaseOrder.java
│   │   │   └── OrderItem.java
│   │   ├── dto/                     # Data transfer objects
│   │   └── config/                  # Configuration classes
│   ├── pom.xml                      # Maven dependencies
│   └── mvnw                         # Maven wrapper
├── frontend/                        # React application
│   ├── src/
│   │   ├── components/              # React components
│   │   │   ├── dashboard/           # Dashboard components
│   │   │   ├── products/            # Product management
│   │   │   ├── inventory/           # Inventory tracking
│   │   │   ├── orders/              # Order processing
│   │   │   ├── layout/              # Navigation and layout
│   │   │   └── common/              # Shared components
│   │   ├── services/                # API service layer
│   │   └── App.js                   # Main application component
│   ├── package.json                 # npm dependencies
│   └── public/                      # Static assets
├── start.sh                         # Application startup script
└── logs/                           # Application logs (created at runtime)
```

## 🔌 API Documentation

### Products API (`/api/products`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/sku/{sku}` | Get product by SKU |
| POST | `/api/products` | Create new product |
| PUT | `/api/products/{id}` | Update existing product |
| DELETE | `/api/products/{id}` | Delete product |

**Product Model:**
```json
{
  "id": 1,
  "name": "Product Name",
  "description": "Product description",
  "sku": "PROD-001",
  "price": 29.99
}
```

### Inventory API (`/api/inventory`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/inventory` | Get all inventory records |
| GET | `/api/inventory/{productId}` | Get inventory for specific product |
| PUT | `/api/inventory/{productId}` | Set inventory quantity |
| PATCH | `/api/inventory/{productId}/adjust` | Adjust inventory quantity |

**Inventory Model:**
```json
{
  "id": 1,
  "product": { /* Product object */ },
  "quantity": 50
}
```

### Purchase Orders API (`/api/orders`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/status/{status}` | Get orders by status |
| GET | `/api/orders/customer?email={email}` | Get orders by customer email |
| GET | `/api/orders/date-range?startDate={start}&endDate={end}` | Get orders by date range |
| POST | `/api/orders` | Create new order |
| POST | `/api/orders/{id}/process` | Process order |
| POST | `/api/orders/{id}/cancel` | Cancel order |

**Order Statuses:** `CREATED`, `PROCESSING`, `COMPLETED`, `CANCELLED`

## 🏗 Development Setup

### Prerequisites
- **Java 21** or later
- **Maven 3.6+**
- **Node.js 14+**
- **npm 6+**

### Manual Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd inventory-demo
   ```

2. **Backend setup:**
   ```bash
   cd backend
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

3. **Frontend setup (in a new terminal):**
   ```bash
   cd frontend
   npm install
   npm start
   ```

### Development Commands

**Backend:**
```bash
cd backend
./mvnw clean compile          # Compile the application
./mvnw test                   # Run tests
./mvnw spring-boot:run        # Start development server
```

**Frontend:**
```bash
cd frontend
npm install                   # Install dependencies
npm start                     # Start development server
npm test                      # Run tests
npm run build                 # Build for production
```

### Feature Development Workflow

1. **Create feature branch:**
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/your-feature-name
   ```

2. **Verify baseline:**
   ```bash
   cd backend && ./mvnw test
   cd ../frontend && npm test -- --watchAll=false
   ```

3. **Start development:**
   ```bash
   ./start.sh  # Start both services
   ```

4. **Access development tools:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

## 🧪 Testing

**Run all tests:**
```bash
# Backend tests
cd backend && ./mvnw test

# Frontend tests  
cd frontend && npm test -- --watchAll=false
```

**Build verification:**
```bash
# Backend build
cd backend && ./mvnw compile

# Frontend build
cd frontend && npm run build
```

## 🚀 Production Deployment

1. **Build the application:**
   ```bash
   cd backend && ./mvnw clean package
   cd ../frontend && npm run build
   ```

2. **Run the JAR file:**
   ```bash
   java -jar backend/target/inventory-service-demo-0.0.1-SNAPSHOT.jar
   ```

3. **Serve frontend static files** using a web server like nginx or Apache.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Follow the development workflow above
4. Ensure all tests pass
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## 📝 License

This project is built by **Windsurf Enterprises, Inc**.

## 🆘 Troubleshooting

**Port conflicts:**
- The `start.sh` script automatically kills processes on ports 3000 and 8080
- Manually kill processes: `lsof -ti:3000 | xargs kill -9`

**Database issues:**
- H2 database is in-memory and resets on application restart
- Access H2 console at http://localhost:8080/h2-console for debugging

**Build issues:**
- Ensure Java 21 and Node.js 14+ are installed
- Clear Maven cache: `./mvnw dependency:purge-local-repository`
- Clear npm cache: `npm cache clean --force`

**Log files:**
- Backend logs: `logs/backend.log`
- Frontend logs: `logs/frontend.log`
