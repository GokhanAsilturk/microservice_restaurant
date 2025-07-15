package com.example.restaurantapi.service

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

data class StockRequest(val items: List<StockItemDto>)
data class StockItemDto(val productId: Long, val quantity: Int)
data class StockResponse(val available: Boolean, val message: String = "") {
    // Java uyumluluğu için isAvailable() metodu ekliyoruz
    fun isAvailable(): Boolean = available
}

@Service
class StockService(private val productRepository: ProductRepository) {

    @Transactional(readOnly = true)
    fun checkStock(request: StockRequest): StockResponse {
        try {
            for (item in request.items) {
                val product = productRepository.findById(item.productId)
                    .orElseThrow { NoSuchElementException("Ürün bulunamadı: ID=${item.productId}") }

                if (product.stockQuantity < item.quantity) {
                    return StockResponse(
                        available = false,
                        message = "${product.name} ürünü için stok yetersiz (Mevcut: ${product.stockQuantity}, İstenen: ${item.quantity})"
                    )
                }
            }
            return StockResponse(available = true)
        } catch (e: Exception) {
            return StockResponse(available = false, message = "Stok kontrolü sırasında hata: ${e.message}")
        }
    }

    @Transactional
    fun reduceStock(request: StockRequest): StockResponse {
        try {
            for (item in request.items) {
                val product = productRepository.findById(item.productId)
                    .orElseThrow { NoSuchElementException("Ürün bulunamadı: ID=${item.productId}") }

                if (product.stockQuantity < item.quantity) {
                    throw IllegalStateException("${product.name} ürünü için stok yetersiz")
                }

                product.stockQuantity -= item.quantity
                productRepository.save(product)
            }
            return StockResponse(available = true, message = "Stoklar başarıyla güncellendi")
        } catch (e: Exception) {
            return StockResponse(available = false, message = "Stok güncellemesi sırasında hata: ${e.message}")
        }
    }
}
