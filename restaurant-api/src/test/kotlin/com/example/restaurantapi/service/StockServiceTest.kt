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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * StockService için Unit Test Sınıfı
 */
class StockServiceTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    private lateinit var stockService: StockService
    private lateinit var testProduct: Product

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        stockService = StockService(productRepository)

        testProduct = Product(
            id = 1,
            name = "Test Pizza",
            price = 29.99,
            stockQuantity = 10
        )
    }

    /**
     * Test 1: checkStock - Stok yeterli
     */
    @Test
    fun `checkStock should return available when stock is sufficient`() {
        // Given
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 5)
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))

        // When
        val result = stockService.checkStock(stockRequest)

        // Then
        assertTrue(result.available)
        verify(productRepository).findById(1)
    }

    /**
     * Test 2: checkStock - Stok yetersiz
     */
    @Test
    fun `checkStock should return unavailable when stock is insufficient`() {
        // Given
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 15) // More than available
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))

        // When
        val result = stockService.checkStock(stockRequest)

        // Then
        assertFalse(result.available)
        assertTrue(result.message.contains("stok yetersiz"))
        assertTrue(result.message.contains("Test Pizza"))
    }

    /**
     * Test 3: checkStock - Ürün bulunamadı
     */
    @Test
    fun `checkStock should return unavailable when product not found`() {
        // Given
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 999, quantity = 5)
            )
        )
        whenever(productRepository.findById(999)).thenReturn(Optional.empty())

        // When
        val result = stockService.checkStock(stockRequest)

        // Then
        assertFalse(result.available)
        assertTrue(result.message.contains("Stok kontrolü sırasında hata"))
    }

    /**
     * Test 4: checkStock - Çoklu ürün kontrolü başarılı
     */
    @Test
    fun `checkStock should handle multiple items successfully`() {
        // Given
        val product2 = Product(id = 2, name = "Test Burger", price = 19.99, stockQuantity = 8)
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 5),
                StockItemDto(productId = 2, quantity = 3)
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))
        whenever(productRepository.findById(2)).thenReturn(Optional.of(product2))

        // When
        val result = stockService.checkStock(stockRequest)

        // Then
        assertTrue(result.available)
    }

    /**
     * Test 5: checkStock - Çoklu ürün kontrolü, bir ürün yetersiz
     */
    @Test
    fun `checkStock should return unavailable when one item has insufficient stock`() {
        // Given
        val product2 = Product(id = 2, name = "Test Burger", price = 19.99, stockQuantity = 2)
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 5), // OK
                StockItemDto(productId = 2, quantity = 5)  // Not enough
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))
        whenever(productRepository.findById(2)).thenReturn(Optional.of(product2))

        // When
        val result = stockService.checkStock(stockRequest)

        // Then
        assertFalse(result.available)
        assertTrue(result.message.contains("Test Burger"))
    }

    /**
     * Test 6: reduceStock - Başarılı stok azaltma
     */
    @Test
    fun `reduceStock should reduce stock successfully`() {
        // Given
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 5)
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))
        whenever(productRepository.save(any<Product>())).thenReturn(testProduct.copy(stockQuantity = 5))

        // When
        val result = stockService.reduceStock(stockRequest)

        // Then
        assertTrue(result.available)
        verify(productRepository).findById(1)
        verify(productRepository).save(any<Product>())
    }

    /**
     * Test 7: reduceStock - Stok yetersiz
     */
    @Test
    fun `reduceStock should return unavailable when stock insufficient`() {
        // Given
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 15) // More than available
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))

        // When
        val result = stockService.reduceStock(stockRequest)

        // Then
        assertFalse(result.available)
        assertTrue(result.message.contains("Stok azaltma sırasında hata"))
    }

    /**
     * Test 8: reduceStock - Ürün bulunamadı
     */
    @Test
    fun `reduceStock should handle product not found`() {
        // Given
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 999, quantity = 5)
            )
        )
        whenever(productRepository.findById(999)).thenReturn(Optional.empty())

        // When
        val result = stockService.reduceStock(stockRequest)

        // Then
        assertFalse(result.available)
        assertTrue(result.message.contains("Stok azaltma sırasında hata"))
    }

    /**
     * Test 9: reduceStock - Çoklu ürün azaltma
     */
    @Test
    fun `reduceStock should handle multiple items successfully`() {
        // Given
        val product2 = Product(id = 2, name = "Test Burger", price = 19.99, stockQuantity = 8)
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 3),
                StockItemDto(productId = 2, quantity = 2)
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))
        whenever(productRepository.findById(2)).thenReturn(Optional.of(product2))
        whenever(productRepository.save(any<Product>())).thenReturn(testProduct)

        // When
        val result = stockService.reduceStock(stockRequest)

        // Then
        assertTrue(result.available)
        verify(productRepository).findById(1)
        verify(productRepository).findById(2)
    }

    /**
     * Test 10: StockResponse - Java uyumluluğu
     */
    @Test
    fun `StockResponse should provide Java compatibility`() {
        // Given
        val response = StockResponse(available = true, message = "Test")

        // When & Then
        assertTrue(response.isAvailable())
        assertEquals(true, response.available)
        assertEquals("Test", response.message)
    }

    /**
     * Test 11: Edge case - Sıfır miktar
     */
    @Test
    fun `should handle zero quantity requests`() {
        // Given
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 0)
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))

        // When
        val checkResult = stockService.checkStock(stockRequest)
        val reduceResult = stockService.reduceStock(stockRequest)

        // Then
        assertTrue(checkResult.available)
        assertTrue(reduceResult.available)
    }

    /**
     * Test 12: Edge case - Tam stok miktarı
     */
    @Test
    fun `should handle exact stock quantity`() {
        // Given
        val stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 10) // Exact stock amount
            )
        )
        whenever(productRepository.findById(1)).thenReturn(Optional.of(testProduct))
        whenever(productRepository.save(any<Product>())).thenReturn(testProduct.copy(stockQuantity = 0))

        // When
        val checkResult = stockService.checkStock(stockRequest)
        val reduceResult = stockService.reduceStock(stockRequest)

        // Then
        assertTrue(checkResult.available)
        assertTrue(reduceResult.available)
    }
}
