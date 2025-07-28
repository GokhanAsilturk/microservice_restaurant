package com.example.orderapi.repository;

import com.example.orderapi.model.order.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order save(Order order);

}
