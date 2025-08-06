package com.example.restaurantapi.controller

import com.example.restaurantapi.service.StockItemDto
import com.example.restaurantapi.service.StockRequest
import com.example.restaurantapi.service.StockResponse
import com.example.restaurantapi.service.StockService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * StockController için Integration Test Sınıfı
 */
@WebMvcTest(StockController::class)
class StockControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var stockService: StockService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var stockRequest: StockRequest

    @BeforeEach
    fun setUp() {
        stockRequest = StockRequest(
            items = listOf(
                StockItemDto(productId = 1, quantity = 5)
            )
        )
    }

    /**
     * Test 1: POST /api/stock/check - Stok kontrolü başarılı
     */
    @Test
    fun `checkStock should return stock availability`() {
        // Given
        val stockResponse = StockResponse(
            available = true,
            message = "Stok yeterli"
        )
        whenever(stockService.checkStock(any())).thenReturn(stockResponse)

        // When & Then
        mockMvc.perform(
            post("/api/stock/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.available").value(true))
            .andExpect(jsonPath("$.message").value("Stok yeterli"))
    }

    /**
     * Test 2: POST /api/stock/check - Stok yetersiz
     */
    @Test
    fun `checkStock should return insufficient stock`() {
        // Given
        val stockResponse = StockResponse(
            available = false,
            message = "Stok yetersiz"
        )
        whenever(stockService.checkStock(any())).thenReturn(stockResponse)

        // When & Then
        mockMvc.perform(
            post("/api/stock/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.available").value(false))
            .andExpect(jsonPath("$.message").value("Stok yetersiz"))
    }

    /**
     * Test 3: POST /api/stock/reduce - Stok azaltma başarılı
     */
    @Test
    fun `reduceStock should reduce stock successfully`() {
        // Given
        val stockResponse = StockResponse(
            available = true,
            message = "Stok başarıyla azaltıldı"
        )
        whenever(stockService.reduceStock(any())).thenReturn(stockResponse)

        // When & Then
        mockMvc.perform(
            post("/api/stock/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.available").value(true))
            .andExpect(jsonPath("$.message").value("Stok başarıyla azaltıldı"))
    }

    /**
     * Test 4: POST /api/stock/reduce - Stok azaltma başarısız
     */
    @Test
    fun `reduceStock should return bad request when insufficient stock`() {
        // Given
        val stockResponse = StockResponse(
            available = false,
            message = "Stok yetersiz, azaltılamadı"
        )
        whenever(stockService.reduceStock(any())).thenReturn(stockResponse)

        // When & Then
        mockMvc.perform(
            post("/api/stock/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.available").value(false))
            .andExpect(jsonPath("$.message").value("Stok yetersiz, azaltılamadı"))
    }

    /**
     * Test 5: Geçersiz JSON - check endpoint
     */
    @Test
    fun `checkStock should return 400 for invalid JSON`() {
        // When & Then
        mockMvc.perform(
            post("/api/stock/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        )
            .andExpect(status().isBadRequest)
    }

    /**
     * Test 6: Geçersiz JSON - reduce endpoint
     */
    @Test
    fun `reduceStock should return 400 for invalid JSON`() {
        // When & Then
        mockMvc.perform(
            post("/api/stock/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        )
            .andExpect(status().isBadRequest)
    }
}
