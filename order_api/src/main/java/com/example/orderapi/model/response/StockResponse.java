package com.example.orderapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Restaurant API'den gelen stok kontrol yanıt modeli
 *
 * Bu sınıf Restaurant API'nin stok kontrol ve stok azaltma
 * işlemlerinden gelen yanıtları temsil eder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {

    @JsonProperty("available")
    private boolean available;

    @JsonProperty("message")
    private String message;

    /**
     * Stok durumu kontrolü için helper method
     * @return stok uygun mu?
     */
    public boolean isAvailable() {
        return available;
    }
}
