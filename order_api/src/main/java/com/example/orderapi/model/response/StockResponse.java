package com.example.orderapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {

    private boolean available;

    private String message;


    public boolean isAvailable() {
        return available;
    }
}
