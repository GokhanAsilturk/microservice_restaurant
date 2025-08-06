package com.example.orderapi;

import com.example.orderapi.repository.OrderRepository;
import com.example.orderapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

/**
 * Order API Application Tests
 *
 * Bu test sınıfı Spring Boot uygulamasının context'inin başarıyla yüklendiğini test eder.
 * Tüm bağımlılıkları mock'layarak hızlı context loading sağlar.
 */
@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
    ElasticsearchRestClientAutoConfiguration.class,
    ElasticsearchDataAutoConfiguration.class
})
class OrderApiApplicationTests {

    @MockBean
    private ElasticsearchOperations elasticsearchOperations;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Bu test Spring Boot context'inin başarıyla yüklendiğini doğrular
        // Tüm bağımlılıklar mock'lanarak context loading problemi çözülür
    }

}
