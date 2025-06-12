package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.Inventory;
import com.example.inventory_service_demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct(Product product);
    Optional<Inventory> findByProductId(Long productId);
}
