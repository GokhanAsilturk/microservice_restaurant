package com.example.restaurantapi.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global Exception Handler
 *
 * Bu sınıf uygulamadaki tüm hataları yakalar ve uygun HTTP yanıtları döner.
 * Özellikle validation hatalarını düzgün yönetir.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = mutableMapOf<String, String>()

        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Geçersiz değer"
            errors[fieldName] = errorMessage
        }

        val response = mapOf(
            "status" to "error",
            "message" to "Validation hatası",
            "errors" to errors
        )

        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> {
        val response = mapOf(
            "status" to "error",
            "message" to (ex.message ?: "Kayıt bulunamadı")
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        val response = mapOf(
            "status" to "error",
            "message" to "Sunucu hatası oluştu"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}
