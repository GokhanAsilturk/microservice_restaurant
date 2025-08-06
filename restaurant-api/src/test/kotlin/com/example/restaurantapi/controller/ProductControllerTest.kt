package com.example.restaurantapi.controller

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.service.ProductService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * ProductController için Integration Test Sınıfı
 *
 * Bu test sınıfı REST API endpoint'lerini test eder:
 * - HTTP isteklerini ve yanıtlarını kontrol eder
 * - JSON serileştirme/deserileştirme işlemlerini test eder
 * - HTTP status kodlarını doğrular
 * - Controller katmanındaki hata yönetimini test eder
 * - Validation hatalarını test eder
 */
@WebMvcTest(ProductController::class)
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var testProduct: Product
    private lateinit var productList: List<Product>

    @BeforeEach
    fun setUp() {
        testProduct = Product(
            id = 1,
            name = "Test Pizza",
            price = 29.99,
            stockQuantity = 10
        )

        productList = listOf(
            testProduct,
            Product(
                id = 2,
                name = "Test Burger",
                price = 19.99,
                stockQuantity = 5
            )
        )
    }

    /**
     * Test 1: GET /api/products - Tüm ürünleri getirme başarılı
     */
    @Test
    fun `getAllProducts should return list of products`() {
        // Given
        whenever(productService.getAllProducts()).thenReturn(productList)

        // When & Then
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Test Pizza"))
            .andExpect(jsonPath("$[0].price").value(29.99))
            .andExpect(jsonPath("$[0].stockQuantity").value(10))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Test Burger"))
    }

    /**
     * Test 2: GET /api/products - Boş liste
     */
    @Test
    fun `getAllProducts should return empty list when no products exist`() {
        // Given
        whenever(productService.getAllProducts()).thenReturn(emptyList())

        // When & Then
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(0))
    }

    /**
     * Test 3: GET /api/products/{id} - Ürün bulundu
     */
    @Test
    fun `getProductById should return product when found`() {
        // Given
        whenever(productService.getProductById(eq(1))).thenReturn(testProduct)

        // When & Then
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Pizza"))
            .andExpect(jsonPath("$.price").value(29.99))
            .andExpect(jsonPath("$.stockQuantity").value(10))
    }

    /**
     * Test 4: GET /api/products/{id} - Ürün bulunamadı (GlobalExceptionHandler gerçek davranışına göre)
     */
    @Test
    fun `getProductById should return 404 when product not found`() {
        // Given
        whenever(productService.getProductById(eq(999)))
            .thenThrow(NoSuchElementException("Ürün bulunamadı: ID=999"))

        // When & Then - GlobalExceptionHandler'ın gerçek davranışına göre beklenti
        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.message").value("Ürün bulunamadı: ID=999"))
    }

    /**
     * Test 5: POST /api/products - Ürün oluşturma başarılı
     */
    @Test
    fun `createProduct should create and return new product`() {
        // Given
        val newProduct = Product(
            id = 0,
            name = "New Pizza",
            price = 35.99,
            stockQuantity = 15
        )
        val savedProduct = newProduct.copy(id = 3)

        whenever(productService.createProduct(any())).thenReturn(savedProduct)

        // When & Then
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("New Pizza"))
            .andExpect(jsonPath("$.price").value(35.99))
            .andExpect(jsonPath("$.stockQuantity").value(15))
    }

    /**
     * Test 6: POST /api/products - Validation hatası - Boş isim
     */
    @Test
    fun `createProduct should return 400 for empty product name`() {
        // Given
        val invalidProduct = Product(
            id = 0,
            name = "",
            price = 25.99,
            stockQuantity = 10
        )

        // When & Then
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.message").value("Validation hatası"))
            .andExpect(jsonPath("$.errors.name").exists())
    }

    /**
     * Test 7: POST /api/products - Validation hatası - Negatif fiyat
     */
    @Test
    fun `createProduct should return 400 for negative price`() {
        // Given
        val invalidProduct = Product(
            id = 0,
            name = "Test Product",
            price = -10.0,
            stockQuantity = 10
        )

        // When & Then
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.message").value("Validation hatası"))
            .andExpect(jsonPath("$.errors.price").exists())
    }

    /**
     * Test 8: POST /api/products - Validation hatası - Negatif stok
     */
    @Test
    fun `createProduct should return 400 for negative stock`() {
        // Given
        val invalidProduct = Product(
            id = 0,
            name = "Test Product",
            price = 25.99,
            stockQuantity = -5
        )

        // When & Then
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.message").value("Validation hatası"))
            .andExpect(jsonPath("$.errors.stockQuantity").exists())
    }

    /**
     * Test 9: PUT /api/products/{id} - Ürün güncelleme başarılı
     */
    @Test
    fun `updateProduct should update and return product`() {
        // Given
        val updatedProduct = testProduct.copy(
            name = "Updated Pizza",
            price = 39.99,
            stockQuantity = 20
        )

        whenever(productService.updateProduct(eq(1), any())).thenReturn(updatedProduct)

        // When & Then
        mockMvc.perform(
            put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Pizza"))
            .andExpect(jsonPath("$.price").value(39.99))
            .andExpect(jsonPath("$.stockQuantity").value(20))
    }

    /**
     * Test 10: PUT /api/products/{id} - Ürün bulunamadı
     */
    @Test
    fun `updateProduct should return 404 when product not found`() {
        // Given
        whenever(productService.updateProduct(eq(999), any()))
            .thenThrow(NoSuchElementException("Ürün bulunamadı: ID=999"))

        // When & Then
        mockMvc.perform(
            put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct))
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
    }

    /**
     * Test 11: DELETE /api/products/{id} - Ürün silme başarılı
     */
    @Test
    fun `deleteProduct should delete product successfully`() {
        // Given - No exception means successful deletion

        // When & Then
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isNoContent)
    }

    /**
     * Test 12: DELETE /api/products/{id} - Ürün bulunamadı
     */
    @Test
    fun `deleteProduct should return 404 when product not found`() {
        // Given
        whenever(productService.deleteProduct(eq(999)))
            .thenThrow(NoSuchElementException("Ürün bulunamadı: ID=999"))

        // When & Then
        mockMvc.perform(delete("/api/products/999"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
    }

    /**
     * Test 13: POST /api/products - Geçersiz JSON
     */
    @Test
    fun `createProduct should return 400 for invalid JSON`() {
        // When & Then
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        )
            .andExpect(status().isBadRequest)
    }
}
