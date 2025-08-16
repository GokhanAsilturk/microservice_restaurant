package com.example.orderapi;

import com.example.orderapi.model.request.OrderRequest;
import com.example.orderapi.model.order.OrderItemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrderApiApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static GenericContainer<?> elasticsearch = new GenericContainer<>(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.11.0"))
            .withExposedPorts(9200, 9300)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
            .withStartupTimeout(Duration.ofMinutes(3));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", () -> "http://localhost:" + elasticsearch.getMappedPort(9200));
        registry.add("restaurant.api.url", () -> "http://localhost:8081/api");
        registry.add("delivery.api.url", () -> "http://localhost:8082/api/delivery");
    }

    @Test
    void place_order_integration_test() throws Exception {
        // Elasticsearch'in hazır olmasını bekle
        Thread.sleep(5000);

        OrderRequest orderRequest = OrderRequest.builder()
                .customerId(1)
                .address("Test Address, Istanbul")
                .items(Arrays.asList(
                    OrderItemDto.builder()
                        .productId(1)
                        .name("Test Product 1")
                        .quantity(2)
                        .price(29.99)
                        .build(),
                    OrderItemDto.builder()
                        .productId(2)
                        .name("Test Product 2")
                        .quantity(1)
                        .price(15.50)
                        .build()
                ))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> entity = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "http://localhost:" + port + "/api/orders",
            HttpMethod.POST,
            entity,
            String.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        String responseBody = response.getBody();
        assertThat(responseBody).contains("orderId");
        assertThat(responseBody).contains("CREATED");

        // DB'ye kayıt atıldığını kontrol et
        ResponseEntity<String> getAllResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/orders",
            String.class
        );

        assertThat(getAllResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(getAllResponse.getBody()).contains("customerId");
    }
}
