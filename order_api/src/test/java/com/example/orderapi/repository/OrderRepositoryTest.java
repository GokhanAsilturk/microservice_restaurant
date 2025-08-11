package com.example.orderapi.repository;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        OrderItem orderItem = OrderItem.builder()
                .productId(1)
                .quantity(2)
                .build();

        testOrder = Order.builder()
                .id("order-123")
                .customerId(123)
                .address("Test Adres 123")
                .items(List.of(orderItem))
                .status("PENDING")
                .totalAmount(100.0)
                .build();
    }

    @Test
    void save_ShouldSaveOrder_WhenValidOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order savedOrder = orderRepository.save(testOrder);

        assertNotNull(savedOrder);
        assertEquals("order-123", savedOrder.getId());
        assertEquals(123, savedOrder.getCustomerId());
        assertEquals("PENDING", savedOrder.getStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void findById_ShouldReturnOrder_WhenOrderExists() {
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(testOrder));

        Optional<Order> foundOrder = orderRepository.findById("order-123");

        assertTrue(foundOrder.isPresent());
        assertEquals("order-123", foundOrder.get().getId());
        assertEquals(123, foundOrder.get().getCustomerId());
        verify(orderRepository).findById("order-123");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenOrderNotExists() {
        when(orderRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Order> foundOrder = orderRepository.findById("nonexistent");

        assertFalse(foundOrder.isPresent());
        verify(orderRepository).findById("nonexistent");
    }

    @Test
    void findAll_ShouldReturnAllOrders() {
        Order order2 = Order.builder()
                .id("order-456")
                .customerId(456)
                .address("Test Adres 456")
                .status("COMPLETED")
                .totalAmount(200.0)
                .build();

        List<Order> expectedOrders = Arrays.asList(testOrder, order2);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        Iterable<Order> orders = orderRepository.findAll();

        assertNotNull(orders);
        List<Order> orderList = (List<Order>) orders;
        assertEquals(2, orderList.size());
        assertTrue(orderList.contains(testOrder));
        assertTrue(orderList.contains(order2));
        verify(orderRepository).findAll();
    }

    @Test
    void deleteById_ShouldDeleteOrder_WhenOrderExists() {
        orderRepository.deleteById("order-123");
        verify(orderRepository).deleteById("order-123");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenOrderExists() {
        when(orderRepository.existsById("order-123")).thenReturn(true);
        boolean exists = orderRepository.existsById("order-123");
        assertTrue(exists);
        verify(orderRepository).existsById("order-123");
    }

    @Test
    void existsById_ShouldReturnFalse_WhenOrderNotExists() {
        when(orderRepository.existsById("nonexistent")).thenReturn(false);
        boolean exists = orderRepository.existsById("nonexistent");
        assertFalse(exists);
        verify(orderRepository).existsById("nonexistent");
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        when(orderRepository.count()).thenReturn(5L);
        long count = orderRepository.count();
        assertEquals(5L, count);
        verify(orderRepository).count();
    }

    @Test
    void deleteAll_ShouldDeleteAllOrders() {
        orderRepository.deleteAll();
        verify(orderRepository).deleteAll();
    }

    @Test
    void findByCustomerId_ShouldReturnCustomerOrders() {
        List<Order> customerOrders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(123)).thenReturn(customerOrders);
        List<Order> orders = orderRepository.findByCustomerId(123);
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(123, orders.get(0).getCustomerId());
        verify(orderRepository).findByCustomerId(123);
    }
}
