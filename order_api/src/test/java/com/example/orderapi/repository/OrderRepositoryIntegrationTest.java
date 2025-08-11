package com.example.orderapi.repository;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class OrderRepositoryIntegrationTest {

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        OrderItem orderItem = OrderItem.builder()
                .productId(1)
                .quantity(2)
                .build();

        testOrder = Order.builder()
                .id("order-integration-123")
                .customerId(123)
                .address("Integration Test Adres")
                .items(List.of(orderItem))
                .status("PENDING")
                .totalAmount(100.0)
                .build();
    }

    @Test
    void save_ShouldSaveOrderToElasticsearch_WhenValidOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderRepository.findById("order-integration-123")).thenReturn(Optional.of(testOrder));

        Order savedOrder = orderRepository.save(testOrder);

        assertNotNull(savedOrder);
        assertEquals("order-integration-123", savedOrder.getId());
        assertEquals(123, savedOrder.getCustomerId());
        assertEquals("PENDING", savedOrder.getStatus());

        Optional<Order> foundOrder = orderRepository.findById("order-integration-123");
        assertTrue(foundOrder.isPresent());
        assertEquals("Integration Test Adres", foundOrder.get().getAddress());

        verify(orderRepository).save(testOrder);
        verify(orderRepository).findById("order-integration-123");
    }

    @Test
    void findById_ShouldReturnOrderFromElasticsearch_WhenOrderExists() {
        when(orderRepository.findById("order-integration-123")).thenReturn(Optional.of(testOrder));

        Optional<Order> foundOrder = orderRepository.findById("order-integration-123");

        assertTrue(foundOrder.isPresent());
        assertEquals("order-integration-123", foundOrder.get().getId());
        assertEquals(123, foundOrder.get().getCustomerId());
        assertEquals("Integration Test Adres", foundOrder.get().getAddress());

        verify(orderRepository).findById("order-integration-123");
    }

    @Test
    void findAll_ShouldReturnAllOrdersFromElasticsearch() {
        Order order2 = Order.builder()
                .id("order-integration-456")
                .customerId(456)
                .address("Test Adres 456")
                .status("COMPLETED")
                .totalAmount(200.0)
                .build();

        List<Order> mockOrders = Arrays.asList(testOrder, order2);
        when(orderRepository.findAll()).thenReturn(mockOrders);

        Iterable<Order> orders = orderRepository.findAll();

        assertNotNull(orders);
        List<Order> orderList = (List<Order>) orders;
        assertEquals(2, orderList.size());

        verify(orderRepository).findAll();
    }

    @Test
    void deleteById_ShouldRemoveOrderFromElasticsearch_WhenOrderExists() {
        when(orderRepository.existsById("order-integration-123")).thenReturn(false);

        orderRepository.deleteById("order-integration-123");

        assertFalse(orderRepository.existsById("order-integration-123"));

        verify(orderRepository).deleteById("order-integration-123");
        verify(orderRepository).existsById("order-integration-123");
    }

    @Test
    void count_ShouldReturnCorrectCountFromElasticsearch() {
        when(orderRepository.count()).thenReturn(2L);

        long count = orderRepository.count();

        assertEquals(2, count);

        verify(orderRepository).count();
    }
}
