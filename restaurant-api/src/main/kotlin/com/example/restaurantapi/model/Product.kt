package com.example.restaurantapi.model

import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true)
    @field:NotBlank(message = "Ürün adı boş olamaz")
    @field:Size(min = 2, max = 100, message = "Ürün adı 2-100 karakter arasında olmalıdır")
    val name: String,

    @Column(nullable = false)
    @field:DecimalMin(value = "0.01", message = "Fiyat 0'dan büyük olmalıdır")
    @field:DecimalMax(value = "999999.99", message = "Fiyat çok yüksek")
    val price: Double,

    @Column(nullable = false)
    @field:Min(value = 0, message = "Stok miktarı negatif olamaz")
    @field:Max(value = 999999, message = "Stok miktarı çok yüksek")
    var stockQuantity: Int
)
