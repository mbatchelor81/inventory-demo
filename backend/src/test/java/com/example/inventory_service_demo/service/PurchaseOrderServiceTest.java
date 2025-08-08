package com.example.inventory_service_demo.service;

import com.example.inventory_service_demo.dto.CreatePurchaseOrderDto;
import com.example.inventory_service_demo.dto.OrderItemDto;
import com.example.inventory_service_demo.model.*;
import com.example.inventory_service_demo.repository.OrderItemRepository;
import com.example.inventory_service_demo.repository.ProductRepository;
import com.example.inventory_service_demo.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private Product testProduct;
    private Inventory testInventory;
    private PurchaseOrder testOrder;
    private CreatePurchaseOrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(10.00));
        testProduct.setSku("TEST-001");

        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setProduct(testProduct);
        testInventory.setQuantity(100);

        testOrder = new PurchaseOrder("John Doe", "john@example.com");
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.CREATED);

        OrderItemDto itemDto = new OrderItemDto(1L, 5);
        testOrderDto = new CreatePurchaseOrderDto("John Doe", "john@example.com", Arrays.asList(itemDto));
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        List<PurchaseOrder> expectedOrders = Arrays.asList(testOrder);
        when(purchaseOrderRepository.findAll()).thenReturn(expectedOrders);

        List<PurchaseOrder> result = purchaseOrderService.getAllOrders();

        assertEquals(expectedOrders, result);
        verify(purchaseOrderRepository).findAll();
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<PurchaseOrder> result = purchaseOrderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(testOrder, result.get());
        verify(purchaseOrderRepository).findById(1L);
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldReturnEmpty() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<PurchaseOrder> result = purchaseOrderService.getOrderById(1L);

        assertFalse(result.isPresent());
        verify(purchaseOrderRepository).findById(1L);
    }

    @Test
    void getOrdersByStatus_ShouldReturnOrdersWithSpecifiedStatus() {
        List<PurchaseOrder> expectedOrders = Arrays.asList(testOrder);
        when(purchaseOrderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(expectedOrders);

        List<PurchaseOrder> result = purchaseOrderService.getOrdersByStatus(OrderStatus.CREATED);

        assertEquals(expectedOrders, result);
        verify(purchaseOrderRepository).findByStatus(OrderStatus.CREATED);
    }

    @Test
    void getOrdersByCustomerEmail_ShouldReturnOrdersForCustomer() {
        List<PurchaseOrder> expectedOrders = Arrays.asList(testOrder);
        when(purchaseOrderRepository.findByCustomerEmail("john@example.com")).thenReturn(expectedOrders);

        List<PurchaseOrder> result = purchaseOrderService.getOrdersByCustomerEmail("john@example.com");

        assertEquals(expectedOrders, result);
        verify(purchaseOrderRepository).findByCustomerEmail("john@example.com");
    }

    @Test
    void createOrder_WhenValidOrder_ShouldCreateSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryService.getInventoryByProductId(1L)).thenReturn(Optional.of(testInventory));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

        PurchaseOrder result = purchaseOrderService.createOrder(testOrderDto);

        assertNotNull(result);
        verify(productRepository).findById(1L);
        verify(inventoryService).getInventoryByProductId(1L);
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
    }

    @Test
    void createOrder_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.createOrder(testOrderDto));

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository).findById(1L);
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void createOrder_WhenInsufficientInventory_ShouldThrowException() {
        testInventory.setQuantity(2);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryService.getInventoryByProductId(1L)).thenReturn(Optional.of(testInventory));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.createOrder(testOrderDto));

        assertTrue(exception.getMessage().contains("Insufficient inventory"));
        verify(productRepository).findById(1L);
        verify(inventoryService).getInventoryByProductId(1L);
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void createOrder_WhenNoInventoryFound_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryService.getInventoryByProductId(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.createOrder(testOrderDto));

        assertTrue(exception.getMessage().contains("No inventory found"));
        verify(productRepository).findById(1L);
        verify(inventoryService).getInventoryByProductId(1L);
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void processOrder_WhenValidOrder_ShouldProcessSuccessfully() {
        OrderItem orderItem = new OrderItem(testProduct, 5);
        testOrder.addItem(orderItem);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

        PurchaseOrder result = purchaseOrderService.processOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.COMPLETED, result.getStatus());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService).adjustInventory(1L, -5);
        verify(purchaseOrderRepository).save(testOrder);
    }

    @Test
    void processOrder_WhenOrderNotFound_ShouldThrowException() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.processOrder(1L));

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
    }

    @Test
    void processOrder_WhenOrderAlreadyProcessed_ShouldThrowException() {
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.processOrder(1L));

        assertTrue(exception.getMessage().contains("Order cannot be processed"));
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
    }

    @Test
    void cancelOrder_WhenCreatedOrder_ShouldCancelSuccessfully() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

        PurchaseOrder result = purchaseOrderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(purchaseOrderRepository).findById(1L);
        verify(purchaseOrderRepository).save(testOrder);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
    }

    @Test
    void cancelOrder_WhenProcessingOrder_ShouldRestoreInventory() {
        testOrder.setStatus(OrderStatus.PROCESSING);
        OrderItem orderItem = new OrderItem(testProduct, 5);
        testOrder.addItem(orderItem);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

        PurchaseOrder result = purchaseOrderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService).adjustInventory(1L, 5);
        verify(purchaseOrderRepository).save(testOrder);
    }

    @Test
    void cancelOrder_WhenCompletedOrder_ShouldThrowException() {
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.cancelOrder(1L));

        assertEquals("Completed orders cannot be cancelled", exception.getMessage());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
    }

    @Test
    void cancelOrder_WhenOrderNotFound_ShouldThrowException() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.cancelOrder(1L));

        assertEquals("Order not found with id: 1", exception.getMessage());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
    }

    @Test
    void getOrdersBetweenDates_ShouldReturnOrdersInDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<PurchaseOrder> expectedOrders = Arrays.asList(testOrder);
        when(purchaseOrderRepository.findByOrderDateBetween(startDate, endDate)).thenReturn(expectedOrders);

        List<PurchaseOrder> result = purchaseOrderService.getOrdersBetweenDates(startDate, endDate);

        assertEquals(expectedOrders, result);
        verify(purchaseOrderRepository).findByOrderDateBetween(startDate, endDate);
    }
}
