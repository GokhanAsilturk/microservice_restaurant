package com.example.restaurantapi.controller

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.model.request.ProductCreateRequest
import com.example.restaurantapi.model.request.ProductUpdateRequest
import com.example.restaurantapi.model.request.StockUpdateRequest
import com.example.restaurantapi.model.response.ApiResponse
import com.example.restaurantapi.service.ProductService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
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

    @PostMapping
    fun createProduct(@Valid @RequestBody request: ProductCreateRequest): ResponseEntity<ApiResponse<Product>> {
        logger.debug("Yeni ürün oluşturma isteği: {}", request)

        val product = productService.createProduct(request)
        val response = ApiResponse.success(product, "Ürün başarıyla oluşturuldu")

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Int): ResponseEntity<ApiResponse<Product>> {
        logger.debug("Ürün detayı istendi - ID: {}", id)

        val product = productService.getProductById(id)
        val response = ApiResponse.success(product, "Ürün başarıyla getirildi")

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Int,
        @Valid @RequestBody request: ProductUpdateRequest
    ): ResponseEntity<ApiResponse<Product>> {
        logger.debug("Ürün güncelleme isteği - ID: {}, Request: {}", id, request)

        val product = productService.updateProduct(id, request)
        val response = ApiResponse.success(product, "Ürün başarıyla güncellendi")

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}/stock")
    fun updateStock(
        @PathVariable id: Int,
        @Valid @RequestBody request: StockUpdateRequest
    ): ResponseEntity<ApiResponse<Product>> {
        logger.debug("Stok güncelleme isteği - ID: {}, Request: {}", id, request)

        val product = productService.updateStock(id, request)
        val response = ApiResponse.success(product, "Stok başarıyla güncellendi")

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Int): ResponseEntity<ApiResponse<String>> {
        logger.debug("Ürün silme isteği - ID: {}", id)

        productService.deleteProduct(id)
        val response = ApiResponse.success("", "Ürün başarıyla silindi")

        return ResponseEntity.ok(response)
    }
}
