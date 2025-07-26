package com.example.orderapi.model.request;

import com.example.orderapi.model.order.OrderItemDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

    @JsonProperty("order_id")
    private int orderId;

    @JsonProperty("customer_id")
    private int customerId;

    @JsonProperty("address")
    private String address;

    @JsonProperty("items")
    private List<DeliveryItemDto> items;

    @JsonProperty("request_time")
    private String requestTime;

    public DeliveryRequest(int orderId, int customerId, String address, List<OrderItemDto> orderItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.address = address;
        // ISO format olarak gönderelim
        this.requestTime = LocalDateTime.now().toString();

        if (orderItems != null) {
            this.items = orderItems.stream()
                    .map(item -> new DeliveryItemDto(
                            item.getProductId(),
                            item.getName(),
                            item.getQuantity()))
                    .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryItemDto {
        @JsonProperty("product_id")
        private int productId;

        @JsonProperty("name")
        private String name;

        @JsonProperty("quantity")
        private Integer quantity;
    }
}
