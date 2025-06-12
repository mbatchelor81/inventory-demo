package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.dto.CreatePurchaseOrderDto;
import com.example.inventory_service_demo.dto.OrderItemDto;
import com.example.inventory_service_demo.model.OrderItem;
import com.example.inventory_service_demo.model.OrderStatus;
import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.model.PurchaseOrder;
import com.example.inventory_service_demo.repository.OrderItemRepository;
import com.example.inventory_service_demo.repository.ProductRepository;
import com.example.inventory_service_demo.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for purchase order operations.
 */
@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Autowired
    public PurchaseOrderService(
            PurchaseOrderRepository purchaseOrderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            InventoryService inventoryService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    /**
     * Get all purchase orders.
     *
     * @return List of all purchase orders
     */
    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }

    /**
     * Get a purchase order by ID.
     *
     * @param id The ID of the purchase order
     * @return Optional containing the purchase order if found
     */
    public Optional<PurchaseOrder> getOrderById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    /**
     * Get purchase orders by status.
     *
     * @param status The order status to filter by
     * @return List of purchase orders with the specified status
     */
    public List<PurchaseOrder> getOrdersByStatus(OrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    /**
     * Get purchase orders by customer email.
     *
     * @param email The customer email to filter by
     * @return List of purchase orders for the specified customer email
     */
    public List<PurchaseOrder> getOrdersByCustomerEmail(String email) {
        return purchaseOrderRepository.findByCustomerEmail(email);
    }

    /**
     * Create a new purchase order.
     *
     * @param orderDto The DTO containing order information
     * @return The created purchase order
     * @throws IllegalArgumentException if any product is not found or insufficient inventory
     */
    @Transactional
    public PurchaseOrder createOrder(CreatePurchaseOrderDto orderDto) {
        // Create a new purchase order
        PurchaseOrder order = new PurchaseOrder(orderDto.getCustomerName(), orderDto.getCustomerEmail());
        
        // Process each item in the order
        for (OrderItemDto itemDto : orderDto.getItems()) {
            // Find the product
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + itemDto.getProductId()));
            
            // Check inventory availability
            inventoryService.getInventoryByProductId(product.getId())
                    .ifPresentOrElse(inventory -> {
                        if (inventory.getQuantity() < itemDto.getQuantity()) {
                            throw new IllegalArgumentException("Insufficient inventory for product: " + product.getName() +
                                    ". Available: " + inventory.getQuantity() + ", Requested: " + itemDto.getQuantity());
                        }
                    }, () -> {
                        throw new IllegalArgumentException("No inventory found for product: " + product.getName());
                    });
            
            // Create order item
            OrderItem orderItem = new OrderItem(product, itemDto.getQuantity());
            order.addItem(orderItem);
        }
        
        // Save the order
        return purchaseOrderRepository.save(order);
    }

    /**
     * Process an order by updating its status and adjusting inventory.
     *
     * @param orderId The ID of the order to process
     * @return The updated purchase order
     * @throws IllegalArgumentException if the order is not found or cannot be processed
     */
    @Transactional
    public PurchaseOrder processOrder(Long orderId) {
        // Find the order
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        
        // Check if the order can be processed
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalArgumentException("Order cannot be processed. Current status: " + order.getStatus());
        }
        
        // Update order status
        order.setStatus(OrderStatus.PROCESSING);
        
        // Adjust inventory for each item
        for (OrderItem item : order.getItems()) {
            // Reduce inventory by the ordered quantity
            inventoryService.adjustInventory(item.getProduct().getId(), -item.getQuantity());
        }
        
        // Update order status to completed
        order.setStatus(OrderStatus.COMPLETED);
        
        // Save and return the updated order
        return purchaseOrderRepository.save(order);
    }

    /**
     * Cancel an order.
     *
     * @param orderId The ID of the order to cancel
     * @return The updated purchase order
     * @throws IllegalArgumentException if the order is not found or cannot be cancelled
     */
    @Transactional
    public PurchaseOrder cancelOrder(Long orderId) {
        // Find the order
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        
        // Check if the order can be cancelled
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Completed orders cannot be cancelled");
        }
        
        // If the order was being processed, restore inventory
        if (order.getStatus() == OrderStatus.PROCESSING) {
            for (OrderItem item : order.getItems()) {
                // Increase inventory by the ordered quantity
                inventoryService.adjustInventory(item.getProduct().getId(), item.getQuantity());
            }
        }
        
        // Update order status
        order.setStatus(OrderStatus.CANCELLED);
        
        // Save and return the updated order
        return purchaseOrderRepository.save(order);
    }

    /**
     * Get orders created between two dates.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of orders created between the specified dates
     */
    public List<PurchaseOrder> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseOrderRepository.findByOrderDateBetween(startDate, endDate);
    }
}
