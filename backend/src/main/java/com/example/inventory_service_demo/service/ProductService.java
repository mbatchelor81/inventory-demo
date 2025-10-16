package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

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
    
    // INTENTIONAL VULNERABILITY: SQL Injection via string concatenation
    @SuppressWarnings("unchecked")
    public List<Product> searchProducts(String searchTerm) {
        // Vulnerable: User input directly concatenated into SQL query
        String sql = "SELECT * FROM product WHERE name LIKE '%" + searchTerm + "%'";
        return entityManager.createNativeQuery(sql, Product.class).getResultList();
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
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
    
    // INTENTIONAL VULNERABILITY #5: Insecure Random - Using predictable Random
    @SuppressWarnings("java:S2245")
    public String generateProductCode() {
        // Vulnerable: java.util.Random is predictable and not cryptographically secure
        Random random = new Random();
        int code = random.nextInt(999999);
        return String.format("PROD-%06d", code);
    }
}
