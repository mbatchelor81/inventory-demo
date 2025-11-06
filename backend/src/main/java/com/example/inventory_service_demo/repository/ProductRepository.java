package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Product> findByNameContainingIgnoreCase(String name);
    
    default List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        
        try {
            DataSource dataSource = null;
            
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return products;
    }
}
