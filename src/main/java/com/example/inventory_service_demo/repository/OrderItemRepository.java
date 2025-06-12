package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.OrderItem;
import com.example.inventory_service_demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for OrderItem entity operations.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Find order items by order ID
    List<OrderItem> findByOrderId(Long orderId);
    
    // Find order items by product
    List<OrderItem> findByProduct(Product product);
    
    // Find order items by product ID
    List<OrderItem> findByProductId(Long productId);
}
