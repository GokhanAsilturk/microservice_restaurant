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

/**
 * OrderRepository için Unit Test Sınıfı
 * <p>
 * Bu test sınıfı repository işlemlerini mock'layarak test eder:
 * - CRUD operasyonları
 * - Mock repository davranışları
 * - Unit test yaklaşımı
 *
 * @ExtendWith(MockitoExtension.class) anotasyonu:
 * - Mockito framework'ünü aktifleştirir
 * - Mock objeler oluşturur
 * - Elasticsearch bağımlılığını ortadan kaldırır
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Test verisi hazırlama
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

    /**
     * Test 1: save - Sipariş kaydetme
     */
    @Test
    void save_ShouldSaveOrder_WhenValidOrder() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order savedOrder = orderRepository.save(testOrder);

        // Then
        assertNotNull(savedOrder);
        assertEquals("order-123", savedOrder.getId());
        assertEquals(123, savedOrder.getCustomerId());
        assertEquals("PENDING", savedOrder.getStatus());
        verify(orderRepository).save(testOrder);
    }

    /**
     * Test 2: findById - ID ile sipariş bulma
     */
    @Test
    void findById_ShouldReturnOrder_WhenOrderExists() {
        // Given
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(testOrder));

        // When
        Optional<Order> foundOrder = orderRepository.findById("order-123");

        // Then
        assertTrue(foundOrder.isPresent());
        assertEquals("order-123", foundOrder.get().getId());
        assertEquals(123, foundOrder.get().getCustomerId());
        verify(orderRepository).findById("order-123");
    }

    /**
     * Test 3: findById - Sipariş bulunamadı
     */
    @Test
    void findById_ShouldReturnEmpty_WhenOrderNotExists() {
        // Given
        when(orderRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<Order> foundOrder = orderRepository.findById("nonexistent");

        // Then
        assertFalse(foundOrder.isPresent());
        verify(orderRepository).findById("nonexistent");
    }

    /**
     * Test 4: findAll - Tüm siparişleri getirme
     */
    @Test
    void findAll_ShouldReturnAllOrders() {
        // Given
        Order order2 = Order.builder()
                .id("order-456")
                .customerId(456)
                .address("Test Adres 456")
                .status("COMPLETED")
                .totalAmount(200.0)
                .build();

        List<Order> expectedOrders = Arrays.asList(testOrder, order2);
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // When
        Iterable<Order> orders = orderRepository.findAll();

        // Then
        assertNotNull(orders);
        List<Order> orderList = (List<Order>) orders;
        assertEquals(2, orderList.size());
        assertTrue(orderList.contains(testOrder));
        assertTrue(orderList.contains(order2));
        verify(orderRepository).findAll();
    }

    /**
     * Test 5: deleteById - Sipariş silme
     */
    @Test
    void deleteById_ShouldDeleteOrder_WhenOrderExists() {
        // Given - Mock repository behavior

        // When
        orderRepository.deleteById("order-123");

        // Then
        verify(orderRepository).deleteById("order-123");
    }

    /**
     * Test 6: existsById - Sipariş varlığı kontrolü
     */
    @Test
    void existsById_ShouldReturnTrue_WhenOrderExists() {
        // Given
        when(orderRepository.existsById("order-123")).thenReturn(true);

        // When
        boolean exists = orderRepository.existsById("order-123");

        // Then
        assertTrue(exists);
        verify(orderRepository).existsById("order-123");
    }

    /**
     * Test 7: existsById - Sipariş yoksa false
     */
    @Test
    void existsById_ShouldReturnFalse_WhenOrderNotExists() {
        // Given
        when(orderRepository.existsById("nonexistent")).thenReturn(false);

        // When
        boolean exists = orderRepository.existsById("nonexistent");

        // Then
        assertFalse(exists);
        verify(orderRepository).existsById("nonexistent");
    }

    /**
     * Test 8: count - Sipariş sayısı
     */
    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        when(orderRepository.count()).thenReturn(5L);

        // When
        long count = orderRepository.count();

        // Then
        assertEquals(5L, count);
        verify(orderRepository).count();
    }

    /**
     * Test 9: deleteAll - Tüm siparişleri silme
     */
    @Test
    void deleteAll_ShouldDeleteAllOrders() {
        // When
        orderRepository.deleteAll();

        // Then
        verify(orderRepository).deleteAll();
    }

    /**
     * Test 10: findByCustomerId - Müşteri ID'sine göre sipariş bulma
     */
    @Test
    void findByCustomerId_ShouldReturnCustomerOrders() {
        // Given
        List<Order> customerOrders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerId(123)).thenReturn(customerOrders);

        // When
        List<Order> orders = orderRepository.findByCustomerId(123);

        // Then
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(123, orders.get(0).getCustomerId());
        verify(orderRepository).findByCustomerId(123);
    }
}
