package com.example.restaurantapi.model.request

import com.example.restaurantapi.domain.ProductDomain
import jakarta.validation.constraints.*

data class StockUpdateRequest(
    @field:Min(value = 1, message = "Stok miktarı pozitif olmalıdır")
    @field:Max(value = 999999, message = "Stok miktarı çok yüksek")
    val quantity: Int,

    @field:NotBlank(message = "İşlem tipi belirtilmelidir")
    @field:Pattern(regexp = "^(ADD|REDUCE)$", message = "İşlem tipi ADD veya REDUCE olmalıdır")
    val operation: String
) {
    fun applyToDomain(productDomain: ProductDomain): ProductDomain {
        return when (operation.uppercase()) {
            "ADD" -> productDomain.addStock(quantity)
            "REDUCE" -> productDomain.reduceStock(quantity)
            else -> throw IllegalArgumentException("Geçersiz işlem tipi: $operation")
        }
    }
}
