package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private static @TempDir Path tempExportDir;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.exports.dir", () -> tempExportDir.toString());
    }

    @BeforeEach
    void setUp() throws Exception {
        Files.writeString(tempExportDir.resolve("test-export.txt"), "Test export data");
    }

    @Test
    void exportProductData_withValidFilename_returnsFileContent() throws Exception {
        mockMvc.perform(get("/api/products/export/test-export.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test export data"));
    }

    @Test
    void exportProductData_withDoubleDotInFilename_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/products/export/bad..name.txt"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid filename"));
    }

    @Test
    void exportProductData_withForwardSlashInFilename_blockedBySpringRouting() throws Exception {
        mockMvc.perform(get("/api/products/export/etc/passwd"))
                .andExpect(status().isNotFound());
    }

    @Test
    void exportProductData_withPathTraversalBackslash_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/products/export/etc\\passwd"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid filename"));
    }

    @Test
    void exportProductData_withInvalidCharacters_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/products/export/test@file.txt"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Filename contains invalid characters"));
    }

    @Test
    void exportProductData_withNonexistentFile_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/products/export/missing.txt"))
                .andExpect(status().isNotFound());
    }

    @Test
    void exportProductData_withEmptyFilename_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/products/export/"))
                .andExpect(status().isNotFound());
    }
}
