package com.example.restaurantapi.controller

import com.example.restaurantapi.model.response.ApiResponse
import com.example.restaurantapi.service.StockService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class StockRequest(
    val items: List<StockItemDto>
)

data class StockItemDto(
    val productId: Int,
    val quantity: Int
)

data class StockResponse(
    val available: Boolean,
    val message: String = ""
)

@RestController
@RequestMapping("/api/stock")
class StockController(private val stockService: StockService) {

    private val logger = LoggerFactory.getLogger(StockController::class.java)

    @PostMapping("/check")
    fun checkStock(@RequestBody request: StockRequest): ResponseEntity<StockResponse> {
        logger.debug("Stok kontrolü istendi: {}", request)
        
        try {
            val isAvailable = stockService.checkStock(request.items)
            
            val response = if (isAvailable) {
                StockResponse(available = true, message = "Tüm ürünler stokta mevcut")
            } else {
                StockResponse(available = false, message = "Bazı ürünlerde yetersiz stok")
            }
            
            logger.info("Stok kontrolü tamamlandı. Sonuç: {}", response.available)
            return ResponseEntity.ok(response)
            
        } catch (e: Exception) {
            logger.error("Stok kontrolü sırasında hata: {}", e.message)
            val response = StockResponse(available = false, message = "Stok kontrolü başarısız: ${e.message}")
            return ResponseEntity.ok(response)
        }
    }

    @PostMapping("/reduce")
    fun reduceStock(@RequestBody request: StockRequest): ResponseEntity<StockResponse> {
        logger.debug("Stok azaltma istendi: {}", request)
        
        try {
            val success = stockService.reduceStock(request.items)
            
            val response = if (success) {
                StockResponse(available = true, message = "Stok başarıyla azaltıldı")
            } else {
                StockResponse(available = false, message = "Stok azaltma başarısız")
            }
            
            logger.info("Stok azaltma tamamlandı. Sonuç: {}", response.available)
            return ResponseEntity.ok(response)
            
        } catch (e: Exception) {
            logger.error("Stok azaltma sırasında hata: {}", e.message)
            val response = StockResponse(available = false, message = "Stok azaltma başarısız: ${e.message}")
            return ResponseEntity.ok(response)
        }
    }
}
