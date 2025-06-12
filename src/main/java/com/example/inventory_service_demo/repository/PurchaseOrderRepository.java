package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.OrderStatus;
import com.example.inventory_service_demo.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for PurchaseOrder entity operations.
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    
    // Find orders by status
    List<PurchaseOrder> findByStatus(OrderStatus status);
    
    // Find orders by customer email
    List<PurchaseOrder> findByCustomerEmail(String customerEmail);
    
    // Find orders created between two dates
    List<PurchaseOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find orders by customer name containing the given string (case insensitive)
    List<PurchaseOrder> findByCustomerNameContainingIgnoreCase(String customerName);
}
