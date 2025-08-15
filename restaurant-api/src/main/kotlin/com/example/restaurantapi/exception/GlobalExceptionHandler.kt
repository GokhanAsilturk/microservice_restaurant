package com.example.restaurantapi.exception

import com.example.restaurantapi.model.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Any>> {

        logger.error("Geçersiz parametre: {}", ex.message)

        val response = ApiResponse.error<Any>("Gönderilen veri formatı hatalı", "INVALID_PARAMETER")
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(
        ex: NoSuchElementException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Any>> {

        logger.error("Kayıt bulunamadı: {}", ex.message)

        val response = ApiResponse.error<Any>("İstenen kayıt bulunamadı", "NOT_FOUND")
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Any>> {

        logger.error("Beklenmeyen hata oluştu: {}", ex.message)

        val response = ApiResponse.error<Any>("İşlem sırasında beklenmeyen bir hata oluştu", "INTERNAL_SERVER_ERROR")
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
