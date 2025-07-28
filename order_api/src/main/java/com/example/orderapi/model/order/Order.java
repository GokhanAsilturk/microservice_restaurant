package com.example.orderapi.model.order;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private int id;

    @Field(type = FieldType.Integer)
    private int customerId;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Date)
    private LocalDateTime orderDate;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Nested)
    private List<OrderItem> items;

    @Field(type = FieldType.Double)
    private Double totalAmount;

    @Field(type = FieldType.Integer)
    private int deliveryId;
}
