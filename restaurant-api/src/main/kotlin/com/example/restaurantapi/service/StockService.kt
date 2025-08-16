package com.example.restaurantapi.service

import com.example.restaurantapi.controller.StockItemDto
import com.example.restaurantapi.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(private val productRepository: ProductRepository) {

    private val logger = LoggerFactory.getLogger(StockService::class.java)

    fun checkStock(items: List<StockItemDto>): Boolean {
        logger.debug("Stok kontrolü başlatıldı: {} adet ürün", items.size)

        return items.all { item ->
            val product = productRepository.findById(item.productId).orElse(null)
            if (product == null) {
                logger.warn("Ürün bulunamadı: productId={}", item.productId)
                false
            } else {
                val available = product.stockQuantity >= item.quantity
                logger.debug("Ürün stok kontrolü - productId: {}, istenen: {}, mevcut: {}, uygun: {}",
                    item.productId, item.quantity, product.stockQuantity, available)
                available
            }
        }
    }

    @Transactional
    fun reduceStock(items: List<StockItemDto>): Boolean {
        logger.debug("Stok azaltma işlemi başlatıldı: {} adet ürün", items.size)

        try {
            if (!checkStock(items)) {
                logger.warn("Stok yetersiz, işlem iptal edildi")
                return false
            }

            items.forEach { item ->
                val product = productRepository.findById(item.productId).orElseThrow {
                    RuntimeException("Ürün bulunamadı: ${item.productId}")
                }

                product.stockQuantity -= item.quantity
                productRepository.save(product)

                logger.debug("Stok azaltıldı - productId: {}, azaltılan: {}, kalan: {}",
                    item.productId, item.quantity, product.stockQuantity)
            }

            logger.info("Stok azaltma işlemi başarıyla tamamlandı")
            return true

        } catch (e: Exception) {
            logger.error("Stok azaltma işlemi başarısız: {}", e.message)
            throw e
        }
    }
}
