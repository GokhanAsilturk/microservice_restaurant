package com.example.restaurantapi.controller

import com.example.restaurantapi.model.response.ApiResponse
import com.example.restaurantapi.service.StockRequest
import com.example.restaurantapi.service.StockResponse
import com.example.restaurantapi.service.StockService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/stock")
class StockController(private val stockService: StockService) {

    private val logger = LoggerFactory.getLogger(StockController::class.java)

    @PostMapping("/check")
    fun checkStock(@RequestBody request: StockRequest): ResponseEntity<ApiResponse<StockResponse>> {
        logger.info("Stok kontrol isteği alındı: {}", request)
        val stockResponse = stockService.checkStock(request)
        logger.info("Stok kontrol sonucu: {}", stockResponse.available)

        val response = ApiResponse.success(stockResponse, "Stok kontrolü tamamlandı")
        return ResponseEntity.ok(response)
    }

    @PostMapping("/reduce")
    fun reduceStock(@RequestBody request: StockRequest): ResponseEntity<ApiResponse<StockResponse>> {
        logger.info("Stok azaltma isteği alındı: {}", request)
        val stockResponse = stockService.reduceStock(request)
        logger.info("Stok azaltma sonucu: {}", stockResponse.available)

        return if (stockResponse.available) {
            val response = ApiResponse.success(stockResponse, "Stok başarıyla güncellendi")
            ResponseEntity.ok(response)
        } else {
            val response = ApiResponse.error<StockResponse>(stockResponse.message, "STOCK_ERROR")
            ResponseEntity.badRequest().body(response)
        }
    }
}
