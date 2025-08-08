package com.example.inventory_service_demo.controller;

import com.example.inventory_service_demo.dto.CreatePurchaseOrderDto;
import com.example.inventory_service_demo.dto.OrderItemDto;
import com.example.inventory_service_demo.model.OrderStatus;
import com.example.inventory_service_demo.model.PurchaseOrder;
import com.example.inventory_service_demo.service.PurchaseOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseOrderController.class)
class PurchaseOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private ObjectMapper objectMapper;

    private PurchaseOrder testOrder;
    private CreatePurchaseOrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        testOrder = new PurchaseOrder("John Doe", "john@example.com");
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.CREATED);

        OrderItemDto itemDto = new OrderItemDto(1L, 5);
        testOrderDto = new CreatePurchaseOrderDto("John Doe", "john@example.com", Arrays.asList(itemDto));
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() throws Exception {
        List<PurchaseOrder> orders = Arrays.asList(testOrder);
        when(purchaseOrderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerName").value("John Doe"));

        verify(purchaseOrderService).getAllOrders();
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrder() throws Exception {
        when(purchaseOrderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("John Doe"));

        verify(purchaseOrderService).getOrderById(1L);
    }

    @Test
    void getOrderById_WhenOrderNotFound_ShouldReturn404() throws Exception {
        when(purchaseOrderService.getOrderById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isNotFound());

        verify(purchaseOrderService).getOrderById(1L);
    }

    @Test
    void getOrdersByStatus_ShouldReturnOrdersWithStatus() throws Exception {
        List<PurchaseOrder> orders = Arrays.asList(testOrder);
        when(purchaseOrderService.getOrdersByStatus(OrderStatus.CREATED)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/status/CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("CREATED"));

        verify(purchaseOrderService).getOrdersByStatus(OrderStatus.CREATED);
    }

    @Test
    void getOrdersByCustomerEmail_ShouldReturnOrdersForCustomer() throws Exception {
        List<PurchaseOrder> orders = Arrays.asList(testOrder);
        when(purchaseOrderService.getOrdersByCustomerEmail("john@example.com")).thenReturn(orders);

        mockMvc.perform(get("/api/orders/customer")
                .param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerEmail").value("john@example.com"));

        verify(purchaseOrderService).getOrdersByCustomerEmail("john@example.com");
    }

    @Test
    void createOrder_WhenValidOrder_ShouldCreateSuccessfully() throws Exception {
        when(purchaseOrderService.createOrder(any(CreatePurchaseOrderDto.class))).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("John Doe"));

        verify(purchaseOrderService).createOrder(any(CreatePurchaseOrderDto.class));
    }

    @Test
    void createOrder_WhenInvalidData_ShouldReturn400() throws Exception {
        CreatePurchaseOrderDto invalidDto = new CreatePurchaseOrderDto("", "invalid-email", Arrays.asList());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(purchaseOrderService, never()).createOrder(any());
    }

    @Test
    void createOrder_WhenServiceThrowsException_ShouldReturn400() throws Exception {
        when(purchaseOrderService.createOrder(any(CreatePurchaseOrderDto.class)))
                .thenThrow(new IllegalArgumentException("Product not found"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderDto)))
                .andExpect(status().isBadRequest());

        verify(purchaseOrderService).createOrder(any(CreatePurchaseOrderDto.class));
    }

    @Test
    void processOrder_WhenValidOrder_ShouldProcessSuccessfully() throws Exception {
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(purchaseOrderService.processOrder(1L)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/1/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(purchaseOrderService).processOrder(1L);
    }

    @Test
    void processOrder_WhenServiceThrowsException_ShouldReturn400() throws Exception {
        when(purchaseOrderService.processOrder(1L))
                .thenThrow(new IllegalArgumentException("Order cannot be processed"));

        mockMvc.perform(post("/api/orders/1/process"))
                .andExpect(status().isBadRequest());

        verify(purchaseOrderService).processOrder(1L);
    }

    @Test
    void cancelOrder_WhenValidOrder_ShouldCancelSuccessfully() throws Exception {
        testOrder.setStatus(OrderStatus.CANCELLED);
        when(purchaseOrderService.cancelOrder(1L)).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(purchaseOrderService).cancelOrder(1L);
    }

    @Test
    void cancelOrder_WhenServiceThrowsException_ShouldReturn400() throws Exception {
        when(purchaseOrderService.cancelOrder(1L))
                .thenThrow(new IllegalArgumentException("Completed orders cannot be cancelled"));

        mockMvc.perform(post("/api/orders/1/cancel"))
                .andExpect(status().isBadRequest());

        verify(purchaseOrderService).cancelOrder(1L);
    }

    @Test
    void getOrdersBetweenDates_ShouldReturnOrdersInDateRange() throws Exception {
        List<PurchaseOrder> orders = Arrays.asList(testOrder);
        when(purchaseOrderService.getOrdersBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(orders);

        mockMvc.perform(get("/api/orders/date-range")
                .param("startDate", "2023-01-01T00:00:00")
                .param("endDate", "2023-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(purchaseOrderService).getOrdersBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
