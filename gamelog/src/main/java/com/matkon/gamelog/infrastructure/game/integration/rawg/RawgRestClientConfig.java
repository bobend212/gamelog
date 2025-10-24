package com.matkon.gamelog.infrastructure.game.integration.rawg;

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
@EnableConfigurationProperties(RawgClientProperties.class)
class RawgRestClientConfig {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String RAWG_API_ERROR_MESSAGE = "RAWG API server error: ";

    @Bean
    @Qualifier("rawgRestClient")
    RestClient rawgRestClient(RestClient.Builder builder, RawgClientProperties properties) {
        String baseUrlWithApiKey = properties.getUrl() + "?key=" + properties.getKey();

        return builder
                .baseUrl(baseUrlWithApiKey)
                .defaultHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .requestFactory(createRequestFactory(properties))
                .defaultStatusHandler(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new RuntimeException(RAWG_API_ERROR_MESSAGE + response.getStatusCode());
                        }
                )
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory(RawgClientProperties properties) {
        return new JdkClientHttpRequestFactory(
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(properties.getTimeoutInSeconds()))
                        .build()
        );
    }
}
