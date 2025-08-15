package com.example.restaurantapi.service

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

data class StockRequest(val items: List<StockItemDto>)
data class StockItemDto(val productId: Int, val quantity: Int)
data class StockResponse(val available: Boolean, val message: String = "") {

    fun isAvailable(): Boolean = available
}

@Service
class StockService(private val productRepository: ProductRepository) {

    private val logger = LoggerFactory.getLogger(StockService::class.java)

    @Transactional(readOnly = true)
    fun checkStock(request: StockRequest): StockResponse {
        try {
            logger.debug("Stok kontrol işlemi başlatıldı: {}", request)
            for (item in request.items) {
                val product = productRepository.findById(item.productId)
                    .orElseThrow { NoSuchElementException("Ürün bulunamadı: ID=${item.productId}") }

                if (product.stockQuantity < item.quantity) {
                    logger.warn("Stok yetersiz - Ürün: {}, Mevcut: {}, İstenen: {}",
                        product.name, product.stockQuantity, item.quantity)
                    return StockResponse(
                        available = false,
                        message = "${product.name} ürünü için stok yetersiz (Mevcut: ${product.stockQuantity}, İstenen: ${item.quantity})"
                    )
                }
            }
            logger.info("Stok kontrol başarılı")
            return StockResponse(available = true)
        } catch (e: Exception) {
            logger.error("Stok kontrolü sırasında hata: {}", e.message, e)
            return StockResponse(available = false, message = "Stok kontrolü sırasında hata: ${e.message}")
        }
    }

    @Transactional
    fun reduceStock(request: StockRequest): StockResponse {
        try {
            logger.debug("Stok azaltma işlemi başlatıldı: {}", request)
            for (item in request.items) {
                val product = productRepository.findById(item.productId)
                    .orElseThrow { NoSuchElementException("Ürün bulunamadı: ID=${item.productId}") }

                if (product.stockQuantity < item.quantity) {
                    logger.error("Stok azaltma başarısız - Ürün: {}, Mevcut: {}, İstenen: {}",
                        product.name, product.stockQuantity, item.quantity)
                    throw IllegalStateException("${product.name} ürünü için stok yetersiz")
                }

                val oldQuantity = product.stockQuantity
                product.stockQuantity -= item.quantity
                productRepository.save(product)
                logger.info("Stok güncellendi - Ürün: {}, Eski Miktar: {}, Yeni Miktar: {}",
                    product.name, oldQuantity, product.stockQuantity)
            }
            logger.info("Stok azaltma işlemi başarıyla tamamlandı")
            return StockResponse(available = true, message = "Stoklar başarıyla güncellendi")
        } catch (e: Exception) {
            logger.error("Stok güncellemesi sırasında hata: {}", e.message, e)
            return StockResponse(available = false, message = "Stok güncellemesi sırasında hata: ${e.message}")
        }
    }
}