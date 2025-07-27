package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Supplier;
import com.example.inventory_service_demo.model.SupplierStatus;
import com.example.inventory_service_demo.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    public List<Supplier> getSuppliersByStatus(SupplierStatus status) {
        return supplierRepository.findByStatus(status);
    }

    public Supplier createSupplier(Supplier supplier) {
        if (supplierRepository.existsByName(supplier.getName())) {
            throw new IllegalArgumentException("Supplier with name " + supplier.getName() + " already exists");
        }
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long id, Supplier supplierDetails) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));

        if (!supplier.getName().equals(supplierDetails.getName()) && 
            supplierRepository.existsByName(supplierDetails.getName())) {
            throw new IllegalArgumentException("Supplier with name " + supplierDetails.getName() + " already exists");
        }

        supplier.setName(supplierDetails.getName());
        supplier.setContactEmail(supplierDetails.getContactEmail());
        supplier.setContactPhone(supplierDetails.getContactPhone());
        supplier.setAddress(supplierDetails.getAddress());
        supplier.setStatus(supplierDetails.getStatus());

        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));
        supplierRepository.delete(supplier);
    }
}
