package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.dto.CreatePurchaseOrderDto;
import com.example.inventory_service_demo.model.OrderStatus;
import com.example.inventory_service_demo.model.PurchaseOrder;
import com.example.inventory_service_demo.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for purchase order operations.
 */
@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    /**
     * Get all purchase orders.
     *
     * @return List of all purchase orders
     */
    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllOrders());
    }

    /**
     * Get a purchase order by ID.
     *
     * @param id The ID of the purchase order
     * @return The purchase order if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getOrderById(@PathVariable Long id) {
        return purchaseOrderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
    }

    /**
     * Get purchase orders by status.
     *
     * @param status The order status to filter by
     * @return List of purchase orders with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrder>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersByStatus(status));
    }

    /**
     * Get purchase orders by customer email.
     *
     * @param email The customer email to filter by
     * @return List of purchase orders for the specified customer email
     */
    @GetMapping("/customer")
    public ResponseEntity<List<PurchaseOrder>> getOrdersByCustomerEmail(@RequestParam String email) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersByCustomerEmail(email));
    }

    /**
     * Create a new purchase order.
     *
     * @param orderDto The DTO containing order information
     * @return The created purchase order
     */
    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@Valid @RequestBody CreatePurchaseOrderDto orderDto) {
        try {
            PurchaseOrder createdOrder = purchaseOrderService.createOrder(orderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Process an order.
     *
     * @param id The ID of the order to process
     * @return The updated purchase order
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<PurchaseOrder> processOrder(@PathVariable Long id) {
        try {
            PurchaseOrder processedOrder = purchaseOrderService.processOrder(id);
            return ResponseEntity.ok(processedOrder);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Cancel an order.
     *
     * @param id The ID of the order to cancel
     * @return The updated purchase order
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrder> cancelOrder(@PathVariable Long id) {
        try {
            PurchaseOrder cancelledOrder = purchaseOrderService.cancelOrder(id);
            return ResponseEntity.ok(cancelledOrder);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Get orders created between two dates.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of orders created between the specified dates
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<PurchaseOrder>> getOrdersBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersBetweenDates(startDate, endDate));
    }
}
