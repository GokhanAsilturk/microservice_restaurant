package com.example.restaurantapi.service

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    /**
     * Tüm ürünleri listeler
     */
    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }
}