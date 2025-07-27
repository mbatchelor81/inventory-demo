package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.Supplier;
import com.example.inventory_service_demo.model.SupplierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByStatus(SupplierStatus status);
    boolean existsByName(String name);
}
