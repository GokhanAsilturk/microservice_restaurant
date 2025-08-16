package com.example.orderapi.domain;

import com.example.orderapi.model.enums.OrderStatus;
import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderDomain {
    private Long id;
    private Integer customerId;
    private String address;
    private List<OrderItemDomain> items;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private LocalDateTime confirmedDate;
    private LocalDateTime deliveredDate;

    public boolean canConfirm() {
        return status == OrderStatus.PENDING;
    }

    public boolean canCancel() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public boolean canDeliver() {
        return status == OrderStatus.CONFIRMED;
    }

    public void confirm() {
        if (!canConfirm()) {
            throw new IllegalStateException("Sipariş onay için uygun durumda değil");
        }
        this.status = OrderStatus.CONFIRMED;
        this.confirmedDate = LocalDateTime.now();
    }

    public void cancel() {
        if (!canCancel()) {
            throw new IllegalStateException("Sipariş iptal için uygun durumda değil");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void deliver() {
        if (!canDeliver()) {
            throw new IllegalStateException("Sipariş teslimat için uygun durumda değil");
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredDate = LocalDateTime.now();
    }

    public Double calculateTotalAmount() {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public boolean isValidForDelivery() {
        return items != null && !items.isEmpty() &&
                address != null && !address.trim().isEmpty() &&
                customerId != null && customerId > 0;
    }

    public int getTotalItemCount() {
        return items.stream()
                .mapToInt(OrderItemDomain::getQuantity)
                .sum();
    }

    public Order toEntity() {
        List<OrderItem> entityItems = items.stream()
                .map(domainItem -> OrderItem.builder()
                        .productId(domainItem.getProductId())
                        .productName(domainItem.getProductName())
                        .quantity(domainItem.getQuantity())
                        .price(domainItem.getPrice())
                        .build())
                .collect(Collectors.toList());

        return Order.builder()
                .id(id != null ? id.toString() : null)
                .customerId(customerId)
                .address(address)
                .items(entityItems)
                .totalAmount(totalAmount)
                .status(status)
                .orderDate(orderDate != null ? orderDate.toString() : null)
                .build();
    }

    public static OrderDomain fromEntity(Order entity) {
        List<OrderItemDomain> domainItems = entity.getItems().stream()
                .map(entityItem -> OrderItemDomain.builder()
                        .productId(entityItem.getProductId())
                        .productName(entityItem.getProductName())
                        .quantity(entityItem.getQuantity())
                        .price(entityItem.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDomain.builder()
                .id(entity.getId() != null ? Long.parseLong(entity.getId()) : null)
                .customerId(entity.getCustomerId())
                .address(entity.getAddress())
                .items(domainItems)
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate() != null ? LocalDateTime.parse(entity.getOrderDate()) : null)
                .build();
    }
}
