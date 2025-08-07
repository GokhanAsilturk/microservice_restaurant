package com.example.restaurantapi.repository

import com.example.restaurantapi.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductRepository : JpaRepository<Product, Int> {
    fun findByName(name: String): Optional<Product>
    fun existsByName(name: String): Boolean
}
