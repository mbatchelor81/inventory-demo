package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.service.CategoryService;
import com.example.inventory_service_demo.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryController(CategoryService categoryService, ProductRepository productRepository) {
        this.categoryService = categoryService;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        try {
            Category createdCategory = categoryService.createCategory(category);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Long id) {
        List<Category> subcategories = categoryService.getSubcategories(id);
        return ResponseEntity.ok(subcategories);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Product>> getProductsInCategory(@PathVariable Long id) {
        List<Product> products = productRepository.findByCategoryId(id);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/products/{productId}/category/{categoryId}")
    public ResponseEntity<Product> assignProductToCategory(
            @PathVariable Long productId,
            @PathVariable Long categoryId) {
        try {
            Product product = categoryService.assignProductToCategory(productId, categoryId);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
