package com.example.inventory_service_demo.repository;

import com.example.inventory_service_demo.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PurchaseOrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    private PurchaseOrder order1;
    private PurchaseOrder order2;
    private PurchaseOrder order3;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(10.00));
        testProduct.setSku("TEST-001");
        testProduct.setDescription("Test product description");
        entityManager.persistAndFlush(testProduct);

        order1 = new PurchaseOrder("John Doe", "john@example.com");
        order1.setStatus(OrderStatus.CREATED);
        order1.setOrderDate(LocalDateTime.now().minusDays(5));
        OrderItem item1 = new OrderItem(testProduct, 2);
        order1.addItem(item1);

        order2 = new PurchaseOrder("Jane Smith", "jane@example.com");
        order2.setStatus(OrderStatus.COMPLETED);
        order2.setOrderDate(LocalDateTime.now().minusDays(3));
        OrderItem item2 = new OrderItem(testProduct, 1);
        order2.addItem(item2);

        order3 = new PurchaseOrder("John Doe", "john@example.com");
        order3.setStatus(OrderStatus.PROCESSING);
        order3.setOrderDate(LocalDateTime.now().minusDays(1));
        OrderItem item3 = new OrderItem(testProduct, 3);
        order3.addItem(item3);

        entityManager.persistAndFlush(order1);
        entityManager.persistAndFlush(order2);
        entityManager.persistAndFlush(order3);
    }

    @Test
    void findByStatus_ShouldReturnOrdersWithSpecifiedStatus() {
        List<PurchaseOrder> createdOrders = purchaseOrderRepository.findByStatus(OrderStatus.CREATED);
        List<PurchaseOrder> completedOrders = purchaseOrderRepository.findByStatus(OrderStatus.COMPLETED);
        List<PurchaseOrder> processingOrders = purchaseOrderRepository.findByStatus(OrderStatus.PROCESSING);

        assertEquals(1, createdOrders.size());
        assertEquals(order1.getId(), createdOrders.get(0).getId());

        assertEquals(1, completedOrders.size());
        assertEquals(order2.getId(), completedOrders.get(0).getId());

        assertEquals(1, processingOrders.size());
        assertEquals(order3.getId(), processingOrders.get(0).getId());
    }

    @Test
    void findByCustomerEmail_ShouldReturnOrdersForSpecifiedEmail() {
        List<PurchaseOrder> johnOrders = purchaseOrderRepository.findByCustomerEmail("john@example.com");
        List<PurchaseOrder> janeOrders = purchaseOrderRepository.findByCustomerEmail("jane@example.com");

        assertEquals(2, johnOrders.size());
        assertTrue(johnOrders.stream().allMatch(order -> "john@example.com".equals(order.getCustomerEmail())));

        assertEquals(1, janeOrders.size());
        assertEquals("jane@example.com", janeOrders.get(0).getCustomerEmail());
    }

    @Test
    void findByOrderDateBetween_ShouldReturnOrdersInDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(4);
        LocalDateTime endDate = LocalDateTime.now();

        List<PurchaseOrder> ordersInRange = purchaseOrderRepository.findByOrderDateBetween(startDate, endDate);

        assertEquals(2, ordersInRange.size());
        assertTrue(ordersInRange.stream().anyMatch(order -> order.getId().equals(order2.getId())));
        assertTrue(ordersInRange.stream().anyMatch(order -> order.getId().equals(order3.getId())));
    }

    @Test
    void findByCustomerNameContainingIgnoreCase_ShouldReturnMatchingOrders() {
        List<PurchaseOrder> johnOrders = purchaseOrderRepository.findByCustomerNameContainingIgnoreCase("john");
        List<PurchaseOrder> doeOrders = purchaseOrderRepository.findByCustomerNameContainingIgnoreCase("DOE");
        List<PurchaseOrder> smithOrders = purchaseOrderRepository.findByCustomerNameContainingIgnoreCase("smith");

        assertEquals(2, johnOrders.size());
        assertTrue(johnOrders.stream().allMatch(order -> order.getCustomerName().toLowerCase().contains("john")));

        assertEquals(2, doeOrders.size());
        assertTrue(doeOrders.stream().allMatch(order -> order.getCustomerName().toLowerCase().contains("doe")));

        assertEquals(1, smithOrders.size());
        assertTrue(smithOrders.get(0).getCustomerName().toLowerCase().contains("smith"));
    }

    @Test
    void findByStatus_WhenNoOrdersWithStatus_ShouldReturnEmptyList() {
        List<PurchaseOrder> cancelledOrders = purchaseOrderRepository.findByStatus(OrderStatus.CANCELLED);

        assertTrue(cancelledOrders.isEmpty());
    }

    @Test
    void findByCustomerEmail_WhenNoOrdersForEmail_ShouldReturnEmptyList() {
        List<PurchaseOrder> orders = purchaseOrderRepository.findByCustomerEmail("nonexistent@example.com");

        assertTrue(orders.isEmpty());
    }

    @Test
    void findByOrderDateBetween_WhenNoOrdersInRange_ShouldReturnEmptyList() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now().minusDays(8);

        List<PurchaseOrder> ordersInRange = purchaseOrderRepository.findByOrderDateBetween(startDate, endDate);

        assertTrue(ordersInRange.isEmpty());
    }
}
