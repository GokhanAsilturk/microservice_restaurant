package com.example.orderapi.repository;

import com.example.orderapi.model.order.Order;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ElasticsearchRepository<Order, Integer> {
    // Elasticsearch repository metodları otomatik olarak sağlanır
}
