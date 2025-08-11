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
        testProduct = Product(id = 1, name = "Test Pizza", price = 29.99, stockQuantity = 10)
        productList = listOf(testProduct, Product(id = 2, name = "Test Burger", price = 19.99, stockQuantity = 5))
    }

    @Test
    fun `getAllProducts should return list of products`() {
        whenever(productService.getAllProducts()).thenReturn(productList)
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

    @Test
    fun `getAllProducts should return empty list when no products exist`() {
        whenever(productService.getAllProducts()).thenReturn(emptyList())
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `getProductById should return product when found`() {
        whenever(productService.getProductById(eq(1))).thenReturn(testProduct)
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Pizza"))
            .andExpect(jsonPath("$.price").value(29.99))
            .andExpect(jsonPath("$.stockQuantity").value(10))
    }

    @Test
    fun `getProductById should return 404 when product not found`() {
        whenever(productService.getProductById(eq(999)))
            .thenThrow(NoSuchElementException("Ürün bulunamadı: ID=999"))
        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.message").value("Ürün bulunamadı: ID=999"))
    }

    @Test
    fun `createProduct should create and return new product`() {
        val newProduct = Product(id = 0, name = "New Pizza", price = 35.99, stockQuantity = 15)
        val savedProduct = newProduct.copy(id = 3)
        whenever(productService.createProduct(any())).thenReturn(savedProduct)
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

    @Test
    fun `createProduct should return 400 for empty product name`() {
        val invalidProduct = Product(id = 0, name = "", price = 25.99, stockQuantity = 10)
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

    @Test
    fun `createProduct should return 400 for negative price`() {
        val invalidProduct = Product(id = 0, name = "Test Product", price = -10.0, stockQuantity = 10)
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

    @Test
    fun `createProduct should return 400 for negative stock`() {
        val invalidProduct = Product(id = 0, name = "Test Product", price = 25.99, stockQuantity = -5)
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

    @Test
    fun `updateProduct should update and return product`() {
        val updatedProduct = testProduct.copy(name = "Updated Pizza", price = 39.99, stockQuantity = 20)
        whenever(productService.updateProduct(eq(1), any())).thenReturn(updatedProduct)
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

    @Test
    fun `updateProduct should return 404 when product not found`() {
        whenever(productService.updateProduct(eq(999), any()))
            .thenThrow(NoSuchElementException("Ürün bulunamadı: ID=999"))
        mockMvc.perform(
            put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct))
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
    }

    @Test
    fun `deleteProduct should delete product successfully`() {
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteProduct should return 404 when product not found`() {
        whenever(productService.deleteProduct(eq(999)))
            .thenThrow(NoSuchElementException("Ürün bulunamadı: ID=999"))
        mockMvc.perform(delete("/api/products/999"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("error"))
    }

    @Test
    fun `createProduct should return 400 for invalid JSON`() {
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        )
            .andExpect(status().isBadRequest)
    }
}
