package com.example.orderapi.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.orderapi.model.enums.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;

    private T data;

    private String errorCode;

    private String errorDescription;

    private List<ValidationError> validationErrors;

    private LocalDateTime timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;

        private String message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode.getCode())
                .errorDescription(errorCode.getDescription())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customDescription) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode.getCode())
                .errorDescription(customDescription)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String errorDescription) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .errorDescription(errorDescription)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> validationError(List<ValidationError> validationErrors) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(ErrorCode.VALIDATION_ERROR.getCode())
                .errorDescription(ErrorCode.VALIDATION_ERROR.getDescription())
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
