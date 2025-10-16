package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    
    // INTENTIONAL VULNERABILITY: SQL Injection via string concatenation
    @Query(value = "SELECT * FROM product WHERE name LIKE '%" + ":searchTerm" + "%'", nativeQuery = true)
    List<Product> searchProductsByName(@Param("searchTerm") String searchTerm);
}
