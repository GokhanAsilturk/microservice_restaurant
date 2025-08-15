package com.example.restaurantapi.config

import com.example.restaurantapi.model.Product
import com.example.restaurantapi.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataLoader {

    private val logger = LoggerFactory.getLogger(DataLoader::class.java)

    @Bean
    fun initDatabase(productRepository: ProductRepository): CommandLineRunner {
        return CommandLineRunner { args ->

            if (productRepository.count() == 0L) {
                val products = listOf(
                    Product(name = "Hamburger", price = 50.0, stockQuantity = 20),
                    Product(name = "Pizza", price = 70.0, stockQuantity = 15),
                    Product(name = "Lahmacun", price = 30.0, stockQuantity = 25),
                    Product(name = "Cola", price = 10.0, stockQuantity = 50),
                    Product(name = "Ayran", price = 8.0, stockQuantity = 40)
                )

                productRepository.saveAll(products)
                logger.info("Örnek ürünler veritabanına yüklendi. Toplam ürün sayısı: {}", productRepository.count())
            } else {
                logger.info("Veritabanında zaten ürünler mevcut. Mevcut ürün sayısı: {}", productRepository.count())
            }
        }
    }
}
