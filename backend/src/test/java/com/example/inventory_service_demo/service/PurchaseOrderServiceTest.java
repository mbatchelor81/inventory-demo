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
    private PurchaseOrder testOrder;
    private Inventory testInventory;
    private CreatePurchaseOrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(10.00));

        testOrder = new PurchaseOrder("John Doe", "john@example.com");
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.CREATED);

        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setProduct(testProduct);
        testInventory.setQuantity(100);

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setProductId(1L);
        itemDto.setQuantity(5);

        testOrderDto = new CreatePurchaseOrderDto();
        testOrderDto.setCustomerName("John Doe");
        testOrderDto.setCustomerEmail("john@example.com");
        testOrderDto.setItems(Arrays.asList(itemDto));
    }

    @Test
    void should_ReturnAllOrders_When_GetAllOrdersCalled() {
        List<PurchaseOrder> expectedOrders = Arrays.asList(testOrder);
        when(purchaseOrderRepository.findAll()).thenReturn(expectedOrders);

        List<PurchaseOrder> result = purchaseOrderService.getAllOrders();

        assertEquals(expectedOrders, result);
        verify(purchaseOrderRepository).findAll();
    }

    @Test
    void should_ReturnOrder_When_GetOrderByIdWithValidId() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<PurchaseOrder> result = purchaseOrderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(testOrder, result.get());
        verify(purchaseOrderRepository).findById(1L);
    }

    @Test
    void should_ReturnEmpty_When_GetOrderByIdWithInvalidId() {
        when(purchaseOrderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<PurchaseOrder> result = purchaseOrderService.getOrderById(999L);

        assertFalse(result.isPresent());
        verify(purchaseOrderRepository).findById(999L);
    }

    @Test
    void should_ReturnOrdersByStatus_When_GetOrdersByStatusCalled() {
        List<PurchaseOrder> expectedOrders = Arrays.asList(testOrder);
        when(purchaseOrderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(expectedOrders);

        List<PurchaseOrder> result = purchaseOrderService.getOrdersByStatus(OrderStatus.CREATED);

        assertEquals(expectedOrders, result);
        verify(purchaseOrderRepository).findByStatus(OrderStatus.CREATED);
    }

    @Test
    void should_ReturnOrdersByCustomerEmail_When_GetOrdersByCustomerEmailCalled() {
        List<PurchaseOrder> expectedOrders = Arrays.asList(testOrder);
        when(purchaseOrderRepository.findByCustomerEmail("john@example.com")).thenReturn(expectedOrders);

        List<PurchaseOrder> result = purchaseOrderService.getOrdersByCustomerEmail("john@example.com");

        assertEquals(expectedOrders, result);
        verify(purchaseOrderRepository).findByCustomerEmail("john@example.com");
    }

    @Test
    void should_CreateOrder_When_ValidOrderDtoProvided() {
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
    void should_ThrowException_When_CreateOrderWithNonExistentProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.createOrder(testOrderDto));

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(productRepository).findById(1L);
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void should_ThrowException_When_CreateOrderWithInsufficientInventory() {
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
    void should_ThrowException_When_CreateOrderWithNoInventory() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(inventoryService.getInventoryByProductId(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.createOrder(testOrderDto));

        assertEquals("No inventory found for product: Test Product", exception.getMessage());
        verify(productRepository).findById(1L);
        verify(inventoryService).getInventoryByProductId(1L);
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void should_ProcessOrder_When_OrderInCreatedStatus() {
        testOrder.setStatus(OrderStatus.CREATED);
        OrderItem orderItem = new OrderItem(testProduct, 5);
        testOrder.addItem(orderItem);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

        PurchaseOrder result = purchaseOrderService.processOrder(1L);

        assertEquals(OrderStatus.COMPLETED, result.getStatus());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService).adjustInventory(1L, -5);
        verify(purchaseOrderRepository).save(testOrder);
    }

    @Test
    void should_ThrowException_When_ProcessOrderNotFound() {
        when(purchaseOrderRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.processOrder(999L));

        assertEquals("Order not found with id: 999", exception.getMessage());
        verify(purchaseOrderRepository).findById(999L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
    }

    @Test
    void should_ThrowException_When_ProcessOrderNotInCreatedStatus() {
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.processOrder(1L));

        assertEquals("Order cannot be processed. Current status: COMPLETED", exception.getMessage());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
    }

    @Test
    void should_CancelOrder_When_OrderInCreatedStatus() {
        testOrder.setStatus(OrderStatus.CREATED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

        PurchaseOrder result = purchaseOrderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
        verify(purchaseOrderRepository).save(testOrder);
    }

    @Test
    void should_CancelOrderAndRestoreInventory_When_OrderInProcessingStatus() {
        testOrder.setStatus(OrderStatus.PROCESSING);
        OrderItem orderItem = new OrderItem(testProduct, 5);
        testOrder.addItem(orderItem);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(testOrder);

        PurchaseOrder result = purchaseOrderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService).adjustInventory(1L, 5);
        verify(purchaseOrderRepository).save(testOrder);
    }

    @Test
    void should_ThrowException_When_CancelCompletedOrder() {
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.cancelOrder(1L));

        assertEquals("Completed orders cannot be cancelled", exception.getMessage());
        verify(purchaseOrderRepository).findById(1L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void should_ThrowException_When_CancelOrderNotFound() {
        when(purchaseOrderRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseOrderService.cancelOrder(999L));

        assertEquals("Order not found with id: 999", exception.getMessage());
        verify(purchaseOrderRepository).findById(999L);
        verify(inventoryService, never()).adjustInventory(anyLong(), anyInt());
    }

    @Test
    void should_ReturnOrdersBetweenDates_When_GetOrdersBetweenDatesCalled() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<PurchaseOrder> expectedOrders = Arrays.asList(testOrder);

        when(purchaseOrderRepository.findByOrderDateBetween(startDate, endDate)).thenReturn(expectedOrders);

        List<PurchaseOrder> result = purchaseOrderService.getOrdersBetweenDates(startDate, endDate);

        assertEquals(expectedOrders, result);
        verify(purchaseOrderRepository).findByOrderDateBetween(startDate, endDate);
    }
}
