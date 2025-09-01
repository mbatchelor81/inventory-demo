package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Inventory;
import com.example.inventory_service_demo.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "APIs for managing product inventory levels")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @Operation(summary = "Get all inventory", description = "Retrieve inventory levels for all products")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved inventory data")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventoryList = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventoryList);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory by product ID", description = "Retrieve inventory level for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory found"),
        @ApiResponse(responseCode = "404", description = "Product inventory not found")
    })
    public ResponseEntity<Inventory> getInventoryByProductId(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId) {
        return inventoryService.getInventoryByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update inventory quantity", description = "Set the inventory quantity for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Inventory> updateInventory(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
            @RequestBody Map<String, Integer> request) {
        
        Integer quantity = request.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Inventory updatedInventory = inventoryService.createOrUpdateInventory(productId, quantity);
            return ResponseEntity.ok(updatedInventory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{productId}/adjust")
    @Operation(summary = "Adjust inventory quantity", description = "Adjust inventory by a positive or negative amount")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory adjusted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Inventory> adjustInventory(
            @Parameter(description = "Product ID", required = true) @PathVariable Long productId,
            @RequestBody Map<String, Integer> request) {
        
        Integer quantityChange = request.get("quantityChange");
        if (quantityChange == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Inventory updatedInventory = inventoryService.adjustInventory(productId, quantityChange);
            return ResponseEntity.ok(updatedInventory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
