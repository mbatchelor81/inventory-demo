package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Inventory;
import com.example.inventory_service_demo.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventoryList = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventoryList);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable Long productId) {
        return inventoryService.getInventoryByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable Long productId,
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
    public ResponseEntity<Inventory> adjustInventory(
            @PathVariable Long productId,
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
