package com.example.orderapi.model.order;

import com.example.orderapi.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private int customerId;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Keyword)
    private OrderStatus status;

    @Field(type = FieldType.Nested)
    private List<OrderItem> items;

    @Field(type = FieldType.Double)
    private Double totalAmount;

    @Field(type = FieldType.Integer)
    private int deliveryId;

    @Field(type = FieldType.Keyword)
    private String orderDate;
}
