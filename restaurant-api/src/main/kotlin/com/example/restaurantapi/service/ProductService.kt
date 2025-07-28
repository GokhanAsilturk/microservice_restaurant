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

    /**
     * ID'ye göre ürün getirir
     */
    fun getProductById(id: Int): Product {
        return productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Ürün bulunamadı: ID=$id") }
    }

    /**
     * Yeni ürün ekler
     */
    fun createProduct(product: Product): Product {
        return productRepository.save(product)
    }

    /**
     * Ürün bilgilerini günceller
     */
    fun updateProduct(id: Int, product: Product): Product {
        if (!productRepository.existsById(id)) {
            throw NoSuchElementException("Ürün bulunamadı: ID=$id")
        }
        val updatedProduct = product.copy(id = id)
        return productRepository.save(updatedProduct)
    }

    /**
     * Ürünü siler
     */
    fun deleteProduct(id: Int) {
        if (!productRepository.existsById(id)) {
            throw NoSuchElementException("Ürün bulunamadı: ID=$id")
        }
        productRepository.deleteById(id)
    }
}