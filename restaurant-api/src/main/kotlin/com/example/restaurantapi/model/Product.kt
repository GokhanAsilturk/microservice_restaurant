package com.example.restaurantapi.model

import jakarta.persistence.*

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    var stockQuantity: Int
)
