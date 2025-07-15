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


    public Order toOrder() {
        Order order = new Order();
        order.setCustomerId(this.customerId);
        order.setAddress(this.address);
        order.setStatus("PENDING");

        List<OrderItem> orderItems = this.items.stream().map(itemDto -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemDto.getProductId());
            orderItem.setProductName(itemDto.getName());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(itemDto.getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        Double totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);

        return order;
    }
}
