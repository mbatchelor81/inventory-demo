# SQL Injection in Search Functionality

## Why This Works for Demo:

### High Impact:
- 🔴 **CRITICAL/BLOCKER** severity - gets immediate attention
- #1 most common real-world vulnerability (OWASP Top 10)
- Realistic scenario: "I was just trying to make search more flexible"
- Shows up prominently in SonarQube dashboard

### Easy to Remediate:
- Clear fix: Replace string concatenation with PreparedStatement
- Devin can easily detect and fix
- Single method change
- Fast remediation (~2-3 minutes)

### Why Developers Do This:
- Seems easier than learning PreparedStatement syntax
- Works fine in testing with clean inputs
- "Just a quick prototype" that makes it to production
- Trying to build dynamic WHERE clauses

## Implementation:

```java
// Add to: backend/src/main/java/com/example/inventory_service_demo/repository/ProductRepository.java

package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.Product;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {
    
    private final DataSource dataSource;
    
    public ProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    // INTENTIONAL VULNERABILITY: SQL Injection via string concatenation
    public List<Product> searchProducts(String searchTerm) throws SQLException {
        List<Product> products = new ArrayList<>();
        
        // BLOCKER: SQL Injection - user input concatenated directly into query
        String query = "SELECT * FROM products WHERE name LIKE '%" + searchTerm + "%' OR description LIKE '%" + searchTerm + "%'";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }
        }
        
        return products;
    }
}
```

## The Fix (for Devin):

```java
// FIXED VERSION: Use PreparedStatement with parameterized query
public List<Product> searchProducts(String searchTerm) throws SQLException {
    List<Product> products = new ArrayList<>();
    
    String query = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";
    
    try (Connection conn = dataSource.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        String searchPattern = "%" + searchTerm + "%";
        pstmt.setString(1, searchPattern);
        pstmt.setString(2, searchPattern);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                products.add(product);
            }
        }
    }
    
    return products;
}
```
