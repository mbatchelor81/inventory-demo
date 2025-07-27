package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.model.Supplier;
import com.example.inventory_service_demo.model.SupplierStatus;
import com.example.inventory_service_demo.service.SupplierService;
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

@WebMvcTest(SupplierController.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierService supplierService;

    @Autowired
    private ObjectMapper objectMapper;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        testSupplier = new Supplier("Test Supplier", "test@example.com", "123-456-7890", "123 Test St", SupplierStatus.ACTIVE);
        testSupplier.setId(1L);
    }

    @Test
    void getAllSuppliers_ShouldReturnAllSuppliers() throws Exception {
        List<Supplier> suppliers = Arrays.asList(testSupplier);
        when(supplierService.getAllSuppliers()).thenReturn(suppliers);

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Supplier"))
                .andExpect(jsonPath("$[0].contactEmail").value("test@example.com"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(supplierService).getAllSuppliers();
    }

    @Test
    void getSupplierById_WhenSupplierExists_ShouldReturnSupplier() throws Exception {
        when(supplierService.getSupplierById(1L)).thenReturn(Optional.of(testSupplier));

        mockMvc.perform(get("/api/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Supplier"))
                .andExpect(jsonPath("$.contactEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(supplierService).getSupplierById(1L);
    }

    @Test
    void getSupplierById_WhenSupplierDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(supplierService.getSupplierById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/suppliers/1"))
                .andExpect(status().isNotFound());

        verify(supplierService).getSupplierById(1L);
    }

    @Test
    void getSuppliersByStatus_ShouldReturnSuppliersWithGivenStatus() throws Exception {
        List<Supplier> activeSuppliers = Arrays.asList(testSupplier);
        when(supplierService.getSuppliersByStatus(SupplierStatus.ACTIVE)).thenReturn(activeSuppliers);

        mockMvc.perform(get("/api/suppliers/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Supplier"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(supplierService).getSuppliersByStatus(SupplierStatus.ACTIVE);
    }

    @Test
    void createSupplier_WithValidData_ShouldCreateSupplier() throws Exception {
        when(supplierService.createSupplier(any(Supplier.class))).thenReturn(testSupplier);

        mockMvc.perform(post("/api/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSupplier)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Supplier"))
                .andExpect(jsonPath("$.contactEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(supplierService).createSupplier(any(Supplier.class));
    }

    @Test
    void createSupplier_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Supplier invalidSupplier = new Supplier("", "invalid-email", "123-456-7890", "123 Test St", SupplierStatus.ACTIVE);

        mockMvc.perform(post("/api/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSupplier)))
                .andExpect(status().isBadRequest());

        verify(supplierService, never()).createSupplier(any());
    }

    @Test
    void createSupplier_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        when(supplierService.createSupplier(any(Supplier.class)))
                .thenThrow(new IllegalArgumentException("Supplier with name Test Supplier already exists"));

        mockMvc.perform(post("/api/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSupplier)))
                .andExpect(status().isBadRequest());

        verify(supplierService).createSupplier(any(Supplier.class));
    }

    @Test
    void updateSupplier_WithValidData_ShouldUpdateSupplier() throws Exception {
        Supplier updatedSupplier = new Supplier("Updated Supplier", "updated@example.com", "987-654-3210", "456 Updated St", SupplierStatus.INACTIVE);
        updatedSupplier.setId(1L);
        
        when(supplierService.updateSupplier(eq(1L), any(Supplier.class))).thenReturn(updatedSupplier);

        mockMvc.perform(put("/api/suppliers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSupplier)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Supplier"))
                .andExpect(jsonPath("$.contactEmail").value("updated@example.com"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));

        verify(supplierService).updateSupplier(eq(1L), any(Supplier.class));
    }

    @Test
    void updateSupplier_WhenSupplierDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(supplierService.updateSupplier(eq(1L), any(Supplier.class)))
                .thenThrow(new IllegalArgumentException("Supplier not found with id: 1"));

        mockMvc.perform(put("/api/suppliers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSupplier)))
                .andExpect(status().isNotFound());

        verify(supplierService).updateSupplier(eq(1L), any(Supplier.class));
    }

    @Test
    void deleteSupplier_WhenSupplierExists_ShouldDeleteSupplier() throws Exception {
        doNothing().when(supplierService).deleteSupplier(1L);

        mockMvc.perform(delete("/api/suppliers/1"))
                .andExpect(status().isNoContent());

        verify(supplierService).deleteSupplier(1L);
    }

    @Test
    void deleteSupplier_WhenSupplierDoesNotExist_ShouldReturnNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Supplier not found with id: 1"))
                .when(supplierService).deleteSupplier(1L);

        mockMvc.perform(delete("/api/suppliers/1"))
                .andExpect(status().isNotFound());

        verify(supplierService).deleteSupplier(1L);
    }
}
