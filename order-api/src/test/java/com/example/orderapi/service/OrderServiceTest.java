// ...existing code...

@Test
void placeOrder_ShouldHandleDeliveryFailure_WhenDeliveryApiFails() {
    // Test verilerini hazırla
    OrderRequest orderRequest = new OrderRequest();
    orderRequest.setProductId(1L);
    orderRequest.setQuantity(5);

    // InventoryClient mock'unu yapılandır - null olmayan bir yanıt döndürelim
    StockResponse stockResponse = new StockResponse();
    stockResponse.setAvailable(true);
    ResponseEntity<StockResponse> responseEntity = ResponseEntity.ok(stockResponse);
    when(inventoryClient.checkStock(orderRequest.getProductId(), orderRequest.getQuantity()))
        .thenReturn(responseEntity);

    // Delivery service mockunu yapılandır - bu kısım zaten başarısız olacak şekilde ayarlanmış olabilir

    // Test kodunun geri kalanı
    // ...existing code...
}

// ...existing code...

