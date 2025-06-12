package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Inventory;
import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.InventoryRepository;
import com.example.inventory_service_demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Transactional
    public Inventory createOrUpdateInventory(Long productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        Optional<Inventory> existingInventory = inventoryRepository.findByProduct(product);

        if (existingInventory.isPresent()) {
            Inventory inventory = existingInventory.get();
            inventory.setQuantity(quantity);
            return inventoryRepository.save(inventory);
        } else {
            Inventory newInventory = new Inventory(product, quantity);
            return inventoryRepository.save(newInventory);
        }
    }

    @Transactional
    public Inventory adjustInventory(Long productId, int quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        Optional<Inventory> existingInventory = inventoryRepository.findByProduct(product);
        
        if (existingInventory.isPresent()) {
            Inventory inventory = existingInventory.get();
            int newQuantity = inventory.getQuantity() + quantityChange;
            
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Cannot reduce inventory below zero");
            }
            
            inventory.setQuantity(newQuantity);
            return inventoryRepository.save(inventory);
        } else {
            if (quantityChange < 0) {
                throw new IllegalArgumentException("Cannot reduce non-existent inventory");
            }
            Inventory newInventory = new Inventory(product, quantityChange);
            return inventoryRepository.save(newInventory);
        }
    }
}
