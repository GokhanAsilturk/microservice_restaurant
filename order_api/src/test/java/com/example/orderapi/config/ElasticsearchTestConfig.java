package com.example.orderapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Elasticsearch Test Konfigürasyonu
 * <p>
 * Bu sınıf test ortamında Elasticsearch container'ını başlatır ve
 * Spring Data Elasticsearch'ün bu container'a bağlanmasını sağlar.
 */
@TestConfiguration
public class ElasticsearchTestConfig extends ElasticsearchConfiguration {

    private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:7.17.10";

    private static ElasticsearchContainer elasticsearchContainer;

    @Bean
    @Primary
    public ElasticsearchContainer elasticsearchContainer() {
        if (elasticsearchContainer == null) {
            elasticsearchContainer = new ElasticsearchContainer(
                    DockerImageName.parse(ELASTICSEARCH_IMAGE))
                    .withEnv("discovery.type", "single-node")
                    .withEnv("xpack.security.enabled", "false")
                    .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

            elasticsearchContainer.start();
        }
        return elasticsearchContainer;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        ElasticsearchContainer container = elasticsearchContainer();
        return ClientConfiguration.builder()
                .connectedTo(container.getHttpHostAddress())
                .build();
    }
}
