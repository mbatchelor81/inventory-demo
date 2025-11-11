package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final Path exportsBase;

    @Autowired
    public ProductController(ProductService productService, 
                           @Value("${app.exports.dir}") String exportsDir) {
        this.productService = productService;
        this.exportsBase = Paths.get(exportsDir);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Product> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // INTENTIONAL VULNERABILITY: SQL Injection endpoint
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        List<Product> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/export/{filename}")
    public ResponseEntity<String> exportProductData(@PathVariable String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                return ResponseEntity.badRequest().body("Filename cannot be empty");
            }
            
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                return ResponseEntity.badRequest().body("Invalid filename");
            }
            
            if (!filename.matches("^[a-zA-Z0-9._-]+$")) {
                return ResponseEntity.badRequest().body("Filename contains invalid characters");
            }
            
            Path candidate = exportsBase.resolve(filename).normalize();
            
            if (!Files.exists(candidate) || !Files.isRegularFile(candidate)) {
                return ResponseEntity.notFound().build();
            }
            
            Path baseReal = exportsBase.toRealPath(LinkOption.NOFOLLOW_LINKS);
            Path candidateReal = candidate.toRealPath(LinkOption.NOFOLLOW_LINKS);
            
            if (!candidateReal.startsWith(baseReal)) {
                return ResponseEntity.badRequest().body("Access denied");
            }
            
            String content = Files.readString(candidateReal, StandardCharsets.UTF_8);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            // FIXED: Information Disclosure - Generic error message without internal details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file");
        }
    }
}
