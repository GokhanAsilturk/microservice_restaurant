package com.example.orderapi.service;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import com.example.orderapi.model.order.OrderItemDto;
import com.example.orderapi.model.request.DeliveryRequest;
import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.model.request.StockRequest;
import com.example.orderapi.model.response.DeliveryResponse;
import com.example.orderapi.model.response.StockResponse;
import com.example.orderapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        if (!checkStock(request.getItems())) {
            throw new RuntimeException("Stok yetersiz");
        }

        Order order = request.toEntity();
        order = orderRepository.save(order);

        try {
            DeliveryResponse deliveryResponse = startDelivery(order);
            if (deliveryResponse != null && deliveryResponse.isSuccess()) {
                order.setDeliveryId(deliveryResponse.getDeliveryId());
                order.setStatus("DELIVERING");

                orderRepository.save(order);
                System.out.println("Sipariş başarıyla güncellendi, teslimat ID: " + order.getDeliveryId());
            } else {
                order.setStatus("PENDING_DELIVERY");
                orderRepository.save(order);
                System.out.println("Teslimat başlatılamadı, sipariş PENDING_DELIVERY durumuna alındı.");
            }
        } catch (ResourceAccessException e) {
            System.err.println("Teslimat servisine bağlanılamadı: " + e.getMessage());
            order.setStatus("PENDING_DELIVERY");
            orderRepository.save(order);
            System.out.println("Teslimat servisine bağlanılamadı, sipariş PENDING_DELIVERY durumuna alındı.");
        } catch (Exception e) {
            System.err.println("Teslimat başlatma hatası: " + e.getMessage());
            e.printStackTrace();
            order.setStatus("PENDING_DELIVERY");
            orderRepository.save(order);
            System.out.println("Beklenmeyen hata, sipariş PENDING_DELIVERY durumuna alındı.");
        }

        if (!reduceStock(request.getItems())) {
            order.setStatus("STOCK_ERROR");
            orderRepository.save(order);
            return "Sipariş alındı, ancak stok güncelleme işleminde sorun oluştu.";
        }

        return "Sipariş başarıyla oluşturuldu";
    }


    public List<Order> getAllOrders() {
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

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
            e.printStackTrace();
            throw e; // Üst metotta yakalanması için
        }
    }
}
