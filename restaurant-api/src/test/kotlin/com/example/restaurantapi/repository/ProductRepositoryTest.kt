package com.example.restaurantapi.repository

import com.example.restaurantapi.model.Product
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    private lateinit var testProduct: Product

    @BeforeEach
    fun setUp() {
        testProduct = Product(
            id = 0,
            name = "Test Pizza",
            price = 29.99,
            stockQuantity = 10
        )
    }

    @Test
    fun `save should persist product to database`() {
        val savedProduct = productRepository.save(testProduct)
        assertNotNull(savedProduct.id)
        assertTrue(savedProduct.id > 0)
        assertEquals(testProduct.name, savedProduct.name)
        assertEquals(testProduct.price, savedProduct.price)
        assertEquals(testProduct.stockQuantity, savedProduct.stockQuantity)
    }

    @Test
    fun `findById should return product when exists`() {
        val savedProduct = testEntityManager.persistAndFlush(testProduct)
        val foundProduct = productRepository.findById(savedProduct.id)
        assertTrue(foundProduct.isPresent)
        assertEquals(savedProduct.id, foundProduct.get().id)
        assertEquals(savedProduct.name, foundProduct.get().name)
    }

    @Test
    fun `findById should return empty when product does not exist`() {
        val foundProduct = productRepository.findById(999)
        assertTrue(foundProduct.isEmpty)
    }

    @Test
    fun `findAll should return all products`() {
        val product1 = testEntityManager.persistAndFlush(testProduct)
        val product2 = testEntityManager.persistAndFlush(
            Product(name = "Test Burger", price = 19.99, stockQuantity = 5)
        )
        val allProducts = productRepository.findAll()
        assertEquals(2, allProducts.size)
        assertTrue(allProducts.any { it.id == product1.id })
        assertTrue(allProducts.any { it.id == product2.id })
    }

    @Test
    fun `findAll should return empty list when no products exist`() {
        val allProducts = productRepository.findAll()
        assertTrue(allProducts.isEmpty())
    }

    @Test
    fun `save should update existing product`() {
        val savedProduct = testEntityManager.persistAndFlush(testProduct)
        testEntityManager.detach(savedProduct)
        val updatedProduct = savedProduct.copy(
            name = "Updated Pizza",
            price = 39.99,
            stockQuantity = 15
        )
        val result = productRepository.save(updatedProduct)
        assertEquals(savedProduct.id, result.id)
        assertEquals("Updated Pizza", result.name)
        assertEquals(39.99, result.price)
        assertEquals(15, result.stockQuantity)
    }

    @Test
    fun `deleteById should remove product from database`() {
        val savedProduct = testEntityManager.persistAndFlush(testProduct)
        productRepository.deleteById(savedProduct.id)
        testEntityManager.flush()
        val foundProduct = productRepository.findById(savedProduct.id)
        assertTrue(foundProduct.isEmpty)
    }

    @Test
    fun `existsById should return true when product exists`() {
        val savedProduct = testEntityManager.persistAndFlush(testProduct)
        val exists = productRepository.existsById(savedProduct.id)
        assertTrue(exists)
    }

    @Test
    fun `existsById should return false when product does not exist`() {
        val exists = productRepository.existsById(999)
        assertTrue(!exists)
    }

    @Test
    fun `count should return correct number of products`() {
        testEntityManager.persistAndFlush(testProduct)
        testEntityManager.persistAndFlush(
            Product(name = "Test Burger", price = 19.99, stockQuantity = 5)
        )
        val count = productRepository.count()
        assertEquals(2, count)
    }

    @Test
    fun `operations should be rolled back after test`() {
        productRepository.save(testProduct)
        val count = productRepository.count()
        assertEquals(1, count)
    }

    @Test
    fun `repository should handle edge cases properly`() {
        val productWithZeroStock = Product(
            name = "Zero Stock Product",
            price = 10.0,
            stockQuantity = 0
        )
        val productWithHighPrice = Product(
            name = "Expensive Product",
            price = 999.99,
            stockQuantity = 1
        )
        val saved1 = productRepository.save(productWithZeroStock)
        val saved2 = productRepository.save(productWithHighPrice)
        assertEquals(0, saved1.stockQuantity)
        assertEquals(999.99, saved2.price)
        assertEquals(2, productRepository.count())
    }
}
