package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Electronics", "Electronic devices");
        testCategory.setId(1L);
    }

    @Test
    void getAllCategories_ReturnsAllCategories() {
        List<Category> categories = Arrays.asList(testCategory, new Category("Books", "Books and magazines"));
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_WhenExists_ReturnsCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_WhenNotExists_ReturnsEmpty() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(999L);

        assertFalse(result.isPresent());
        verify(categoryRepository).findById(999L);
    }

    @Test
    void getCategoryByName_WhenExists_ReturnsCategory() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.getCategoryByName("Electronics");

        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getName());
        verify(categoryRepository).findByName("Electronics");
    }

    @Test
    void getCategoryByName_WhenNotExists_ReturnsEmpty() {
        when(categoryRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryByName("NonExistent");

        assertFalse(result.isPresent());
        verify(categoryRepository).findByName("NonExistent");
    }

    @Test
    void createCategory_WhenNameUnique_CreatesCategory() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(testCategory);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void createCategory_WhenNameExists_ThrowsException() {
        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(testCategory);
        });

        verify(categoryRepository).existsByName("Electronics");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenCategoryExists_UpdatesCategory() {
        Category updatedCategory = new Category("Electronics Updated", "Updated description");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Electronics Updated")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).existsByName("Electronics Updated");
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void updateCategory_WhenCategoryNotFound_ThrowsException() {
        Category updatedCategory = new Category("Electronics Updated", "Updated description");
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(999L, updatedCategory);
        });

        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenNewNameExists_ThrowsException() {
        Category updatedCategory = new Category("Books", "Updated description");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Books")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(1L, updatedCategory);
        });

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).existsByName("Books");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenNameUnchanged_UpdatesCategory() {
        Category updatedCategory = new Category("Electronics", "Updated description");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).existsByName(any());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void deleteCategory_WhenCategoryExists_DeletesCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void deleteCategory_WhenCategoryNotFound_ThrowsException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(999L);
        });

        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).delete(any());
    }
}
