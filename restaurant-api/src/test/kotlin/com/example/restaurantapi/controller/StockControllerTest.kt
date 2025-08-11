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

    @Test
    fun `checkStock should return stock availability`() {
        val stockResponse = StockResponse(
            available = true,
            message = "Stok yeterli"
        )
        whenever(stockService.checkStock(any())).thenReturn(stockResponse)

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

    @Test
    fun `checkStock should return insufficient stock`() {
        val stockResponse = StockResponse(
            available = false,
            message = "Stok yetersiz"
        )
        whenever(stockService.checkStock(any())).thenReturn(stockResponse)

        mockMvc.perform(
            post("/api/stock/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.available").value(false))
            .andExpect(jsonPath("$.message").value("Stok yetersiz"))
    }

    @Test
    fun `reduceStock should reduce stock successfully`() {
        val stockResponse = StockResponse(
            available = true,
            message = "Stok başarıyla azaltıldı"
        )
        whenever(stockService.reduceStock(any())).thenReturn(stockResponse)

        mockMvc.perform(
            post("/api/stock/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.available").value(true))
            .andExpect(jsonPath("$.message").value("Stok başarıyla azaltıldı"))
    }

    @Test
    fun `reduceStock should return bad request when insufficient stock`() {
        val stockResponse = StockResponse(
            available = false,
            message = "Stok yetersiz, azaltılamadı"
        )
        whenever(stockService.reduceStock(any())).thenReturn(stockResponse)

        mockMvc.perform(
            post("/api/stock/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.available").value(false))
            .andExpect(jsonPath("$.message").value("Stok yetersiz, azaltılamadı"))
    }

    @Test
    fun `checkStock should return 400 for invalid JSON`() {
        mockMvc.perform(
            post("/api/stock/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `reduceStock should return 400 for invalid JSON`() {
        mockMvc.perform(
            post("/api/stock/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }")
        )
            .andExpect(status().isBadRequest)
    }
}
