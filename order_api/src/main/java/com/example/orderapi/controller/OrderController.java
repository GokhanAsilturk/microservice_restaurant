package com.example.orderapi.controller;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.model.response.ApiResponse;
import com.example.orderapi.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> placeOrder(@RequestBody OrderRequest request) {
        logger.info("Yeni sipariş isteği alındı: {}", request);
        String result = orderService.placeOrder(request);
        logger.info("Sipariş başarıyla işlendi");

        ApiResponse<String> response = ApiResponse.success(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        logger.debug("Tüm siparişler istendi");
        List<Order> orders = orderService.getAllOrders();
        logger.info("{} adet sipariş döndürüldü", orders.size());

        ApiResponse<List<Order>> response = ApiResponse.success(orders);
        return ResponseEntity.ok(response);
    }
}
