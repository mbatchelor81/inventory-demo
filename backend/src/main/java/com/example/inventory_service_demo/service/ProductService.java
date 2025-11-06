package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.exception.HashGenerationException;
import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public Product createProduct(Product product) {
        // Check if product with the same SKU already exists
        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + product.getSku() + " already exists");
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // If SKU is changing, check that the new SKU doesn't already exist
        if (!product.getSku().equals(productDetails.getSku()) && 
            productRepository.existsBySku(productDetails.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + productDetails.getSku() + " already exists");
        }

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setSku(productDetails.getSku());
        product.setPrice(productDetails.getPrice());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        productRepository.delete(product);
    }
    
    public List<Product> searchProducts(String searchTerm) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm);
    }
    
    // INTENTIONAL VULNERABILITY #2: Weak Cryptography - Using MD5 for hashing
    @SuppressWarnings("java:S4790")
    public String generateProductHash(String productData) {
        try {
            // Vulnerable: MD5 is cryptographically broken and should not be used
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(productData.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new HashGenerationException("MD5 algorithm not found", e);
        }
    }
    
    // Fixed: Using SecureRandom as a reusable instance for cryptographically secure random numbers
    public String generateProductCode() {
        int code = secureRandom.nextInt(999999);
        return String.format("PROD-%06d", code);
    }
}
