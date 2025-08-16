package com.example.orderapi.model.request;

import com.example.orderapi.domain.OrderDomain;
import com.example.orderapi.domain.OrderItemDomain;
import com.example.orderapi.model.enums.OrderStatus;
import com.example.orderapi.model.order.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private int customerId;
    private String address;
    private List<OrderItemDto> items;

    public OrderDomain toDomain() {
        List<OrderItemDomain> domainItems = items.stream()
                .map(dto -> OrderItemDomain.builder()
                        .productId(dto.getProductId())
                        .productName(dto.getName())
                        .quantity(dto.getQuantity())
                        .price(dto.getPrice())
                        .build())
                .collect(Collectors.toList());

        OrderDomain domain = OrderDomain.builder()
                .customerId(customerId)
                .address(address)
                .items(domainItems)
                .status(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .build();

        domain.setTotalAmount(domain.calculateTotalAmount());
        return domain;
    }
}
