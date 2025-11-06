package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

    @Test
    void testGetAllCategoriesReturnsListOfCategories() throws Exception {
        Category category1 = new Category("Electronics", "Electronic devices");
        category1.setId(1L);
        
        Category category2 = new Category("Books", "Various books");
        category2.setId(2L);
        
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].name").value("Books"));
    }

    @Test
    void testGetCategoryByIdReturnsCategory() throws Exception {
        Category category = new Category("Furniture", "Home furniture");
        category.setId(1L);
        
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Furniture"))
                .andExpect(jsonPath("$.description").value("Home furniture"));
    }

    @Test
    void testGetCategoryByIdReturnsNotFoundForNonExistent() throws Exception {
        when(categoryService.getCategoryById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCategoryCreatesNewCategory() throws Exception {
        Category category = new Category("Sports", "Sports equipment");
        Category savedCategory = new Category("Sports", "Sports equipment");
        savedCategory.setId(1L);
        
        when(categoryService.createCategory(any(Category.class))).thenReturn(savedCategory);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sports"));
    }

    @Test
    void testCreateCategoryReturnsBadRequestForInvalidData() throws Exception {
        Category invalidCategory = new Category("", "No name");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCategoryUpdatesExistingCategory() throws Exception {
        Category updateData = new Category("Toys & Games", "Children toys and games");
        Category updatedCategory = new Category("Toys & Games", "Children toys and games");
        updatedCategory.setId(1L);
        
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Toys & Games"));
    }

    @Test
    void testUpdateCategoryReturnsNotFoundForNonExistent() throws Exception {
        Category updateData = new Category("NonExistent", "Does not exist");
        
        when(categoryService.updateCategory(eq(99L), any(Category.class)))
                .thenThrow(new IllegalArgumentException("Category not found"));

        mockMvc.perform(put("/api/categories/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCategoryRemovesCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCategoryReturnsNotFoundForNonExistent() throws Exception {
        doThrow(new IllegalArgumentException("Category not found"))
                .when(categoryService).deleteCategory(99L);

        mockMvc.perform(delete("/api/categories/99"))
                .andExpect(status().isNotFound());
    }
}
