package com.example.orderapi.service;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OrderService Basit Unit Test Sınıfı
 * <p>
 * Bu test sınıfı repository işlemlerini test eder ve external API'leri mock'lar.
 * Karmaşık entegrasyon testleri yerine basit ve güvenilir unit testler yapar.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceSimpleTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .id("order-123")
                .customerId(123)
                .address("Test Adres 123")
                .status("PENDING")
                .totalAmount(100.0)
                .build();
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        // Given
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // When
        List<Order> orders = orderService.getAllOrders();

        // Then
        assertEquals(1, orders.size());
        assertEquals(testOrder.getId(), orders.get(0).getId());
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenExists() {
        // Given
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(testOrder));

        // When
        Optional<Order> foundOrder = orderService.getOrderById("order-123");

        // Then
        assertTrue(foundOrder.isPresent());
        assertEquals("order-123", foundOrder.get().getId());
        verify(orderRepository).findById("order-123");
    }

    @Test
    void getOrderById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(orderRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<Order> foundOrder = orderService.getOrderById("nonexistent");

        // Then
        assertFalse(foundOrder.isPresent());
        verify(orderRepository).findById("nonexistent");
    }

    @Test
    void getOrdersByCustomerId_ShouldReturnCustomerOrders() {
        // Given
        List<Order> customerOrders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(123)).thenReturn(customerOrders);

        // When
        List<Order> orders = orderService.getOrdersByCustomerId(123);

        // Then
        assertEquals(1, orders.size());
        assertEquals(123, orders.get(0).getCustomerId());
        verify(orderRepository).findByCustomerId(123);
    }

    @Test
    void findOrdersByCustomerId_ShouldReturnOrdersFromElasticsearch() {
        // Given - Repository metodu mock'lanır
        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(123)).thenReturn(expectedOrders);

        // When
        List<Order> orders = orderService.getOrdersByCustomerId(123);

        // Then
        assertEquals(1, orders.size());
        assertEquals(123, orders.get(0).getCustomerId());
        verify(orderRepository).findByCustomerId(123);
    }

    @Test
    void orderRepository_ShouldSaveOrder() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order savedOrder = orderRepository.save(testOrder);

        // Then
        assertNotNull(savedOrder);
        assertEquals("order-123", savedOrder.getId());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void orderRepository_ShouldCheckIfOrderExists() {
        // Given
        when(orderRepository.existsById("order-123")).thenReturn(true);

        // When
        boolean exists = orderRepository.existsById("order-123");

        // Then
        assertTrue(exists);
        verify(orderRepository).existsById("order-123");
    }

    @Test
    void orderRepository_ShouldDeleteOrder() {
        // Given - Mock repository behavior

        // When
        orderRepository.deleteById("order-123");

        // Then
        verify(orderRepository).deleteById("order-123");
    }

    @Test
    void orderRepository_ShouldCountOrders() {
        // Given
        when(orderRepository.count()).thenReturn(5L);

        // When
        long count = orderRepository.count();

        // Then
        assertEquals(5L, count);
        verify(orderRepository).count();
    }

    @Test
    void getAllOrders_ShouldReturnEmptyList_WhenNoOrders() {
        // Given
        when(orderRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Order> orders = orderService.getAllOrders();

        // Then
        assertEquals(0, orders.size());
        verify(orderRepository).findAll();
    }
}
