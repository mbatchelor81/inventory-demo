package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.service.CategoryService;
import com.example.inventory_service_demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductRepository productRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Electronics", "Electronic devices", null);
        testCategory.setId(1L);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() throws Exception {
        List<Category> categories = Arrays.asList(testCategory, new Category("Books", "Books and magazines", null));
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Electronics"));

        verify(categoryService).getAllCategories();
    }

    @Test
    void getCategoryById_WhenExists_ShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService).getCategoryById(1L);
    }

    @Test
    void getCategoryById_WhenNotExists_ShouldReturn404() throws Exception {
        when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());

        verify(categoryService).getCategoryById(999L);
    }

    @Test
    void createCategory_WithValidData_ShouldReturnCreated() throws Exception {
        Category newCategory = new Category("Electronics", "Electronic devices", null);
        when(categoryService.createCategory(any(Category.class))).thenReturn(testCategory);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService).createCategory(any(Category.class));
    }

    @Test
    void createCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Category invalidCategory = new Category("", "Description", null);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(Category.class));
    }

    @Test
    void createCategory_WithDuplicateName_ShouldReturnBadRequest() throws Exception {
        Category newCategory = new Category("Electronics", "Electronic devices", null);
        when(categoryService.createCategory(any(Category.class)))
                .thenThrow(new IllegalArgumentException("Category with name 'Electronics' already exists"));

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isBadRequest());

        verify(categoryService).createCategory(any(Category.class));
    }

    @Test
    void updateCategory_WithValidData_ShouldReturnUpdated() throws Exception {
        Category updatedCategory = new Category("Electronics Updated", "Updated description", null);
        updatedCategory.setId(1L);
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electronics Updated"));

        verify(categoryService).updateCategory(eq(1L), any(Category.class));
    }

    @Test
    void updateCategory_WhenNotExists_ShouldReturn404() throws Exception {
        Category updatedCategory = new Category("Electronics Updated", "Updated description", null);
        when(categoryService.updateCategory(eq(999L), any(Category.class)))
                .thenThrow(new IllegalArgumentException("Category not found with id: 999"));

        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isNotFound());

        verify(categoryService).updateCategory(eq(999L), any(Category.class));
    }

    @Test
    void deleteCategory_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void deleteCategory_WhenNotExists_ShouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Category not found with id: 999"))
                .when(categoryService).deleteCategory(999L);

        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isBadRequest());

        verify(categoryService).deleteCategory(999L);
    }

    @Test
    void deleteCategory_WithAssignedProducts_ShouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Cannot delete category with assigned products"))
                .when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isBadRequest());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void getSubcategories_ShouldReturnSubcategories() throws Exception {
        Category subcategory1 = new Category("Laptops", "Laptop computers", 1L);
        Category subcategory2 = new Category("Phones", "Mobile phones", 1L);
        when(categoryService.getSubcategories(1L)).thenReturn(Arrays.asList(subcategory1, subcategory2));

        mockMvc.perform(get("/api/categories/1/subcategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptops"));

        verify(categoryService).getSubcategories(1L);
    }

    @Test
    void getProductsInCategory_ShouldReturnProducts() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        when(productRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/categories/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        verify(productRepository).findByCategoryId(1L);
    }

    @Test
    void assignProductToCategory_WithValidIds_ShouldReturnProduct() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setCategory(testCategory);
        when(categoryService.assignProductToCategory(1L, 1L)).thenReturn(product);

        mockMvc.perform(post("/api/categories/products/1/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(categoryService).assignProductToCategory(1L, 1L);
    }

    @Test
    void assignProductToCategory_WithInvalidIds_ShouldReturnBadRequest() throws Exception {
        when(categoryService.assignProductToCategory(999L, 1L))
                .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        mockMvc.perform(post("/api/categories/products/999/category/1"))
                .andExpect(status().isBadRequest());

        verify(categoryService).assignProductToCategory(999L, 1L);
    }
}
