package com.example.orderapi.exception;

import com.example.orderapi.model.response.ApiResponse;
import com.example.orderapi.model.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderProcessingException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderProcessingException(
            OrderProcessingException ex, WebRequest request) {

        logger.error("Sipariş işleme hatası: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ErrorCode.GENERAL_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        logger.error("Geçersiz parametre: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ErrorCode.VALIDATION_ERROR, "Gönderilen veri formatı hatalı");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, WebRequest request) {

        logger.error("Beklenmeyen hata oluştu: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
