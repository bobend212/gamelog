package com.matkon.gamelog.infrastructure.integration.igdb;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(IgdbClientProperties.class)
public class IgdbRestClientConfig {

    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CLIENT_ID = "Client-ID";
    private static final String BEARER_ = "Bearer ";

    @Bean
    @Qualifier("igdbRestClient")
    RestClient igdbRestClient(RestClient.Builder builder, IgdbClientProperties properties, IgdbTokenService tokenService) {
        return builder
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .requestFactory(createRequestFactory(properties))
                .requestInterceptor((request, body, execution) -> {

                    request.getHeaders().set(CLIENT_ID, properties.getClientId());
                    request.getHeaders().set(AUTHORIZATION, BEARER_ + tokenService.getValidToken());

                    return execution.execute(request, body);
                })
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory(IgdbClientProperties properties) {
        return new JdkClientHttpRequestFactory(
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(properties.getTimeoutInSeconds()))
                        .build()
        );
    }
}
