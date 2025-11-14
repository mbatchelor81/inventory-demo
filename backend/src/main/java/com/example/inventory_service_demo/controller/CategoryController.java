package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.service.CategoryService;
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

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
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

    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        return handleServiceCall(
            () -> categoryService.createCategory(category),
            HttpStatus.CREATED,
            HttpStatus.BAD_REQUEST
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        return handleServiceCall(
            () -> categoryService.updateCategory(id, category),
            HttpStatus.OK,
            HttpStatus.NOT_FOUND
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return handleServiceCallVoid(
            () -> categoryService.deleteCategory(id),
            HttpStatus.NO_CONTENT,
            HttpStatus.NOT_FOUND
        );
    }

    private <T> ResponseEntity<T> handleServiceCall(
            java.util.function.Supplier<T> serviceCall,
            HttpStatus successStatus,
            HttpStatus errorStatus) {
        try {
            T result = serviceCall.get();
            return new ResponseEntity<>(result, successStatus);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(errorStatus);
        }
    }

    private ResponseEntity<Void> handleServiceCallVoid(
            Runnable serviceCall,
            HttpStatus successStatus,
            HttpStatus errorStatus) {
        try {
            serviceCall.run();
            return new ResponseEntity<>(successStatus);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(errorStatus);
        }
    }
}
