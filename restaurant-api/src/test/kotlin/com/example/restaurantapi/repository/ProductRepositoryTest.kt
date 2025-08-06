package com.example.restaurantapi.repository

import com.example.restaurantapi.model.Product
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * ProductRepository için Integration Test Sınıfı
 *
 * @DataJpaTest anotasyonu:
 * - Sadece JPA repository katmanını test eder
 * - In-memory H2 database kullanır
 * - Transaction'lar otomatik rollback edilir
 */
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
            id = 0, // Auto-generated
            name = "Test Pizza",
            price = 29.99,
            stockQuantity = 10
        )
    }

    /**
     * Test 1: save - Ürün kaydetme
     */
    @Test
    fun `save should persist product to database`() {
        // When
        val savedProduct = productRepository.save(testProduct)

        // Then
        assertNotNull(savedProduct.id)
        assertTrue(savedProduct.id > 0)
        assertEquals(testProduct.name, savedProduct.name)
        assertEquals(testProduct.price, savedProduct.price)
        assertEquals(testProduct.stockQuantity, savedProduct.stockQuantity)
    }

    /**
     * Test 2: findById - Ürün bulma
     */
    @Test
    fun `findById should return product when exists`() {
        // Given
        val savedProduct = testEntityManager.persistAndFlush(testProduct)

        // When
        val foundProduct = productRepository.findById(savedProduct.id)

        // Then
        assertTrue(foundProduct.isPresent)
        assertEquals(savedProduct.id, foundProduct.get().id)
        assertEquals(savedProduct.name, foundProduct.get().name)
    }

    /**
     * Test 3: findById - Ürün bulunamadı
     */
    @Test
    fun `findById should return empty when product does not exist`() {
        // When
        val foundProduct = productRepository.findById(999)

        // Then
        assertTrue(foundProduct.isEmpty)
    }

    /**
     * Test 4: findAll - Tüm ürünleri getirme
     */
    @Test
    fun `findAll should return all products`() {
        // Given
        val product1 = testEntityManager.persistAndFlush(testProduct)
        val product2 = testEntityManager.persistAndFlush(
            Product(name = "Test Burger", price = 19.99, stockQuantity = 5)
        )

        // When
        val allProducts = productRepository.findAll()

        // Then
        assertEquals(2, allProducts.size)
        assertTrue(allProducts.any { it.id == product1.id })
        assertTrue(allProducts.any { it.id == product2.id })
    }

    /**
     * Test 5: findAll - Boş liste
     */
    @Test
    fun `findAll should return empty list when no products exist`() {
        // When
        val allProducts = productRepository.findAll()

        // Then
        assertTrue(allProducts.isEmpty())
    }

    /**
     * Test 6: update - Ürün güncelleme
     */
    @Test
    fun `save should update existing product`() {
        // Given
        val savedProduct = testEntityManager.persistAndFlush(testProduct)
        testEntityManager.detach(savedProduct) // Detach to simulate update scenario

        // When
        val updatedProduct = savedProduct.copy(
            name = "Updated Pizza",
            price = 39.99,
            stockQuantity = 15
        )
        val result = productRepository.save(updatedProduct)

        // Then
        assertEquals(savedProduct.id, result.id)
        assertEquals("Updated Pizza", result.name)
        assertEquals(39.99, result.price)
        assertEquals(15, result.stockQuantity)
    }

    /**
     * Test 7: deleteById - Ürün silme
     */
    @Test
    fun `deleteById should remove product from database`() {
        // Given
        val savedProduct = testEntityManager.persistAndFlush(testProduct)

        // When
        productRepository.deleteById(savedProduct.id)
        testEntityManager.flush()

        // Then
        val foundProduct = productRepository.findById(savedProduct.id)
        assertTrue(foundProduct.isEmpty)
    }

    /**
     * Test 8: existsById - Ürün varlığı kontrolü
     */
    @Test
    fun `existsById should return true when product exists`() {
        // Given
        val savedProduct = testEntityManager.persistAndFlush(testProduct)

        // When
        val exists = productRepository.existsById(savedProduct.id)

        // Then
        assertTrue(exists)
    }

    /**
     * Test 9: existsById - Ürün yoksa false
     */
    @Test
    fun `existsById should return false when product does not exist`() {
        // When
        val exists = productRepository.existsById(999)

        // Then
        assertTrue(!exists)
    }

    /**
     * Test 10: count - Ürün sayısı
     */
    @Test
    fun `count should return correct number of products`() {
        // Given
        testEntityManager.persistAndFlush(testProduct)
        testEntityManager.persistAndFlush(
            Product(name = "Test Burger", price = 19.99, stockQuantity = 5)
        )

        // When
        val count = productRepository.count()

        // Then
        assertEquals(2, count)
    }

    /**
     * Test 11: Transaction rollback test
     */
    @Test
    fun `operations should be rolled back after test`() {
        // Given & When
        productRepository.save(testProduct)
        val count = productRepository.count()

        // Then
        assertEquals(1, count)
        // After test completion, this will be rolled back
    }

    /**
     * Test 12: Custom query test (if any custom queries exist)
     */
    @Test
    fun `repository should handle edge cases properly`() {
        // Given
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

        // When
        val saved1 = productRepository.save(productWithZeroStock)
        val saved2 = productRepository.save(productWithHighPrice)

        // Then
        assertEquals(0, saved1.stockQuantity)
        assertEquals(999.99, saved2.price)
        assertEquals(2, productRepository.count())
    }
}
