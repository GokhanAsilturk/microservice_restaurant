package com.example.orderapi.repository;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderRepository için Integration Test Sınıfı
 *
 * Bu test sınıfı Elasticsearch repository işlemlerini test eder:
 * - CRUD operasyonları
 * - Elasticsearch bağlantısı
 * - Veri persistansı
 *
 * @DataElasticsearchTest anotasyonu:
 * - Sadece Elasticsearch katmanını yükler
 * - Test için embedded Elasticsearch kullanır
 * - Repository bean'lerini otomatik konfigüre eder
 */
@DataElasticsearchTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Test öncesi temizlik
        orderRepository.deleteAll();

        // Test verisi hazırlama
        OrderItem orderItem = OrderItem.builder()
                .productId(1)
                .quantity(2)
                .build();

        testOrder = Order.builder()
                .customerId(123)
                .address("Test Adres 123")
                .status("PENDING")
                .items(List.of(orderItem))
                .totalAmount(100.0)
                .deliveryId(0)
                .build();
    }

    /**
     * Test 1: Sipariş kaydetme
     */
    @Test
    void save_Success() {
        // When
        Order savedOrder = orderRepository.save(testOrder);

        // Then
        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getId()); // Elasticsearch otomatik ID atar
        assertEquals(testOrder.getCustomerId(), savedOrder.getCustomerId());
        assertEquals(testOrder.getAddress(), savedOrder.getAddress());
        assertEquals(testOrder.getStatus(), savedOrder.getStatus());
    }

    /**
     * Test 2: ID ile sipariş bulma
     */
    @Test
    void findById_Success() {
        // Given
        Order savedOrder = orderRepository.save(testOrder);

        // When
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertTrue(foundOrder.isPresent());
        assertEquals(savedOrder.getId(), foundOrder.get().getId());
        assertEquals(savedOrder.getCustomerId(), foundOrder.get().getCustomerId());
    }

    /**
     * Test 3: Olmayan ID ile arama
     */
    @Test
    void findById_NotFound() {
        // When
        Optional<Order> foundOrder = orderRepository.findById("nonexistent-id");

        // Then
        assertFalse(foundOrder.isPresent());
    }

    /**
     * Test 4: Tüm siparişleri listeleme
     */
    @Test
    void findAll_Success() {
        // Given
        Order order1 = orderRepository.save(testOrder);

        Order order2 = Order.builder()
                .customerId(456)
                .address("Test Adres 456")
                .status("DELIVERED")
                .items(List.of(OrderItem.builder().productId(2).quantity(1).build()))
                .totalAmount(50.0)
                .deliveryId(0)
                .build();
        orderRepository.save(order2);

        // When
        Iterable<Order> orders = orderRepository.findAll();
        List<Order> orderList = StreamSupport.stream(orders.spliterator(), false)
                .collect(Collectors.toList());

        // Then
        assertEquals(2, orderList.size());
    }

    /**
     * Test 5: Sipariş güncelleme
     */
    @Test
    void update_Success() {
        // Given
        Order savedOrder = orderRepository.save(testOrder);
        String orderId = savedOrder.getId();

        // When
        savedOrder.setStatus("DELIVERED");
        savedOrder.setDeliveryId(999);
        Order updatedOrder = orderRepository.save(savedOrder);

        // Then
        assertEquals(orderId, updatedOrder.getId()); // ID aynı kalmalı
        assertEquals("DELIVERED", updatedOrder.getStatus());
        assertEquals(999, updatedOrder.getDeliveryId());
    }

    /**
     * Test 6: Sipariş silme
     */
    @Test
    void delete_Success() {
        // Given
        Order savedOrder = orderRepository.save(testOrder);
        String orderId = savedOrder.getId();

        // When
        orderRepository.deleteById(orderId);

        // Then
        Optional<Order> deletedOrder = orderRepository.findById(orderId);
        assertFalse(deletedOrder.isPresent());
    }

    /**
     * Test 7: Boş liste durumu
     */
    @Test
    void findAll_EmptyList() {
        // When (setUp'ta deleteAll() çağrılıyor)
        Iterable<Order> orders = orderRepository.findAll();
        List<Order> orderList = StreamSupport.stream(orders.spliterator(), false)
                .collect(Collectors.toList());

        // Then
        assertEquals(0, orderList.size());
    }

    /**
     * Test 8: Count operasyonu
     */
    @Test
    void count_Success() {
        // Given
        orderRepository.save(testOrder);

        Order order2 = Order.builder()
                .customerId(456)
                .address("Test Adres 456")
                .status("PENDING")
                .items(List.of(OrderItem.builder().productId(2).quantity(1).build()))
                .totalAmount(75.0)
                .deliveryId(0)
                .build();
        orderRepository.save(order2);

        // When
        long count = orderRepository.count();

        // Then
        assertEquals(2, count);
    }

    /**
     * Test 9: Exists kontrolü
     */
    @Test
    void existsById_Success() {
        // Given
        Order savedOrder = orderRepository.save(testOrder);

        // When
        boolean exists = orderRepository.existsById(savedOrder.getId());
        boolean notExists = orderRepository.existsById("nonexistent-id");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }
}
