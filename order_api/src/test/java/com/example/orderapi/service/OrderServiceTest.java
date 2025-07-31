package com.example.orderapi.service;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import com.example.orderapi.model.order.OrderItemDto;
import com.example.orderapi.model.request.DeliveryRequest;
import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.model.request.StockRequest;
import com.example.orderapi.model.response.DeliveryResponse;
import com.example.orderapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OrderService için Unit Test Sınıfı
 *
 * Bu test sınıfı OrderService'in tüm önemli metodlarını test eder:
 * - placeOrder() - Sipariş verme işlemi
 * - checkStock() - Stok kontrolü
 * - reduceStock() - Stok azaltma
 * - startDelivery() - Teslimat başlatma
 * - Elasticsearch arama metodları
 *
 * Test stratejisi:
 * 1. Mockito ile bağımlılıkları taklit ediyoruz
 * 2. Hem başarılı hem de hata senaryolarını test ediyoruz
 * 3. External API çağrılarını mock'luyoruz
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private OrderService orderService;

    // Test için örnek veriler
    private OrderRequest orderRequest;
    private Order order;
    private OrderItemDto orderItemDto;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        // Test URL'lerini ayarlıyoruz
        ReflectionTestUtils.setField(orderService, "restaurantApiUrl", "http://restaurant-api");
        ReflectionTestUtils.setField(orderService, "deliveryApiUrl", "http://delivery-api");

        // Test verilerini hazırlıyoruz - price alanını ekliyoruz
        orderItemDto = OrderItemDto.builder()
                .productId(1)
                .quantity(2)
                .price(25.0) // Price alanını ekliyoruz
                .name("Test Ürün")
                .build();

        orderItem = OrderItem.builder()
                .productId(1)
                .quantity(2)
                .price(25.0) // Price alanını ekliyoruz
                .productName("Test Ürün")
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
                .totalAmount(50.0) // 2 * 25.0
                .build();
    }

    /**
     * Test 1: Başarılı sipariş verme
     * - Stok yeterli
     * - Teslimat başarılı
     * - Stok azaltma başarılı
     */
    @Test
    void placeOrder_Success() {
        // Given - Test verilerini hazırlıyoruz
        StockResponse stockResponse = new StockResponse(true, "Stok yeterli");
        DeliveryResponse deliveryResponse = new DeliveryResponse(true, 456, "Teslimat başlatıldı");

        // Mock'ları ayarlıyoruz
        when(restTemplate.postForEntity(eq("http://restaurant-api/stock/check"), any(StockRequest.class), eq(StockResponse.class)))
                .thenReturn(ResponseEntity.ok(stockResponse));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(restTemplate.postForEntity(eq("http://delivery-api/start"), any(DeliveryRequest.class), eq(DeliveryResponse.class)))
                .thenReturn(ResponseEntity.ok(deliveryResponse));

        when(restTemplate.postForEntity(eq("http://restaurant-api/stock/reduce"), any(StockRequest.class), eq(StockResponse.class)))
                .thenReturn(ResponseEntity.ok(stockResponse));

        // When - Test edilen metodu çalıştırıyoruz
        String result = orderService.placeOrder(orderRequest);

        // Then - Sonuçları kontrol ediyoruz
        assertEquals("Sipariş başarıyla oluşturuldu", result);
        verify(orderRepository, times(2)).save(any(Order.class)); // İlk kayıt + teslimat güncelleme
        verify(restTemplate, times(3)).postForEntity(anyString(), any(), any()); // Stok kontrol + teslimat + stok azalt
    }

    /**
     * Test 2: Stok yetersiz durumu
     */
    @Test
    void placeOrder_InsufficientStock() {
        // Given
        StockResponse stockResponse = new StockResponse(false, "Stok yetersiz");

        when(restTemplate.postForEntity(eq("http://restaurant-api/stock/check"), any(StockRequest.class), eq(StockResponse.class)))
                .thenReturn(ResponseEntity.ok(stockResponse));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(orderRequest);
        });

        assertEquals("Stok yetersiz", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * Test 3: Teslimat servisi çalışmıyor durumu
     */
    @Test
    void placeOrder_DeliveryServiceDown() {
        // Given
        StockResponse stockResponse = new StockResponse(true, "Stok yeterli");

        when(restTemplate.postForEntity(eq("http://restaurant-api/stock/check"), any(StockRequest.class), eq(StockResponse.class)))
                .thenReturn(ResponseEntity.ok(stockResponse));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        // Teslimat servisine bağlanılamıyor
        when(restTemplate.postForEntity(eq("http://delivery-api/start"), any(DeliveryRequest.class), eq(DeliveryResponse.class)))
                .thenThrow(new ResourceAccessException("Connection timeout"));

        when(restTemplate.postForEntity(eq("http://restaurant-api/stock/reduce"), any(StockRequest.class), eq(StockResponse.class)))
                .thenReturn(ResponseEntity.ok(stockResponse));

        // When
        String result = orderService.placeOrder(orderRequest);

        // Then
        assertEquals("Sipariş başarıyla oluşturuldu", result);
        verify(orderRepository, times(2)).save(any(Order.class)); // Sipariş + durum güncelleme
    }

    /**
     * Test 4: Stok azaltma başarısız durumu
     */
    @Test
    void placeOrder_StockReductionFailed() {
        // Given
        StockResponse stockCheckResponse = new StockResponse(true, "Stok yeterli");
        StockResponse stockReduceResponse = new StockResponse(false, "Stok azaltılamadı");
        DeliveryResponse deliveryResponse = new DeliveryResponse(true, 456, "Başarılı");

        when(restTemplate.postForEntity(eq("http://restaurant-api/stock/check"), any(StockRequest.class), eq(StockResponse.class)))
                .thenReturn(ResponseEntity.ok(stockCheckResponse));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(restTemplate.postForEntity(eq("http://delivery-api/start"), any(DeliveryRequest.class), eq(DeliveryResponse.class)))
                .thenReturn(ResponseEntity.ok(deliveryResponse));

        when(restTemplate.postForEntity(eq("http://restaurant-api/stock/reduce"), any(StockRequest.class), eq(StockResponse.class)))
                .thenReturn(ResponseEntity.ok(stockReduceResponse));

        // When
        String result = orderService.placeOrder(orderRequest);

        // Then
        assertEquals("Sipariş alındı, ancak stok güncelleme işleminde sorun oluştu.", result);
        verify(orderRepository, times(3)).save(any(Order.class)); // Sipariş + teslimat + stok hatası
    }

    /**
     * Test 5: Tüm siparişleri getirme
     */
    @Test
    void getAllOrders_Success() {
        // Given
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<Order> result = orderService.getAllOrders();

        // Then
        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
        verify(orderRepository).findAll();
    }

    /**
     * Test 6: ID ile sipariş getirme - Bulundu
     */
    @Test
    void getOrderById_Found() {
        // Given
        when(orderRepository.findById("order-123")).thenReturn(Optional.of(order));

        // When
        Optional<Order> result = orderService.getOrderById("order-123");

        // Then
        assertTrue(result.isPresent());
        assertEquals(order.getId(), result.get().getId());
        verify(orderRepository).findById("order-123");
    }

    /**
     * Test 7: ID ile sipariş getirme - Bulunamadı
     */
    @Test
    void getOrderById_NotFound() {
        // Given
        when(orderRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderService.getOrderById("nonexistent");

        // Then
        assertFalse(result.isPresent());
        verify(orderRepository).findById("nonexistent");
    }

    /**
     * Test 8: Elasticsearch - Müşteri ID ile arama
     */
    @Test
    @SuppressWarnings("unchecked")
    void findOrdersByCustomerId_Success() {
        // Given
        SearchHit<Order> searchHit = mock(SearchHit.class);
        SearchHits<Order> searchHits = mock(SearchHits.class);

        when(searchHit.getContent()).thenReturn(order);
        when(searchHits.stream()).thenReturn(List.of(searchHit).stream());
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(Order.class)))
                .thenReturn(searchHits);

        // When
        List<Order> result = orderService.findOrdersByCustomerId(123);

        // Then
        assertEquals(1, result.size());
        assertEquals(order.getCustomerId(), result.get(0).getCustomerId());
        verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(Order.class));
    }

    /**
     * Test 9: Elasticsearch - Durum ile arama
     */
    @Test
    @SuppressWarnings("unchecked")
    void findOrdersByStatus_Success() {
        // Given
        SearchHit<Order> searchHit = mock(SearchHit.class);
        SearchHits<Order> searchHits = mock(SearchHits.class);

        when(searchHit.getContent()).thenReturn(order);
        when(searchHits.stream()).thenReturn(List.of(searchHit).stream());
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(Order.class)))
                .thenReturn(searchHits);

        // When
        List<Order> result = orderService.findOrdersByStatus("PENDING");

        // Then
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(Order.class));
    }

    /**
     * Test 10: Elasticsearch hatası durumu
     */
    @Test
    void findOrdersByCustomerId_ElasticsearchError() {
        // Given
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(Order.class)))
                .thenThrow(new RuntimeException("Elasticsearch connection error"));

        // When
        List<Order> result = orderService.findOrdersByCustomerId(123);

        // Then
        assertTrue(result.isEmpty()); // Hata durumunda boş liste döner
        verify(elasticsearchOperations).search(any(CriteriaQuery.class), eq(Order.class));
    }
}
