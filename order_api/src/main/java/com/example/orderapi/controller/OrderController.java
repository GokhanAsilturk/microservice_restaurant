package com.example.orderapi.controller;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Sipariş Yönetimi", description = "Sipariş oluşturma ve sorgulama işlemleri")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Yeni sipariş oluştur", description = "Stok kontrolü yaparak yeni sipariş oluşturur")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest request) {
        try {
            String result = orderService.placeOrder(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Sipariş işlenemedi: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Tüm siparişleri getir", description = "Sistemdeki tüm siparişleri listeler")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile sipariş getir", description = "Belirtilen ID'ye sahip siparişi getirir")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ====== ELASTICSEARCH ARAMA ÖZELLİKLERİ ======

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Müşteri ID'ye göre sipariş ara", description = "Elasticsearch: Belirtilen müşteri ID'sine ait siparişleri getirir")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(
            @Parameter(description = "Müşteri ID") @PathVariable int customerId) {
        List<Order> orders = orderService.findOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Duruma göre sipariş ara", description = "Elasticsearch: Belirtilen durumdaki siparişleri getirir")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @Parameter(description = "Sipariş durumu (PENDING, DELIVERING, DELIVERED, CANCELLED)") @PathVariable String status) {
        List<Order> orders = orderService.findOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/search/address")
    @Operation(summary = "Adres ile arama", description = "Elasticsearch: Adres alanında metin arama yapar (fuzzy search)")
    public ResponseEntity<List<Order>> searchByAddress(
            @Parameter(description = "Aranacak adres metni") @RequestParam String address) {
        List<Order> orders = orderService.searchOrdersByAddress(address);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/search/date-range")
    @Operation(summary = "Tarih aralığında arama", description = "Elasticsearch: Belirtilen tarih aralığındaki siparişleri getirir")
    public ResponseEntity<List<Order>> getOrdersByDateRange(
            @Parameter(description = "Başlangıç tarihi (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Bitiş tarihi (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Order> orders = orderService.findOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/search/amount-range")
    @Operation(summary = "Tutar aralığında arama", description = "Elasticsearch: Belirtilen tutar aralığındaki siparişleri getirir")
    public ResponseEntity<List<Order>> getOrdersByAmountRange(
            @Parameter(description = "Minimum tutar") @RequestParam Double minAmount,
            @Parameter(description = "Maksimum tutar") @RequestParam Double maxAmount) {
        List<Order> orders = orderService.findOrdersByTotalAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/search/customer-status")
    @Operation(summary = "Müşteri ve durum kombinasyonu", description = "Elasticsearch: Müşteri ID ve duruma göre gelişmiş arama")
    public ResponseEntity<List<Order>> getOrdersByCustomerAndStatus(
            @Parameter(description = "Müşteri ID") @RequestParam int customerId,
            @Parameter(description = "Sipariş durumu") @RequestParam String status) {
        List<Order> orders = orderService.findOrdersByCustomerIdAndStatus(customerId, status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Sayfalama ile sipariş listesi", description = "Elasticsearch: Sayfalanmış sipariş listesi getirir")
    public ResponseEntity<List<Order>> getOrdersPaginated(
            @Parameter(description = "Sayfa numarası (0'dan başlar)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa boyutu") @RequestParam(defaultValue = "10") int size) {
        List<Order> orders = orderService.findOrdersWithPagination(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/limited")
    @Operation(summary = "Sınırlı sipariş listesi", description = "Elasticsearch: Belirtilen sayıda sipariş getirir")
    public ResponseEntity<List<Order>> getOrdersLimited(
            @Parameter(description = "Maksimum sipariş sayısı") @RequestParam(defaultValue = "5") int limit) {
        List<Order> orders = orderService.findOrdersWithLimit(limit);
        return ResponseEntity.ok(orders);
    }
}
