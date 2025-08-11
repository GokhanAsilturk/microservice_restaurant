package com.example.orderapi.controller;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import com.example.orderapi.model.order.OrderItemDto;
import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.service.OrderService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequest orderRequest;
    private Order order;
    private OrderItemDto orderItemDto;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItemDto = OrderItemDto.builder()
                .productId(1)
                .quantity(2)
                .build();

        orderItem = OrderItem.builder()
                .productId(1)
                .quantity(2)
                .build();

        orderRequest = OrderRequest.builder()
                .customerId(123)
                .address("Test Adres 123")
                .items(List.of(orderItemDto))
                .build();

        order = Order.builder()
                .id("order-123")
                .customerId(123)
                .address("Test Adres 123")
                .items(List.of(orderItem))
                .status("PENDING")
                .totalAmount(100.0)
                .build();
    }

    @Test
    void placeOrder_Success() throws Exception {
        when(orderService.placeOrder(any(OrderRequest.class)))
                .thenReturn("Sipariş başarıyla oluşturuldu");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sipariş başarıyla oluşturuldu"));
    }

    @Test
    void placeOrder_Error() throws Exception {
        when(orderService.placeOrder(any(OrderRequest.class)))
                .thenThrow(new RuntimeException("Stok yetersiz"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Sipariş işlenemedi: Stok yetersiz"));
    }

    @Test
    void placeOrder_InvalidJson() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOrders_Success() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("order-123")))
                .andExpect(jsonPath("$[0].customerId", is(123)))
                .andExpect(jsonPath("$[0].address", is("Test Adres 123")))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    @Test
    void getAllOrders_EmptyList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getOrderById_Found() throws Exception {
        when(orderService.getOrderById("order-123")).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/order-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("order-123")))
                .andExpect(jsonPath("$.customerId", is(123)))
                .andExpect(jsonPath("$.address", is("Test Adres 123")));
    }

    @Test
    void getOrderById_NotFound() throws Exception {
        when(orderService.getOrderById("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrdersByCustomerId_Success() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(orderService.findOrdersByCustomerId(123)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/customer/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId", is(123)));
    }

    @Test
    void getOrdersByStatus_Success() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(orderService.findOrdersByStatus("PENDING")).thenReturn(orders);

        mockMvc.perform(get("/api/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }

    @Test
    void searchByAddress_Success() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(orderService.searchOrdersByAddress("Test")).thenReturn(orders);

        mockMvc.perform(get("/api/orders/search/address")
                        .param("address", "Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].address", containsString("Test")));
    }

    @Test
    void searchByAddress_MissingParameter() throws Exception {
        mockMvc.perform(get("/api/orders/search/address"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersByDateRange_Success() throws Exception {
        List<Order> orders = Arrays.asList(order);
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        when(orderService.findOrdersByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(orders);

        mockMvc.perform(get("/api/orders/search/date-range")
                        .param("startDate", "2024-01-01T00:00:00")
                        .param("endDate", "2024-12-31T23:59:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getOrdersByDateRange_InvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/orders/search/date-range")
                        .param("startDate", "invalid-date")
                        .param("endDate", "2024-12-31T23:59:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersByAmountRange_Success() throws Exception {
        List<Order> orders = Arrays.asList(order);
        when(orderService.findOrdersByTotalAmountRange(50.0, 150.0)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/search/amount-range")
                        .param("minAmount", "50.0")
                        .param("maxAmount", "150.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].totalAmount", is(100.0)));
    }

    @Test
    void getOrdersByAmountRange_InvalidAmount() throws Exception {
        mockMvc.perform(get("/api/orders/search/amount-range")
                        .param("minAmount", "invalid")
                        .param("maxAmount", "150.0"))
                .andExpect(status().isBadRequest());
    }
}
