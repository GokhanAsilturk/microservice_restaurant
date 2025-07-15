package com.example.restaurantapi.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun restaurantApiOpenAPI(): OpenAPI {
        return OpenAPI()
                .info(Info()
                        .title("Restaurant API")
                        .description("Restoran stok yönetimi mikroservis API")
                        .version("1.0")
                        .contact(Contact()
                                .name("Microservice Team")
                                .email("support@example.com"))
                        .license(License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(listOf(
                        Server().url("http://localhost:8082").description("Development Server")
                ))
    }
}
