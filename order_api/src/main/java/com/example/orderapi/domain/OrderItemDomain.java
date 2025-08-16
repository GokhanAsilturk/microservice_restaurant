package com.example.orderapi.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDomain {
    private Long id;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Double price;

    public Double getSubTotal() {
        return price * quantity;
    }

    public boolean isValid() {
        return productId != null && productId > 0 &&
                productName != null && !productName.trim().isEmpty() &&
                quantity != null && quantity > 0 &&
                price != null && price > 0;
    }
}
