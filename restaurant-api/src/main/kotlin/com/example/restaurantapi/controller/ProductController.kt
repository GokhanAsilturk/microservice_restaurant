package com.example.restaurantapi.controller

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.model.response.ApiResponse
import com.example.restaurantapi.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    private val logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping
    fun getAllProducts(): ResponseEntity<ApiResponse<List<Product>>> {
        logger.debug("Tüm ürünler istendi")
        val products = productService.getAllProducts()
        logger.info("{} adet ürün döndürüldü", products.size)

        val response = ApiResponse.success(products, "Ürünler başarıyla getirildi")
        return ResponseEntity.ok(response)
    }
}
