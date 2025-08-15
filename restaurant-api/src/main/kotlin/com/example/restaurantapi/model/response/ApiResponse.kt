package com.example.restaurantapi.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val errorCode: String? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data
            )
        }

        fun <T> success(data: T, message: String): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = data
            )
        }

        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message
            )
        }

        fun <T> error(message: String, errorCode: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                errorCode = errorCode
            )
        }
    }
}
