package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.CategoryRepository;
import com.example.inventory_service_demo.repository.ProductRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Electronics", "Electronic devices", null);
        testCategory.setId(1L);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        List<Category> categories = Arrays.asList(testCategory, new Category("Books", "Books and magazines", null));
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_WhenExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getName());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_WhenNotExists_ShouldReturnEmpty() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.getCategoryById(999L);

        assertFalse(result.isPresent());
        verify(categoryRepository).findById(999L);
    }

    @Test
    void createCategory_WithValidData_ShouldSaveCategory() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(testCategory);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).findByName("Electronics");
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void createCategory_WithDuplicateName_ShouldThrowException() {
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(testCategory));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.createCategory(testCategory));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(categoryRepository).findByName("Electronics");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createCategory_WithInvalidParentId_ShouldThrowException() {
        Category subcategory = new Category("Laptops", "Laptop computers", 999L);
        when(categoryRepository.findByName("Laptops")).thenReturn(Optional.empty());
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.createCategory(subcategory));

        assertTrue(exception.getMessage().contains("Parent category not found"));
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createCategory_WithValidParentId_ShouldSaveCategory() {
        Category parentCategory = new Category("Electronics", "Electronic devices", null);
        Category subcategory = new Category("Laptops", "Laptop computers", 1L);
        when(categoryRepository.findByName("Laptops")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(subcategory);

        Category result = categoryService.createCategory(subcategory);

        assertNotNull(result);
        assertEquals("Laptops", result.getName());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(subcategory);
    }

    @Test
    void updateCategory_WithValidData_ShouldUpdateCategory() {
        Category updatedDetails = new Category("Electronics Updated", "Updated description", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.updateCategory(1L, updatedDetails);

        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void updateCategory_WhenNotExists_ShouldThrowException() {
        Category updatedDetails = new Category("Electronics Updated", "Updated description", null);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.updateCategory(999L, updatedDetails));

        assertTrue(exception.getMessage().contains("Category not found with id: 999"));
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WithDuplicateName_ShouldThrowException() {
        Category existingCategory = new Category("Books", "Books and magazines", null);
        Category updatedDetails = new Category("Books", "Updated description", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findByName("Books")).thenReturn(Optional.of(existingCategory));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.updateCategory(1L, updatedDetails));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(categoryRepository).findByName("Books");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WithValidId_ShouldDeleteCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.findByCategoryId(1L)).thenReturn(Arrays.asList());
        when(categoryRepository.findByParentCategoryId(1L)).thenReturn(Arrays.asList());

        categoryService.deleteCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(productRepository).findByCategoryId(1L);
        verify(categoryRepository).findByParentCategoryId(1L);
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void deleteCategory_WhenNotExists_ShouldThrowException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.deleteCategory(999L));

        assertTrue(exception.getMessage().contains("Category not found with id: 999"));
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_WithAssignedProducts_ShouldThrowException() {
        Product product = new Product();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(product));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.deleteCategory(1L));

        assertTrue(exception.getMessage().contains("Cannot delete category with assigned products"));
        verify(productRepository).findByCategoryId(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_WithSubcategories_ShouldThrowException() {
        Category subcategory = new Category("Laptops", "Laptop computers", 1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.findByCategoryId(1L)).thenReturn(Arrays.asList());
        when(categoryRepository.findByParentCategoryId(1L)).thenReturn(Arrays.asList(subcategory));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.deleteCategory(1L));

        assertTrue(exception.getMessage().contains("Cannot delete category with subcategories"));
        verify(categoryRepository).findByParentCategoryId(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void getSubcategories_ShouldReturnSubcategories() {
        Category subcategory1 = new Category("Laptops", "Laptop computers", 1L);
        Category subcategory2 = new Category("Phones", "Mobile phones", 1L);
        when(categoryRepository.findByParentCategoryId(1L)).thenReturn(Arrays.asList(subcategory1, subcategory2));

        List<Category> result = categoryService.getSubcategories(1L);

        assertEquals(2, result.size());
        verify(categoryRepository).findByParentCategoryId(1L);
    }

    @Test
    void assignProductToCategory_WithValidIds_ShouldAssignProduct() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = categoryService.assignProductToCategory(1L, 1L);

        assertNotNull(result);
        assertEquals(testCategory, result.getCategory());
        verify(productRepository).findById(1L);
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(product);
    }

    @Test
    void assignProductToCategory_WithInvalidProductId_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.assignProductToCategory(999L, 1L));

        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        verify(productRepository).findById(999L);
        verify(categoryRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void assignProductToCategory_WithInvalidCategoryId_ShouldThrowException() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> categoryService.assignProductToCategory(1L, 999L));

        assertTrue(exception.getMessage().contains("Category not found with id: 999"));
        verify(productRepository).findById(1L);
        verify(categoryRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }
}
