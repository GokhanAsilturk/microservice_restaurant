package com.example.orderapi.service;

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

        if (!checkStock(request.getItems())) {
            logger.warn("Insufficient stock, order rejected: {}", request);
            throw new OrderProcessingException("Insufficient stock");
        }

        Order order = request.toEntity();
        order = orderRepository.save(order);
        logger.info("Order saved, ID: {}", order.getId());

        try {
            DeliveryResponse deliveryResponse = startDelivery(order);
            if (deliveryResponse != null && deliveryResponse.isSuccess()) {
                order.setDeliveryId(deliveryResponse.getDeliveryId());
                order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
                orderRepository.save(order);
                logger.info("Order successfully updated, delivery ID: {}", order.getDeliveryId());
            } else {
                order.setStatus(OrderStatus.PENDING);
                orderRepository.save(order);
                logger.warn("Delivery could not be started, order set to PENDING status");
            }
        } catch (ResourceAccessException e) {
            logger.error("Could not connect to delivery service: {}", e.getMessage());
            order.setStatus(OrderStatus.PENDING);
            orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Delivery start error: {}", e.getMessage(), e);
            order.setStatus(OrderStatus.PENDING);
            orderRepository.save(order);
        }

        if (!reduceStock(request.getItems())) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            logger.error("Stock update failed, order cancelled: {}", order.getId());
            throw new OrderProcessingException("Stock update failed");
        }

        logger.info("Order successfully created: {}", order.getId());
        return "Order successfully created";
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
}
