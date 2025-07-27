package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.Supplier;
import com.example.inventory_service_demo.model.SupplierStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SupplierRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier activeSupplier;
    private Supplier inactiveSupplier;

    @BeforeEach
    void setUp() {
        activeSupplier = new Supplier("Active Supplier", "active@example.com", "123-456-7890", "123 Active St", SupplierStatus.ACTIVE);
        inactiveSupplier = new Supplier("Inactive Supplier", "inactive@example.com", "987-654-3210", "456 Inactive St", SupplierStatus.INACTIVE);
        
        entityManager.persistAndFlush(activeSupplier);
        entityManager.persistAndFlush(inactiveSupplier);
    }

    @Test
    void findByStatus_WithActiveStatus_ShouldReturnActiveSuppliers() {
        List<Supplier> activeSuppliers = supplierRepository.findByStatus(SupplierStatus.ACTIVE);

        assertEquals(1, activeSuppliers.size());
        assertEquals("Active Supplier", activeSuppliers.get(0).getName());
        assertEquals(SupplierStatus.ACTIVE, activeSuppliers.get(0).getStatus());
    }

    @Test
    void findByStatus_WithInactiveStatus_ShouldReturnInactiveSuppliers() {
        List<Supplier> inactiveSuppliers = supplierRepository.findByStatus(SupplierStatus.INACTIVE);

        assertEquals(1, inactiveSuppliers.size());
        assertEquals("Inactive Supplier", inactiveSuppliers.get(0).getName());
        assertEquals(SupplierStatus.INACTIVE, inactiveSuppliers.get(0).getStatus());
    }

    @Test
    void existsByName_WithExistingName_ShouldReturnTrue() {
        boolean exists = supplierRepository.existsByName("Active Supplier");

        assertTrue(exists);
    }

    @Test
    void existsByName_WithNonExistingName_ShouldReturnFalse() {
        boolean exists = supplierRepository.existsByName("Non-existing Supplier");

        assertFalse(exists);
    }

    @Test
    void existsByName_WithCaseSensitiveName_ShouldReturnFalse() {
        boolean exists = supplierRepository.existsByName("active supplier");

        assertFalse(exists);
    }
}
