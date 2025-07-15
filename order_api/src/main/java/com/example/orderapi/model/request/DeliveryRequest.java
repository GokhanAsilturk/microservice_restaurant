package com.example.orderapi.model.request;

import com.example.orderapi.model.order.OrderItemDto;
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

    private Long orderId;
    private int customerId;
    private String address;
    private List<DeliveryItemDto> items;
    private LocalDateTime requestTime;

    public DeliveryRequest(Long orderId, int customerId, String address, List<OrderItemDto> orderItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.address = address;
        this.requestTime = LocalDateTime.now();

        if (orderItems != null) {
            this.items = orderItems.stream()
                    .map(item -> new DeliveryItemDto(
                            item.getProductId().toString(),
                            item.getName(),
                            item.getQuantity()))
                    .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryItemDto {
        private String productId;
        private String name;
        private Integer quantity;
    }
}
