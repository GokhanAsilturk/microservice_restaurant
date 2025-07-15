package com.example.orderapi.model.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    private Integer quantity;

    private Double price;

    public OrderItemDto toDto() {
        return OrderItemDto.builder()
                .name(productName)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
