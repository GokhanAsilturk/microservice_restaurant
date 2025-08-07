package com.example.restaurantapi.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @Value("\${server.port:8081}")
    private lateinit var port: String

    @GetMapping("/")
    fun home(): Map<String, String> {
        return mapOf(
            "status" to "OK",
            "message" to "Restaurant API çalışıyor",
            "version" to "1.0.0",
            "port" to port,
            "swagger" to "/swagger-ui/index.html"
        )
    }
}
