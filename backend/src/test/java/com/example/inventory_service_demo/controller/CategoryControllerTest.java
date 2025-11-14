package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.service.CategoryService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Electronics", "Electronic devices");
        testCategory.setId(1L);
    }

    @Test
    void getAllCategories_ReturnsListOfCategories() throws Exception {
        List<Category> categories = Arrays.asList(
            testCategory,
            new Category("Books", "Books and magazines")
        );
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Electronics"));
    }

    @Test
    void getCategoryById_WhenExists_ReturnsCategory() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));

        mockMvc.perform(get("/api/categories/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Electronics"))
            .andExpect(jsonPath("$.description").value("Electronic devices"));
    }

    @Test
    void getCategoryById_WhenNotExists_ReturnsNotFound() throws Exception {
        when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getCategoryByName_WhenExists_ReturnsCategory() throws Exception {
        when(categoryService.getCategoryByName("Electronics")).thenReturn(Optional.of(testCategory));

        mockMvc.perform(get("/api/categories/name/Electronics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void getCategoryByName_WhenNotExists_ReturnsNotFound() throws Exception {
        when(categoryService.getCategoryByName("NonExistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/name/NonExistent"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createCategory_WithValidData_ReturnsCreated() throws Exception {
        Category newCategory = new Category("Sports", "Sports equipment");
        Category savedCategory = new Category("Sports", "Sports equipment");
        savedCategory.setId(2L);

        when(categoryService.createCategory(any(Category.class))).thenReturn(savedCategory);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Sports"));
    }

    @Test
    void createCategory_WithDuplicateName_ReturnsBadRequest() throws Exception {
        Category newCategory = new Category("Electronics", "Duplicate name");

        when(categoryService.createCategory(any(Category.class)))
            .thenThrow(new IllegalArgumentException("Category with name Electronics already exists"));

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory_WithValidData_ReturnsOk() throws Exception {
        Category updatedCategory = new Category("Electronics Updated", "Updated description");
        updatedCategory.setId(1L);

        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Electronics Updated"));
    }

    @Test
    void updateCategory_WhenNotFound_ReturnsNotFound() throws Exception {
        Category updatedCategory = new Category("NonExistent", "Description");

        when(categoryService.updateCategory(eq(999L), any(Category.class)))
            .thenThrow(new IllegalArgumentException("Category not found"));

        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategory)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_WhenExists_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_WhenNotFound_ReturnsNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Category not found"))
            .when(categoryService).deleteCategory(999L);

        mockMvc.perform(delete("/api/categories/999"))
            .andExpect(status().isNotFound());
    }
}
