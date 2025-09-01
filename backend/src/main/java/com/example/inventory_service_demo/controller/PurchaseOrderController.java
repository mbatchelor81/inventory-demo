package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.dto.CreatePurchaseOrderDto;
import com.example.inventory_service_demo.model.OrderStatus;
import com.example.inventory_service_demo.model.PurchaseOrder;
import com.example.inventory_service_demo.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Purchase Order Management", description = "APIs for managing purchase orders and order processing")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    @Operation(summary = "Get all purchase orders", description = "Retrieve a list of all purchase orders")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by ID", description = "Retrieve a specific purchase order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<PurchaseOrder> getOrderById(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        return purchaseOrderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id: " + id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieve purchase orders filtered by their status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    public ResponseEntity<List<PurchaseOrder>> getOrdersByStatus(
            @Parameter(description = "Order status", required = true) @PathVariable OrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersByStatus(status));
    }

    @GetMapping("/customer")
    @Operation(summary = "Get orders by customer email", description = "Retrieve purchase orders for a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customer orders")
    public ResponseEntity<List<PurchaseOrder>> getOrdersByCustomerEmail(
            @Parameter(description = "Customer email address", required = true) @RequestParam String email) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersByCustomerEmail(email));
    }

    @PostMapping
    @Operation(summary = "Create new purchase order", description = "Create a new purchase order with order items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid order data")
    })
    public ResponseEntity<PurchaseOrder> createOrder(@Valid @RequestBody CreatePurchaseOrderDto orderDto) {
        try {
            PurchaseOrder createdOrder = purchaseOrderService.createOrder(orderDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Process order", description = "Process a pending order and update inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order processed successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be processed")
    })
    public ResponseEntity<PurchaseOrder> processOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        try {
            PurchaseOrder processedOrder = purchaseOrderService.processOrder(id);
            return ResponseEntity.ok(processedOrder);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled")
    })
    public ResponseEntity<PurchaseOrder> cancelOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        try {
            PurchaseOrder cancelledOrder = purchaseOrderService.cancelOrder(id);
            return ResponseEntity.ok(cancelledOrder);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get orders by date range", description = "Retrieve orders created within a specific date range")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved orders")
    public ResponseEntity<List<PurchaseOrder>> getOrdersBetweenDates(
            @Parameter(description = "Start date (ISO format)", required = true) 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersBetweenDates(startDate, endDate));
    }
}
