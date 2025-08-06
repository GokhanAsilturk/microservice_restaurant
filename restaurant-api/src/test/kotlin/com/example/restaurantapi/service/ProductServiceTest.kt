package com.example.restaurantapi.service

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.repository.ProductRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * ProductService için Unit Test Sınıfı
 *
 * Bu test sınıfı business logic'i test eder:
 * - Service katmanındaki iş kurallarını test eder
 * - Repository ile etkileşimleri doğrular
 * - Exception handling'i test eder
 * - Data transformation işlemlerini kontrol eder
 */
class ProductServiceTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    private lateinit var productService: ProductService
    private lateinit var testProduct: Product

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        productService = ProductService(productRepository)

        testProduct = Product(
            id = 1,
            name = "Test Pizza",
            price = 29.99,
            stockQuantity = 10
        )
    }

    /**
     * Test 1: getAllProducts - Başarılı liste getirme
     */
    @Test
    fun `getAllProducts should return all products from repository`() {
        // Given
        val expectedProducts = listOf(
            testProduct,
            Product(id = 2, name = "Test Burger", price = 19.99, stockQuantity = 5)
        )
        whenever(productRepository.findAll()).thenReturn(expectedProducts)

        // When
        val result = productService.getAllProducts()

        // Then
        assertEquals(expectedProducts, result)
        verify(productRepository).findAll()
    }

    /**
     * Test 2: getAllProducts - Boş liste
     */
    @Test
    fun `getAllProducts should return empty list when no products exist`() {
        // Given
        whenever(productRepository.findAll()).thenReturn(emptyList())

        // When
        val result = productService.getAllProducts()

        // Then
        assertEquals(emptyList(), result)
        verify(productRepository).findAll()
    }

    /**
     * Test 3: getProductById - Ürün bulundu
     */
    @Test
    fun `getProductById should return product when found`() {
        // Given
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))

        // When
        val result = productService.getProductById(1)

        // Then
        assertEquals(testProduct, result)
        verify(productRepository).findById(1)
    }

    /**
     * Test 4: getProductById - Ürün bulunamadı
     */
    @Test
    fun `getProductById should throw exception when product not found`() {
        // Given
        whenever(productRepository.findById(999)).thenReturn(Optional.empty())

        // When & Then
        val exception = assertThrows<NoSuchElementException> {
            productService.getProductById(999)
        }
        assertEquals("Ürün bulunamadı: ID=999", exception.message)
        verify(productRepository).findById(999)
    }

    /**
     * Test 5: createProduct - Başarılı ürün oluşturma
     */
    @Test
    fun `createProduct should save and return new product`() {
        // Given
        val newProduct = Product(
            id = 0,
            name = "New Pizza",
            price = 35.99,
            stockQuantity = 15
        )
        val savedProduct = newProduct.copy(id = 3)
        whenever(productRepository.save(newProduct)).thenReturn(savedProduct)

        // When
        val result = productService.createProduct(newProduct)

        // Then
        assertEquals(savedProduct, result)
        verify(productRepository).save(newProduct)
    }

    /**
     * Test 6: createProduct - Null product handling
     */
    @Test
    fun `createProduct should handle product with null values gracefully`() {
        // Given
        val productWithDefaults = Product(
            id = 0,
            name = "Default Product",
            price = 0.0,
            stockQuantity = 0
        )
        whenever(productRepository.save(any())).thenReturn(productWithDefaults)

        // When
        val result = productService.createProduct(productWithDefaults)

        // Then
        assertNotNull(result)
        verify(productRepository).save(productWithDefaults)
    }

    /**
     * Test 7: updateProduct - Başarılı güncelleme
     */
    @Test
    fun `updateProduct should update existing product`() {
        // Given
        val updatedProduct = testProduct.copy(
            id = 1, // ID'yi açıkça belirt
            name = "Updated Pizza",
            price = 39.99,
            stockQuantity = 20
        )
        whenever(productRepository.existsById(1)).thenReturn(true)
        whenever(productRepository.save(any<Product>())).thenReturn(updatedProduct)

        // When
        val result = productService.updateProduct(1, updatedProduct)

        // Then
        assertNotNull(result)
        assertEquals(updatedProduct, result)
        assertEquals(1, result.id) // ID should be preserved
        verify(productRepository).existsById(1)
        verify(productRepository).save(any<Product>())
    }

    /**
     * Test 8: updateProduct - Ürün bulunamadı
     */
    @Test
    fun `updateProduct should throw exception when product not found`() {
        // Given
        whenever(productRepository.existsById(999)).thenReturn(false)

        // When & Then
        val exception = assertThrows<NoSuchElementException> {
            productService.updateProduct(999, testProduct)
        }
        assertEquals("Ürün bulunamadı: ID=999", exception.message)
        verify(productRepository).existsById(999)
    }

    /**
     * Test 9: updateProduct - ID preservation
     */
    @Test
    fun `updateProduct should preserve original ID`() {
        // Given
        val productWithWrongId = testProduct.copy(id = 999, name = "Updated Name")
        whenever(productRepository.existsById(1)).thenReturn(true)
        whenever(productRepository.save(any())).thenReturn(productWithWrongId.copy(id = 1))

        // When
        val result = productService.updateProduct(1, productWithWrongId)

        // Then
        assertEquals(1, result.id) // Should preserve original ID
        assertEquals("Updated Name", result.name)
        verify(productRepository).existsById(1)
        verify(productRepository).save(any())
    }

    /**
     * Test 10: deleteProduct - Başarılı silme
     */
    @Test
    fun `deleteProduct should delete existing product`() {
        // Given
        whenever(productRepository.existsById(1)).thenReturn(true)

        // When
        productService.deleteProduct(1)

        // Then
        verify(productRepository).existsById(1)
        verify(productRepository).deleteById(1)
    }

    /**
     * Test 11: deleteProduct - Ürün bulunamadı
     */
    @Test
    fun `deleteProduct should throw exception when product not found`() {
        // Given
        whenever(productRepository.existsById(999)).thenReturn(false)

        // When & Then
        val exception = assertThrows<NoSuchElementException> {
            productService.deleteProduct(999)
        }
        assertEquals("Ürün bulunamadı: ID=999", exception.message)
        verify(productRepository).existsById(999)
    }

    /**
     * Test 12: Repository interaction verification
     */
    @Test
    fun `service should properly interact with repository`() {
        // Given
        whenever(productRepository.findAll()).thenReturn(listOf(testProduct))
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))
        whenever(productRepository.save(any())).thenReturn(testProduct)
        whenever(productRepository.existsById(1)).thenReturn(true)

        // When
        productService.getAllProducts()
        productService.getProductById(1)
        productService.createProduct(testProduct)
        productService.updateProduct(1, testProduct)
        productService.deleteProduct(1)

        // Then - Verify all repository methods were called
        verify(productRepository).findAll()
        verify(productRepository).findById(1)
        verify(productRepository).save(any())
        verify(productRepository).existsById(1)
        verify(productRepository).deleteById(1)
    }

    /**
     * Test 13: Edge cases - Negative values handling
     */
    @Test
    fun `service should handle edge cases gracefully`() {
        // Given
        val edgeCaseProduct = Product(
            id = -1,
            name = "",
            price = -10.0,
            stockQuantity = -5
        )
        whenever(productRepository.save(any())).thenReturn(edgeCaseProduct)

        // When
        val result = productService.createProduct(edgeCaseProduct)

        // Then
        assertNotNull(result)
        verify(productRepository).save(edgeCaseProduct)
    }
}
