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

    @BeforeEach
    void setUp() {
        Product product1 = new Product();
        product1.setName("Test Product 1");
        product1.setDescription("Test Description 1");
        product1.setSku("SKU001");
        product1.setPrice(BigDecimal.valueOf(10.00));
        entityManager.persistAndFlush(product1);

        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setDescription("Test Description 2");
        product2.setSku("SKU002");
        product2.setPrice(BigDecimal.valueOf(20.00));
        entityManager.persistAndFlush(product2);

        order1 = new PurchaseOrder("John Doe", "john@example.com");
        order1.setStatus(OrderStatus.CREATED);
        order1.setOrderDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        OrderItem item1 = new OrderItem(product1, 2);
        order1.addItem(item1);

        order2 = new PurchaseOrder("Jane Smith", "jane@example.com");
        order2.setStatus(OrderStatus.COMPLETED);
        order2.setOrderDate(LocalDateTime.of(2024, 2, 20, 14, 30));
        OrderItem item2 = new OrderItem(product2, 3);
        order2.addItem(item2);

        order3 = new PurchaseOrder("John Doe", "john@example.com");
        order3.setStatus(OrderStatus.PROCESSING);
        order3.setOrderDate(LocalDateTime.of(2024, 3, 10, 9, 15));
        OrderItem item3 = new OrderItem(product1, 1);
        order3.addItem(item3);

        entityManager.persistAndFlush(order1);
        entityManager.persistAndFlush(order2);
        entityManager.persistAndFlush(order3);
    }

    @Test
    void should_FindOrdersByStatus_When_FindByStatusCalled() {
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
    void should_FindOrdersByCustomerEmail_When_FindByCustomerEmailCalled() {
        List<PurchaseOrder> johnOrders = purchaseOrderRepository.findByCustomerEmail("john@example.com");
        List<PurchaseOrder> janeOrders = purchaseOrderRepository.findByCustomerEmail("jane@example.com");

        assertEquals(2, johnOrders.size());
        assertTrue(johnOrders.stream().allMatch(order -> "john@example.com".equals(order.getCustomerEmail())));

        assertEquals(1, janeOrders.size());
        assertEquals("jane@example.com", janeOrders.get(0).getCustomerEmail());
    }

    @Test
    void should_FindOrdersBetweenDates_When_FindByOrderDateBetweenCalled() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 2, 28, 23, 59);

        List<PurchaseOrder> ordersInRange = purchaseOrderRepository.findByOrderDateBetween(startDate, endDate);

        assertEquals(2, ordersInRange.size());
        assertTrue(ordersInRange.stream().anyMatch(order -> order.getId().equals(order1.getId())));
        assertTrue(ordersInRange.stream().anyMatch(order -> order.getId().equals(order2.getId())));
        assertFalse(ordersInRange.stream().anyMatch(order -> order.getId().equals(order3.getId())));
    }

    @Test
    void should_FindOrdersByCustomerNameContaining_When_FindByCustomerNameContainingIgnoreCaseCalled() {
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
    void should_ReturnEmptyList_When_NoOrdersMatchCriteria() {
        List<PurchaseOrder> cancelledOrders = purchaseOrderRepository.findByStatus(OrderStatus.CANCELLED);
        List<PurchaseOrder> nonExistentEmail = purchaseOrderRepository.findByCustomerEmail("nonexistent@example.com");
        
        LocalDateTime futureStart = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime futureEnd = LocalDateTime.of(2025, 12, 31, 23, 59);
        List<PurchaseOrder> futureOrders = purchaseOrderRepository.findByOrderDateBetween(futureStart, futureEnd);

        assertTrue(cancelledOrders.isEmpty());
        assertTrue(nonExistentEmail.isEmpty());
        assertTrue(futureOrders.isEmpty());
    }
}
