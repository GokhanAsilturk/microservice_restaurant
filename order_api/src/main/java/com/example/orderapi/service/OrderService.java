package com.example.orderapi.service;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import com.example.orderapi.model.order.OrderItemDto;
import com.example.orderapi.model.request.DeliveryRequest;
import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.model.request.StockRequest;
import com.example.orderapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    @Value("${restaurant.api.url}")
    private String restaurantApiUrl;

    @Value("${delivery.api.url}")
    private String deliveryApiUrl;

    public OrderService(RestTemplate restTemplate, OrderRepository orderRepository) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
    }

    public String placeOrder(OrderRequest request) {
        // 1. Stok kontrolü
        boolean stockAvailable = checkStock(request.getItems());
        if (!stockAvailable) {
            throw new RuntimeException("Stok yetersiz");
        }

        // 2. Stok azaltma işlemi
        if (!reduceStock(request.getItems())) {
            throw new RuntimeException("Stok azaltma işlemi başarısız oldu");
        }

        // 3. Sipariş kaydetme
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setAddress(request.getAddress());
        order.setItems(convertToOrderItems(request.getItems()));
        order.setStatus("CONFIRMED");
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(calculateTotalAmount(request.getItems()));

        orderRepository.save(order);

        // 4. Teslimat başlatma - Şimdilik bu adımı atlıyoruz veya hatayı yakalamıyoruz
        try {
            startDelivery(order);
            // Hata durumunda bile siparişi iptal etmiyoruz
        } catch (Exception e) {
            // Loglama yapılabilir ama işlemi durdurmuyoruz
            System.out.println("Teslimat servisi hatası (siparişe devam ediliyor): " + e.getMessage());
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
                            .map(item -> new StockRequest.StockItemDto(item.getProductId(), item.getQuantity()))
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
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    private boolean checkStock(List<OrderItemDto> items) {
        try {
            StockRequest stockRequest = new StockRequest(
                    items.stream()
                            .map(item -> new StockRequest.StockItemDto(item.getProductId(), item.getQuantity()))
                            .toList()
            );

            // Güncellenen endpoint çağrısı: /check-stock yerine /stock/check kullanıyoruz
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



    private boolean startDelivery(Order order) {
        try {
            DeliveryRequest deliveryRequest = new DeliveryRequest(
                    order.getId(),
                    order.getCustomerId(),
                    order.getAddress(),
                    order.getItems().stream().map(OrderItem::toDto).toList()
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                    deliveryApiUrl + "/start",
                    deliveryRequest,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    private List<OrderItem> convertToOrderItems(List<OrderItemDto> itemDtos) {
        return itemDtos.stream()
                .map(dto -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(dto.getProductId());
                    item.setProductName(dto.getName());
                    item.setQuantity(dto.getQuantity());
                    item.setPrice(dto.getPrice());
                    return item;
                })
                .toList();
    }

    private Double calculateTotalAmount(List<OrderItemDto> items) {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
