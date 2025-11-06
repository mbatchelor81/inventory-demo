package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testGetAllCategoriesReturnsAllCategories() {
        Category category1 = new Category("Test Electronics", "Test electronic devices");
        categoryRepository.save(category1);
        
        Category category2 = new Category("Test Books", "Test various books");
        categoryRepository.save(category2);

        List<Category> categories = categoryService.getAllCategories();
        
        assertNotNull(categories);
        assertTrue(categories.size() >= 2);
        assertTrue(categories.stream().anyMatch(c -> "Test Electronics".equals(c.getName())));
        assertTrue(categories.stream().anyMatch(c -> "Test Books".equals(c.getName())));
    }

    @Test
    void testGetCategoryByIdReturnsCategory() {
        Category category = new Category("Furniture", "Home furniture");
        Category saved = categoryRepository.save(category);

        Optional<Category> found = categoryService.getCategoryById(saved.getId());
        
        assertTrue(found.isPresent());
        assertEquals("Furniture", found.get().getName());
        assertEquals("Home furniture", found.get().getDescription());
    }

    @Test
    void testGetCategoryByIdReturnsEmptyForNonExistent() {
        Optional<Category> found = categoryService.getCategoryById(99999L);
        
        assertFalse(found.isPresent());
    }

    @Test
    void testCreateCategorySavesNewCategory() {
        Category category = new Category("Sports", "Sports equipment");
        
        Category created = categoryService.createCategory(category);
        
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Sports", created.getName());
        assertEquals("Sports equipment", created.getDescription());
    }

    @Test
    void testUpdateCategoryUpdatesExistingCategory() {
        Category category = new Category("Toys", "Children toys");
        Category saved = categoryRepository.save(category);

        Category updateData = new Category("Toys & Games", "Children toys and games");
        updateData.setId(saved.getId());
        
        Category updated = categoryService.updateCategory(saved.getId(), updateData);
        
        assertNotNull(updated);
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Toys & Games", updated.getName());
        assertEquals("Children toys and games", updated.getDescription());
    }

    @Test
    void testUpdateCategoryThrowsExceptionForNonExistent() {
        Category updateData = new Category("NonExistent", "Does not exist");
        
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(99999L, updateData);
        });
    }

    @Test
    void testDeleteCategoryRemovesCategory() {
        Category category = new Category("ToDelete", "Will be deleted");
        Category saved = categoryRepository.save(category);
        Long categoryId = saved.getId();

        categoryService.deleteCategory(categoryId);
        
        Optional<Category> found = categoryRepository.findById(categoryId);
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteCategoryThrowsExceptionForNonExistent() {
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(99999L);
        });
    }
}
