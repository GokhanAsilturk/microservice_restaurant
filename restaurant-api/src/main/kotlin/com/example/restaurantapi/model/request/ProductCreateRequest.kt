package com.example.restaurantapi.model.request

import com.example.restaurantapi.domain.ProductDomain
import jakarta.validation.constraints.*

data class ProductCreateRequest(
    @field:NotBlank(message = "Ürün adı boş olamaz")
    @field:Size(min = 2, max = 100, message = "Ürün adı 2-100 karakter arasında olmalıdır")
    val name: String,

    @field:DecimalMin(value = "0.01", message = "Fiyat 0'dan büyük olmalıdır")
    @field:DecimalMax(value = "999999.99", message = "Fiyat çok yüksek")
    val price: Double,

    @field:Min(value = 0, message = "Stok miktarı negatif olamaz")
    @field:Max(value = 999999, message = "Stok miktarı çok yüksek")
    val stockQuantity: Int
) {
    fun toDomain(): ProductDomain {
        return ProductDomain(
            name = name.trim(),
            price = price,
            stockQuantity = stockQuantity
        )
    }
}
