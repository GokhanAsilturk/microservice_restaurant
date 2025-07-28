package com.example.orderapi.model.order;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Field(type = FieldType.Integer)
    private int productId;

    @Field(type = FieldType.Text)
    private String productName;

    @Field(type = FieldType.Integer)
    private Integer quantity;

    @Field(type = FieldType.Double)
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
