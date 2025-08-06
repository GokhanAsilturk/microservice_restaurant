package com.example.orderapi.service;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import com.example.orderapi.model.order.OrderItemDto;
import com.example.orderapi.model.request.DeliveryRequest;
import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.model.request.StockRequest;
import com.example.orderapi.model.response.DeliveryResponse;
import com.example.orderapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Value("${restaurant.api.url}")
    private String restaurantApiUrl;

    @Value("${delivery.api.url}")
    private String deliveryApiUrl;

    public OrderService(RestTemplate restTemplate, OrderRepository orderRepository, ElasticsearchOperations elasticsearchOperations) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }


    public String placeOrder(OrderRequest request) {
        // 1. Stok kontrolü
        if (!checkStock(request.getItems())) {
            throw new RuntimeException("Stok yetersiz");
        }

        // 2. Sipariş kaydetme - Elasticsearch otomatik ID oluşturacak
        Order order = request.toEntity();
        order = orderRepository.save(order);

        // 3. Teslimat başlatma
        try {
            DeliveryResponse deliveryResponse = startDelivery(order);
            if (deliveryResponse != null && deliveryResponse.isSuccess()) {
                // Teslimat başarıyla başladı
                order.setDeliveryId(deliveryResponse.getDeliveryId());
                order.setStatus("DELIVERING");

                orderRepository.save(order);
                System.out.println("Sipariş başarıyla güncellendi, teslimat ID: " + order.getDeliveryId());
            } else {
                // Teslimat servisi yanıt verdi ama başarısız
                order.setStatus("PENDING_DELIVERY");
                orderRepository.save(order);
                System.out.println("Teslimat başlatılamadı, sipariş PENDING_DELIVERY durumuna alındı.");
                // Hata fırlatmıyoruz, kullanıcıya siparişin alındığını bildiriyoruz
            }
        } catch (ResourceAccessException e) {
            // Teslimat servisine bağlanılamadı
            System.err.println("Teslimat servisine bağlanılamadı: " + e.getMessage());
            order.setStatus("PENDING_DELIVERY");
            orderRepository.save(order);
            System.out.println("Teslimat servisine bağlanılamadı, sipariş PENDING_DELIVERY durumuna alındı.");
            // Hata fırlatmıyoruz, kullanıcıya siparişin alındığını bildiriyoruz
        } catch (Exception e) {
            System.err.println("Teslimat başlatma hatası: " + e.getMessage());
            e.printStackTrace();
            order.setStatus("PENDING_DELIVERY");
            orderRepository.save(order);
            System.out.println("Beklenmeyen hata, sipariş PENDING_DELIVERY durumuna alındı.");
            // Hata fırlatmıyoruz, kullanıcıya siparişin alındığını bildiriyoruz
        }

        // 4. Stok azaltma işlemi
        if (!reduceStock(request.getItems())) {
            // Stok azaltılamadı, ama sipariş zaten kaydedildi
            order.setStatus("STOCK_ERROR");
            orderRepository.save(order);
            return "Sipariş alındı, ancak stok güncelleme işleminde sorun oluştu.";
        }

        return "Sipariş başarıyla oluşturuldu";
    }


    /**
     * Stok azaltma işlemi yapar
     */
    private boolean reduceStock(List<OrderItemDto> items) {
        try {
            StockRequest stockRequest = new StockRequest(
                    items.stream()
                            .map(
                                    item -> new StockRequest.StockItemDto(item.getProductId(), item.getQuantity()))
                            .toList()
            );

            ResponseEntity<StockResponse> response = restTemplate.postForEntity(
                    restaurantApiUrl + "/stock/reduce",
                    stockRequest,
                    StockResponse.class
            );

            return Objects.requireNonNull(response.getBody()).isAvailable();
        } catch (Exception e) {
            System.out.println("Stok azaltma hatası: " + e.getMessage());
            return false;
        }
    }

    public List<Order> getAllOrders() {
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    private boolean checkStock(List<OrderItemDto> items) {
        try {
            StockRequest stockRequest = new StockRequest(
                    items.stream()
                            .map(item -> new StockRequest.StockItemDto(item.getProductId(), item.getQuantity()))
                            .toList()
            );


            ResponseEntity<StockResponse> response = restTemplate.postForEntity(
                    restaurantApiUrl + "/stock/check",
                    stockRequest,
                    StockResponse.class
            );

            return Objects.requireNonNull(response.getBody()).isAvailable();
        } catch (Exception e) {
            System.out.println("Stok kontrol hatası: " + e.getMessage());
            return false;
        }
    }


    private DeliveryResponse startDelivery(Order order) {
        try {
            DeliveryRequest deliveryRequest = new DeliveryRequest(
                    order.getId(),
                    order.getCustomerId(),
                    order.getAddress(),
                    order.getItems().stream().map(OrderItem::toDto).toList()
            );

            System.out.println("Teslimat isteği gönderiliyor: " + deliveryRequest);

            ResponseEntity<DeliveryResponse> response = restTemplate.postForEntity(
                    deliveryApiUrl + "/start",
                    deliveryRequest,
                    DeliveryResponse.class
            );

            System.out.println("Teslimat API yanıtı: " + response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                DeliveryResponse deliveryResponse = response.getBody();
                System.out.println("Teslimat yanıtı: " + deliveryResponse);

                if (deliveryResponse.isSuccess()) {
                    return deliveryResponse;
                } else {
                    System.out.println("Teslimat başarısız: " + deliveryResponse.getMessage());
                }
            } else {
                System.out.println("Teslimat API'den geçersiz yanıt alındı");
            }
            return null;
        } catch (Exception e) {
            System.out.println("Teslimat başlatma hatası: " + e.getMessage());
            e.printStackTrace(); // Ayrıntılı hata günlüğü için
            throw e; // Üst metotta yakalanması için hatayı tekrar fırlat
        }
    }

    /**
     * Elasticsearch Özellik 1: Müşteri ID'ye göre sipariş arama
     * CriteriaQuery kullanarak basit arama
     */
    public List<Order> findOrdersByCustomerId(int customerId) {
        try {
            Criteria criteria = new Criteria("customerId").is(customerId);
            CriteriaQuery query = new CriteriaQuery(criteria);

            SearchHits<Order> searchHits = elasticsearchOperations.search(query, Order.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Elasticsearch arama hatası (customerId): " + e.getMessage());
            return List.of(); // Boş liste döndür
        }
    }

    /**
     * Elasticsearch Özellik 2: Sipariş durumuna göre arama
     */
    public List<Order> findOrdersByStatus(String status) {
        try {
            Criteria criteria = new Criteria("status").is(status);
            CriteriaQuery query = new CriteriaQuery(criteria);

            SearchHits<Order> searchHits = elasticsearchOperations.search(query, Order.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Elasticsearch arama hatası (status): " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Elasticsearch Özellik 3: Adres üzerinde metin arama
     */
    public List<Order> searchOrdersByAddress(String address) {
        try {
            Criteria criteria = new Criteria("address").contains(address);
            CriteriaQuery query = new CriteriaQuery(criteria);

            SearchHits<Order> searchHits = elasticsearchOperations.search(query, Order.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Elasticsearch arama hatası (address): " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Elasticsearch Özellik 4: Tarih aralığına göre sipariş arama
     */
    public List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Criteria criteria = new Criteria("orderDate").greaterThanEqual(startDate).lessThanEqual(endDate);
            CriteriaQuery query = new CriteriaQuery(criteria);

            SearchHits<Order> searchHits = elasticsearchOperations.search(query, Order.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Elasticsearch arama hatası (dateRange): " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Elasticsearch Özellik 5: Toplam tutar aralığına göre arama
     */
    public List<Order> findOrdersByTotalAmountRange(Double minAmount, Double maxAmount) {
        try {
            Criteria criteria = new Criteria("totalAmount").greaterThanEqual(minAmount).lessThanEqual(maxAmount);
            CriteriaQuery query = new CriteriaQuery(criteria);

            SearchHits<Order> searchHits = elasticsearchOperations.search(query, Order.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Elasticsearch arama hatası (amountRange): " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Elasticsearch Özellik 6: Karmaşık arama - Müşteri ID ve durum birlikte
     */
    public List<Order> findOrdersByCustomerIdAndStatus(int customerId, String status) {
        try {
            Criteria criteria = new Criteria("customerId").is(customerId)
                    .and(new Criteria("status").is(status));
            CriteriaQuery query = new CriteriaQuery(criteria);

            SearchHits<Order> searchHits = elasticsearchOperations.search(query, Order.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Elasticsearch arama hatası (customerIdAndStatus): " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Elasticsearch Özellik 7: Repository metodları ile basit arama
     * ElasticsearchRepository otomatik olarak bazı metodlar sağlar
     */
    public List<Order> findOrdersWithLimit(int limit) {
        try {
            return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Elasticsearch arama hatası (withLimit): " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Elasticsearch Özellik 8: Sayfalama ile arama
     */
    public List<Order> findOrdersWithPagination(int page, int size) {
        try {
            return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                    .skip((long) page * size)
                    .limit(size)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Elasticsearch arama hatası (pagination): " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Bekleyen teslimatları yeniden deneme (bu metot daha sonra bir zamanlanmış görev olarak çağrılabilir)
     */
    public void retryPendingDeliveries() {
        try {
            List<Order> pendingOrders = findOrdersByStatus("PENDING_DELIVERY");

            for (Order order : pendingOrders) {
                try {
                    DeliveryResponse deliveryResponse = startDelivery(order);
                    if (deliveryResponse != null && deliveryResponse.isSuccess()) {
                        order.setDeliveryId(deliveryResponse.getDeliveryId());
                        order.setStatus("DELIVERING");
                        orderRepository.save(order);
                        System.out.println("Bekleyen teslimat başarıyla başlatıldı, ID: " + order.getId());
                    }
                } catch (Exception e) {
                    System.err.println("Bekleyen teslimat yeniden deneme hatası, sipariş ID: " + order.getId() + ", hata: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Bekleyen teslimatları yeniden deneme hatası: " + e.getMessage());
        }
    }

    /**
     * Müşteri ID'sine göre siparişleri getirir
     * Repository'nin findByCustomerId metodunu kullanır
     *
     * @param customerId Müşteri ID'si
     * @return Müşteriye ait siparişler listesi
     */
    public List<Order> getOrdersByCustomerId(int customerId) {
        try {
            return orderRepository.findByCustomerId(customerId);
        } catch (Exception e) {
            System.err.println("Müşteri siparişleri getirme hatası: " + e.getMessage());
            return List.of(); // Boş liste döndür
        }
    }
}
