package com.example.restaurantapi.service

import com.example.restaurantapi.domain.ProductDomain
import com.example.restaurantapi.model.Product
import com.example.restaurantapi.model.request.ProductCreateRequest
import com.example.restaurantapi.model.request.ProductUpdateRequest
import com.example.restaurantapi.model.request.StockUpdateRequest
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

    fun createProduct(request: ProductCreateRequest): Product {
        logger.debug("Yeni ürün oluşturuluyor: {}", request)

        val productDomain = request.toDomain()

        if (!productDomain.isValidProduct()) {
            throw IllegalArgumentException("Geçersiz ürün bilgileri")
        }

        val savedProduct = productRepository.save(productDomain.toEntity())
        logger.info("Ürün başarıyla oluşturuldu: {}", savedProduct.id)
        return savedProduct
    }

    fun updateProduct(id: Int, request: ProductUpdateRequest): Product {
        logger.debug("Ürün güncelleniyor - ID: {}, Request: {}", id, request)

        val existingProduct = productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Ürün bulunamadı: $id") }

        val productDomain = request.toDomain(id)

        if (!productDomain.isValidProduct()) {
            throw IllegalArgumentException("Geçersiz ürün bilgileri")
        }

        val updatedProduct = productRepository.save(productDomain.toEntity())
        logger.info("Ürün başarıyla güncellendi: {}", updatedProduct.id)
        return updatedProduct
    }

    fun updateStock(id: Int, request: StockUpdateRequest): Product {
        logger.debug("Stok güncelleniyor - ID: {}, Request: {}", id, request)

        val existingProduct = productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Ürün bulunamadı: $id") }

        val productDomain = ProductDomain.fromEntity(existingProduct)
        val updatedDomain = request.applyToDomain(productDomain)

        val updatedProduct = productRepository.save(updatedDomain.toEntity())
        logger.info("Stok başarıyla güncellendi: {}", updatedProduct.id)
        return updatedProduct
    }

    fun deleteProduct(id: Int) {
        logger.debug("Ürün siliniyor - ID: {}", id)

        if (!productRepository.existsById(id)) {
            throw NoSuchElementException("Ürün bulunamadı: $id")
        }

        productRepository.deleteById(id)
        logger.info("Ürün başarıyla silindi: {}", id)
    }

    fun getProductById(id: Int): Product {
        logger.debug("Ürün getiriliyor - ID: {}", id)

        return productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Ürün bulunamadı: $id") }
    }
}