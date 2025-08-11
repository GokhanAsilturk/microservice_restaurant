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

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("customerId")
    private int customerId;

    @JsonProperty("address")
    private String address;

    @JsonProperty("items")
    private List<DeliveryItemDto> items;

    @JsonProperty("requestTime")
    private String requestTime;

    public DeliveryRequest(String orderId, int customerId, String address, List<OrderItemDto> orderItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.address = address;
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
        @JsonProperty("productId")
        private int productId;

        @JsonProperty("productName")
        private String name;

        @JsonProperty("quantity")
        private Integer quantity;
    }
}
