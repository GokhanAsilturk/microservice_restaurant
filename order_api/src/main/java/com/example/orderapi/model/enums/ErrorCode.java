package com.example.orderapi.model.enums;

public enum ErrorCode {
    GENERAL_ERROR("GENERAL_ERROR", "Genel hata"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validasyon hatası"),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "Sipariş bulunamadı"),
    STOCK_NOT_AVAILABLE("STOCK_NOT_AVAILABLE", "Stok yetersiz"),
    DELIVERY_SERVICE_UNAVAILABLE("DELIVERY_SERVICE_UNAVAILABLE", "Teslimat servisi kullanılamıyor"),
    PAYMENT_FAILED("PAYMENT_FAILED", "Ödeme başarısız"),
    INVALID_ORDER_STATUS("INVALID_ORDER_STATUS", "Geçersiz sipariş durumu"),
    UNAUTHORIZED("UNAUTHORIZED", "Yetkisiz erişim"),
    FORBIDDEN("FORBIDDEN", "Erişim yasak"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Sunucu hatası");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
