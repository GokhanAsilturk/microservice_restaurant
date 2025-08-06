package com.example.orderapi.repository;

import com.example.orderapi.model.order.Order;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends ElasticsearchRepository<Order, String> {
    // Elasticsearch repository metodları otomatik olarak sağlanır

    /**
     * Müşteri ID'sine göre siparişleri bulur
     *
     * @param customerId Müşteri ID'si
     * @return Müşteriye ait siparişler listesi
     */
    List<Order> findByCustomerId(int customerId);
}
