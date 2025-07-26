package com.example.restaurantapi.controller

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
@Tag(name = "Ürün Yönetimi", description = "Ürün listeleme ve yönetim işlemleri")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<Product>> {
        val products = productService.getAllProducts()
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Int): ResponseEntity<Product> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(product)
    }

    @PostMapping
    fun createProduct(@RequestBody product: Product): ResponseEntity<Product> {
        val createdProduct = productService.createProduct(product)
        return ResponseEntity.ok(createdProduct)
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Int, @RequestBody product: Product): ResponseEntity<Product> {
        val updatedProduct = productService.updateProduct(id, product)
        return ResponseEntity.ok(updatedProduct)
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Int): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
}
