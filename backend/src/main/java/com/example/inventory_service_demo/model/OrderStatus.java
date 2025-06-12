package com.example.inventory_service_demo.model;

/**
 * Enum representing the possible statuses of a purchase order.
 */
public enum OrderStatus {
    CREATED,    // Order has been created but not processed
    PROCESSING, // Order is being processed
    COMPLETED,  // Order has been fulfilled and completed
    CANCELLED   // Order has been cancelled
}
