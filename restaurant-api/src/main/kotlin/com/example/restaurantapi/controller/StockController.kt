package com.example.restaurantapi.controller

import com.example.restaurantapi.service.StockRequest
import com.example.restaurantapi.service.StockResponse
import com.example.restaurantapi.service.StockService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/stock")
@Tag(name = "Stok Yönetimi", description = "Stok kontrol ve yönetim işlemleri")
class StockController(private val stockService: StockService) {

    @PostMapping("/check")
    fun checkStock(@RequestBody request: StockRequest): ResponseEntity<StockResponse> {
        val response = stockService.checkStock(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/reduce")
    fun reduceStock(@RequestBody request: StockRequest): ResponseEntity<StockResponse> {
        val response = stockService.reduceStock(request)
        return if (response.available) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.badRequest().body(response)
        }
    }
}