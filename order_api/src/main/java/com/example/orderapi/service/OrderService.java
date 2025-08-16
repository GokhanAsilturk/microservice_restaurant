package com.example.orderapi.service;

import com.example.orderapi.domain.OrderDomain;
import com.example.orderapi.exception.OrderProcessingException;
import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.enums.OrderStatus;
import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.model.order.OrderItemDto;
import com.example.orderapi.model.request.StockRequest;
import com.example.orderapi.model.response.StockResponse;
import com.example.orderapi.model.response.DeliveryResponse;
import com.example.orderapi.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

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
        logger.info("Order creation process started: {}", request);

        OrderDomain orderDomain = request.toDomain();

        if (!orderDomain.isValidForDelivery()) {
            logger.warn("Invalid order data: {}", request);
            throw new OrderProcessingException("Geçersiz sipariş bilgileri");
        }

        if (!checkStock(request.getItems())) {
            logger.warn("Insufficient stock, order rejected: {}", request);
            throw new OrderProcessingException("Insufficient stock");
        }

        orderDomain.confirm();

        Order savedOrder = orderRepository.save(orderDomain.toEntity());
        logger.info("Order saved successfully with ID: {}", savedOrder.getId());

        if (!createDelivery(savedOrder)) {
            logger.error("Delivery creation failed for order: {}", savedOrder.getId());
            throw new OrderProcessingException("Delivery creation failed");
        }

        logger.info("Order process completed successfully: {}", savedOrder.getId());
        return savedOrder.getId();
    }

    public List<Order> getAllOrders() {
        return StreamSupport.stream(orderRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private boolean checkStock(List<OrderItemDto> items) {
        try {
            logger.debug("Stock check started: {}", items);

            List<StockRequest.StockItemDto> stockItems = items.stream()
                    .map(item -> new StockRequest.StockItemDto(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());

            StockRequest stockRequest = new StockRequest(stockItems);

            ResponseEntity<StockResponse> response = restTemplate.postForEntity(
                    restaurantApiUrl + "/stock/check",
                    stockRequest,
                    StockResponse.class
            );

            StockResponse stockResponse = response.getBody();
            boolean available = stockResponse != null && stockResponse.isAvailable();
            logger.debug("Stock check result: {}", available);
            return available;

        } catch (Exception e) {
            logger.error("Stock check error: {}", e.getMessage());
            return false;
        }
    }

    private boolean reduceStock(List<OrderItemDto> items) {
        try {
            logger.debug("Stock reduction started: {}", items);

            List<StockRequest.StockItemDto> stockItems = items.stream()
                    .map(item -> new StockRequest.StockItemDto(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());

            StockRequest stockRequest = new StockRequest(stockItems);

            ResponseEntity<StockResponse> response = restTemplate.postForEntity(
                    restaurantApiUrl + "/stock/reduce",
                    stockRequest,
                    StockResponse.class
            );

            StockResponse stockResponse = response.getBody();
            boolean success = stockResponse != null && stockResponse.isAvailable();
            logger.debug("Stock reduction result: {}", success);
            return success;

        } catch (Exception e) {
            logger.error("Stock reduction error: {}", e.getMessage());
            return false;
        }
    }

    private DeliveryResponse startDelivery(Order order) {
        try {
            logger.debug("Starting delivery for order: {}", order.getId());

            ResponseEntity<DeliveryResponse> response = restTemplate.postForEntity(
                    deliveryApiUrl + "/start",
                    order,
                    DeliveryResponse.class
            );

            DeliveryResponse deliveryResponse = response.getBody();
            logger.debug("Delivery response: {}", deliveryResponse);
            return deliveryResponse;

        } catch (Exception e) {
            logger.error("Delivery start error: {}", e.getMessage());
            throw new OrderProcessingException("Delivery could not be started", e);
        }
    }

    private boolean createDelivery(Order order) {
        try {
            logger.debug("Creating delivery for order: {}", order.getId());

            ResponseEntity<DeliveryResponse> response = restTemplate.postForEntity(
                    deliveryApiUrl + "/create",
                    order,
                    DeliveryResponse.class
            );

            DeliveryResponse deliveryResponse = response.getBody();
            boolean success = deliveryResponse != null && deliveryResponse.isSuccess();
            logger.debug("Delivery creation result: {}", success);
            return success;

        } catch (Exception e) {
            logger.error("Delivery creation error: {}", e.getMessage());
            return false;
        }
    }
}
