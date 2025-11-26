package com.matkon.gamelog.infrastructure.integration.tmdb;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(TmdbClientProperties.class)
class TmdbRestClientConfig {

    private static final String ACCEPT = "accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String TMDB_API_ERROR_MESSAGE = "TMDB API server error: ";

    @Bean
    @Qualifier("tmdbRestClient")
    RestClient tmdbRestClient(RestClient.Builder builder, TmdbClientProperties properties) {
        String baseUrlWithLanguage = properties.getUrl() + "?language=en-US";

        return builder
                .baseUrl(baseUrlWithLanguage)
                .defaultHeader(ACCEPT, APPLICATION_JSON)
                .defaultHeader("Authorization", "Bearer " + properties.getKey())
                .requestFactory(createRequestFactory(properties))
                .defaultStatusHandler(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new RuntimeException(TMDB_API_ERROR_MESSAGE + response.getStatusCode());
                        }
                )
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory(TmdbClientProperties properties) {
        return new JdkClientHttpRequestFactory(
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(properties.getTimeoutInSeconds()))
                        .build()
        );
    }
}