package com.example.restaurantapi.service

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    private val logger = LoggerFactory.getLogger(ProductService::class.java)


    fun getAllProducts(): List<Product> {
        logger.debug("Tüm ürünler getiriliyor")
        val products = productRepository.findAll()
        logger.info("{} adet ürün getirildi", products.size)
        return products
    }
}