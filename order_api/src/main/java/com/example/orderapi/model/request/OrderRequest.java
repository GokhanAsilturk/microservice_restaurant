package com.example.orderapi.model.request;

import com.example.orderapi.model.order.Order;
import com.example.orderapi.model.order.OrderItem;
import com.example.orderapi.model.order.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private int customerId;
    private String address;
    private List<OrderItemDto> items;


    public Order toEntity() {
        return Order.builder().
                customerId(customerId).
                address(address).
                items(convertToOrderItems(items)).
                totalAmount(calculateTotalAmount(items)).
                status("CONFIRMED").build();
    }

    private Double calculateTotalAmount(List<OrderItemDto> items) {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private List<OrderItem> convertToOrderItems(List<OrderItemDto> itemDtos) {
        return itemDtos.stream()
                .map(dto -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(dto.getProductId());
                    item.setProductName(dto.getName());
                    item.setQuantity(dto.getQuantity());
                    item.setPrice(dto.getPrice());
                    return item;
                })
                .collect(Collectors.toList());
    }
}
