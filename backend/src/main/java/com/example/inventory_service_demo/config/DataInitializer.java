package com.example.inventory_service_demo.config;

import com.example.inventory_service_demo.model.OrderItem;
import com.example.inventory_service_demo.model.Product;
import com.example.inventory_service_demo.model.PurchaseOrder;
import com.example.inventory_service_demo.model.Category;
import com.example.inventory_service_demo.repository.PurchaseOrderRepository;
import com.example.inventory_service_demo.service.InventoryService;
import com.example.inventory_service_demo.service.ProductService;
import com.example.inventory_service_demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Component to initialize sample data for the application.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CategoryService categoryService;

    @Autowired
    public DataInitializer(
            ProductService productService, 
            InventoryService inventoryService,
            PurchaseOrderRepository purchaseOrderRepository,
            CategoryService categoryService) {
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.categoryService = categoryService;
    }

    @Override
    public void run(String... args) {
        // Only initialize if no products exist
        if (productService.getAllProducts().isEmpty()) {
            loadSampleData();
        }
    }

    private void loadSampleData() {
        Category electronics = new Category(
                "Electronics",
                "Electronic devices and gadgets"
        );
        Category audio = new Category(
                "Audio",
                "Audio equipment and accessories"
        );
        Category wearables = new Category(
                "Wearables",
                "Wearable technology and fitness devices"
        );
        
        Category savedElectronics = categoryService.createCategory(electronics);
        Category savedAudio = categoryService.createCategory(audio);
        Category savedWearables = categoryService.createCategory(wearables);
        
        Product laptop = new Product(
                "Laptop Pro X1",
                "High-performance laptop with 16GB RAM and 512GB SSD",
                "LP-X1-2025",
                new BigDecimal("1299.99")
        );
        laptop.setCategory(savedElectronics);
        
        Product smartphone = new Product(
                "SmartPhone Galaxy",
                "Latest smartphone with 128GB storage and 5G capability",
                "SP-G-2025",
                new BigDecimal("899.99")
        );
        smartphone.setCategory(savedElectronics);
        
        Product headphones = new Product(
                "Noise Cancelling Headphones",
                "Wireless headphones with 24-hour battery life",
                "NC-HP-2025",
                new BigDecimal("249.99")
        );
        headphones.setCategory(savedAudio);
        
        Product tablet = new Product(
                "Tablet Air",
                "Lightweight tablet with 10-inch display",
                "TA-10-2025",
                new BigDecimal("499.99")
        );
        tablet.setCategory(savedElectronics);
        
        Product smartwatch = new Product(
                "Fitness Watch Pro",
                "Smartwatch with heart rate monitor and GPS",
                "FW-P-2025",
                new BigDecimal("199.99")
        );
        smartwatch.setCategory(savedWearables);

        // Save products
        Product savedLaptop = productService.createProduct(laptop);
        Product savedSmartphone = productService.createProduct(smartphone);
        Product savedHeadphones = productService.createProduct(headphones);
        Product savedTablet = productService.createProduct(tablet);
        Product savedSmartwatch = productService.createProduct(smartwatch);

        // Initialize inventory
        inventoryService.createOrUpdateInventory(savedLaptop.getId(), 15);
        inventoryService.createOrUpdateInventory(savedSmartphone.getId(), 25);
        inventoryService.createOrUpdateInventory(savedHeadphones.getId(), 50);
        inventoryService.createOrUpdateInventory(savedTablet.getId(), 20);
        inventoryService.createOrUpdateInventory(savedSmartwatch.getId(), 30);

        // Create sample purchase orders
        
        // Order 1: Completed order with multiple items
        PurchaseOrder order1 = new PurchaseOrder("John Doe", "john.doe@example.com");
        order1.setOrderDate(LocalDateTime.now().minusDays(5));
        order1.setStatus(com.example.inventory_service_demo.model.OrderStatus.COMPLETED);
        
        OrderItem order1Item1 = new OrderItem(savedLaptop, 1);
        OrderItem order1Item2 = new OrderItem(savedHeadphones, 1);
        
        order1.addItem(order1Item1);
        order1.addItem(order1Item2);
        purchaseOrderRepository.save(order1);
        
        // Order 2: Processing order
        PurchaseOrder order2 = new PurchaseOrder("Jane Smith", "jane.smith@example.com");
        order2.setOrderDate(LocalDateTime.now().minusDays(2));
        order2.setStatus(com.example.inventory_service_demo.model.OrderStatus.PROCESSING);
        
        OrderItem order2Item1 = new OrderItem(savedSmartphone, 1);
        OrderItem order2Item2 = new OrderItem(savedSmartwatch, 1);
        
        order2.addItem(order2Item1);
        order2.addItem(order2Item2);
        purchaseOrderRepository.save(order2);
        
        // Order 3: Recently created order
        PurchaseOrder order3 = new PurchaseOrder("Bob Johnson", "bob.johnson@example.com");
        order3.setOrderDate(LocalDateTime.now().minusHours(3));
        order3.setStatus(com.example.inventory_service_demo.model.OrderStatus.CREATED);
        
        OrderItem order3Item1 = new OrderItem(savedTablet, 2);
        
        order3.addItem(order3Item1);
        purchaseOrderRepository.save(order3);
        
        // Order 4: Cancelled order
        PurchaseOrder order4 = new PurchaseOrder("Alice Brown", "alice.brown@example.com");
        order4.setOrderDate(LocalDateTime.now().minusDays(1));
        order4.setStatus(com.example.inventory_service_demo.model.OrderStatus.CANCELLED);
        
        OrderItem order4Item1 = new OrderItem(savedLaptop, 1);
        OrderItem order4Item2 = new OrderItem(savedSmartphone, 1);
        
        order4.addItem(order4Item1);
        order4.addItem(order4Item2);
        purchaseOrderRepository.save(order4);
    }
}
