package com.example.orderapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("deliveryId")
    private int deliveryId;

    @JsonProperty("message")
    private String message;
}
