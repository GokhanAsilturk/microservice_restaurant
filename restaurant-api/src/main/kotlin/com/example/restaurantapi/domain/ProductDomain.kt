package com.example.restaurantapi.domain

import com.example.restaurantapi.model.Product

data class ProductDomain(
    val id: Int = 0,
    val name: String,
    val price: Double,
    val stockQuantity: Int
) {
    fun canSell(requestedQuantity: Int): Boolean {
        return stockQuantity >= requestedQuantity && requestedQuantity > 0
    }

    fun isValidProduct(): Boolean {
        return name.isNotBlank() && 
               name.length in 2..100 &&
               price > 0.0 && 
               price <= 999999.99 &&
               stockQuantity >= 0 &&
               stockQuantity <= 999999
    }

    fun reduceStock(quantity: Int): ProductDomain {
        if (!canSell(quantity)) {
            throw IllegalStateException("Yetersiz stok veya geçersiz miktar")
        }
        return copy(stockQuantity = stockQuantity - quantity)
    }

    fun addStock(quantity: Int): ProductDomain {
        if (quantity <= 0) {
            throw IllegalArgumentException("Stok ekleme miktarı pozitif olmalıdır")
        }
        val newStock = stockQuantity + quantity
        if (newStock > 999999) {
            throw IllegalArgumentException("Maksimum stok limitini aşıyor")
        }
        return copy(stockQuantity = newStock)
    }

    fun updatePrice(newPrice: Double): ProductDomain {
        if (newPrice <= 0.0 || newPrice > 999999.99) {
            throw IllegalArgumentException("Geçersiz fiyat değeri")
        }
        return copy(price = newPrice)
    }

    fun toEntity(): Product {
        return Product(
            id = id,
            name = name,
            price = price,
            stockQuantity = stockQuantity
        )
    }

    companion object {
        fun fromEntity(entity: Product): ProductDomain {
            return ProductDomain(
                id = entity.id,
                name = entity.name,
                price = entity.price,
                stockQuantity = entity.stockQuantity
            )
        }
    }
}
