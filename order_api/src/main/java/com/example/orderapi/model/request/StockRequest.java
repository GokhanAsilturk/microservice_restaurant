package com.example.orderapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {
    private List<StockItemDto> items;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockItemDto {
        private Integer productId;
        private Integer quantity;
    }
}
