package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Supplier;
import com.example.inventory_service_demo.model.SupplierStatus;
import com.example.inventory_service_demo.repository.SupplierRepository;
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
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        testSupplier = new Supplier("Test Supplier", "test@example.com", "123-456-7890", "123 Test St", SupplierStatus.ACTIVE);
        testSupplier.setId(1L);
    }

    @Test
    void getAllSuppliers_ShouldReturnAllSuppliers() {
        List<Supplier> suppliers = Arrays.asList(testSupplier);
        when(supplierRepository.findAll()).thenReturn(suppliers);

        List<Supplier> result = supplierService.getAllSuppliers();

        assertEquals(1, result.size());
        assertEquals(testSupplier, result.get(0));
        verify(supplierRepository).findAll();
    }

    @Test
    void getSupplierById_WhenSupplierExists_ShouldReturnSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

        Optional<Supplier> result = supplierService.getSupplierById(1L);

        assertTrue(result.isPresent());
        assertEquals(testSupplier, result.get());
        verify(supplierRepository).findById(1L);
    }

    @Test
    void getSupplierById_WhenSupplierDoesNotExist_ShouldReturnEmpty() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Supplier> result = supplierService.getSupplierById(1L);

        assertFalse(result.isPresent());
        verify(supplierRepository).findById(1L);
    }

    @Test
    void getSuppliersByStatus_ShouldReturnSuppliersWithGivenStatus() {
        List<Supplier> activeSuppliers = Arrays.asList(testSupplier);
        when(supplierRepository.findByStatus(SupplierStatus.ACTIVE)).thenReturn(activeSuppliers);

        List<Supplier> result = supplierService.getSuppliersByStatus(SupplierStatus.ACTIVE);

        assertEquals(1, result.size());
        assertEquals(testSupplier, result.get(0));
        verify(supplierRepository).findByStatus(SupplierStatus.ACTIVE);
    }

    @Test
    void createSupplier_WhenNameIsUnique_ShouldCreateSupplier() {
        when(supplierRepository.existsByName("Test Supplier")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

        Supplier result = supplierService.createSupplier(testSupplier);

        assertEquals(testSupplier, result);
        verify(supplierRepository).existsByName("Test Supplier");
        verify(supplierRepository).save(testSupplier);
    }

    @Test
    void createSupplier_WhenNameAlreadyExists_ShouldThrowException() {
        when(supplierRepository.existsByName("Test Supplier")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> supplierService.createSupplier(testSupplier));

        assertEquals("Supplier with name Test Supplier already exists", exception.getMessage());
        verify(supplierRepository).existsByName("Test Supplier");
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void updateSupplier_WhenSupplierExists_ShouldUpdateSupplier() {
        Supplier updatedDetails = new Supplier("Updated Supplier", "updated@example.com", "987-654-3210", "456 Updated St", SupplierStatus.INACTIVE);
        
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.existsByName("Updated Supplier")).thenReturn(false);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(testSupplier);

        Supplier result = supplierService.updateSupplier(1L, updatedDetails);

        assertEquals("Updated Supplier", testSupplier.getName());
        assertEquals("updated@example.com", testSupplier.getContactEmail());
        assertEquals("987-654-3210", testSupplier.getContactPhone());
        assertEquals("456 Updated St", testSupplier.getAddress());
        assertEquals(SupplierStatus.INACTIVE, testSupplier.getStatus());
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).save(testSupplier);
    }

    @Test
    void updateSupplier_WhenSupplierDoesNotExist_ShouldThrowException() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> supplierService.updateSupplier(1L, testSupplier));

        assertEquals("Supplier not found with id: 1", exception.getMessage());
        verify(supplierRepository).findById(1L);
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void updateSupplier_WhenNewNameAlreadyExists_ShouldThrowException() {
        Supplier existingSupplier = new Supplier("Existing Supplier", "existing@example.com", "111-222-3333", "789 Existing St", SupplierStatus.ACTIVE);
        existingSupplier.setId(1L);
        
        Supplier updatedDetails = new Supplier("Another Supplier", "another@example.com", "444-555-6666", "321 Another St", SupplierStatus.ACTIVE);
        
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.existsByName("Another Supplier")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> supplierService.updateSupplier(1L, updatedDetails));

        assertEquals("Supplier with name Another Supplier already exists", exception.getMessage());
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).existsByName("Another Supplier");
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void deleteSupplier_WhenSupplierExists_ShouldDeleteSupplier() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

        supplierService.deleteSupplier(1L);

        verify(supplierRepository).findById(1L);
        verify(supplierRepository).delete(testSupplier);
    }

    @Test
    void deleteSupplier_WhenSupplierDoesNotExist_ShouldThrowException() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> supplierService.deleteSupplier(1L));

        assertEquals("Supplier not found with id: 1", exception.getMessage());
        verify(supplierRepository).findById(1L);
        verify(supplierRepository, never()).delete(any());
    }
}
