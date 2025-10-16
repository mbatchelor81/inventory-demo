package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.CategoryRepository;
import com.example.inventory_service_demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found with id: ";

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        
        if (category.getParentCategoryId() != null) {
            categoryRepository.findById(category.getParentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with id: " + category.getParentCategoryId()));
        }
        
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(CATEGORY_NOT_FOUND_MESSAGE + id));

        if (!category.getName().equals(categoryDetails.getName()) && 
            categoryRepository.findByName(categoryDetails.getName()).isPresent()) {
            throw new IllegalArgumentException("Category with name '" + categoryDetails.getName() + "' already exists");
        }
        
        if (categoryDetails.getParentCategoryId() != null) {
            categoryRepository.findById(categoryDetails.getParentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with id: " + categoryDetails.getParentCategoryId()));
        }

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setParentCategoryId(categoryDetails.getParentCategoryId());
        category.setActive(categoryDetails.getActive());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(CATEGORY_NOT_FOUND_MESSAGE + id));
        
        List<Product> productsInCategory = productRepository.findByCategoryId(id);
        if (!productsInCategory.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with assigned products. Found " + 
                    productsInCategory.size() + " product(s) in this category.");
        }
        
        List<Category> subcategories = categoryRepository.findByParentCategoryId(id);
        if (!subcategories.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete category with subcategories. Found " + 
                    subcategories.size() + " subcategory(ies).");
        }
        
        categoryRepository.delete(category);
    }

    public List<Category> getSubcategories(Long parentId) {
        return categoryRepository.findByParentCategoryId(parentId);
    }

    @Transactional
    public Product assignProductToCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));
        
        product.setCategory(category);
        return productRepository.save(product);
    }
}
