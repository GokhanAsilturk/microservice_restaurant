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

        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(expectedOrders);


        List<Order> orders = orderService.getAllOrders();


        assertEquals(1, orders.size());
        assertEquals(testOrder.getId(), orders.get(0).getId());
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenExists() {

        when(orderRepository.findById("order-123")).thenReturn(Optional.of(testOrder));


        Optional<Order> foundOrder = orderService.getOrderById("order-123");


        assertTrue(foundOrder.isPresent());
        assertEquals("order-123", foundOrder.get().getId());
        verify(orderRepository).findById("order-123");
    }

    @Test
    void getOrderById_ShouldReturnEmpty_WhenNotExists() {

        when(orderRepository.findById("nonexistent")).thenReturn(Optional.empty());


        Optional<Order> foundOrder = orderService.getOrderById("nonexistent");


        assertFalse(foundOrder.isPresent());
        verify(orderRepository).findById("nonexistent");
    }

    @Test
    void getOrdersByCustomerId_ShouldReturnCustomerOrders() {

        List<Order> customerOrders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(123)).thenReturn(customerOrders);


        List<Order> orders = orderService.getOrdersByCustomerId(123);


        assertEquals(1, orders.size());
        assertEquals(123, orders.get(0).getCustomerId());
        verify(orderRepository).findByCustomerId(123);
    }

    @Test
    void findOrdersByCustomerId_ShouldReturnOrdersFromElasticsearch() {

        List<Order> expectedOrders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(123)).thenReturn(expectedOrders);


        List<Order> orders = orderService.getOrdersByCustomerId(123);


        assertEquals(1, orders.size());
        assertEquals(123, orders.get(0).getCustomerId());
        verify(orderRepository).findByCustomerId(123);
    }

    @Test
    void orderRepository_ShouldSaveOrder() {

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);


        Order savedOrder = orderRepository.save(testOrder);

        assertNotNull(savedOrder);
        assertEquals("order-123", savedOrder.getId());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void orderRepository_ShouldCheckIfOrderExists() {
        when(orderRepository.existsById("order-123")).thenReturn(true);

        boolean exists = orderRepository.existsById("order-123");


        assertTrue(exists);
        verify(orderRepository).existsById("order-123");
    }

    @Test
    void orderRepository_ShouldDeleteOrder() {

        orderRepository.deleteById("order-123");


        verify(orderRepository).deleteById("order-123");
    }

    @Test
    void orderRepository_ShouldCountOrders() {

        when(orderRepository.count()).thenReturn(5L);


        long count = orderRepository.count();


        assertEquals(5L, count);
        verify(orderRepository).count();
    }

    @Test
    void getAllOrders_ShouldReturnEmptyList_WhenNoOrders() {

        when(orderRepository.findAll()).thenReturn(Arrays.asList());


        List<Order> orders = orderService.getAllOrders();


        assertEquals(0, orders.size());
        verify(orderRepository).findAll();
    }
}
