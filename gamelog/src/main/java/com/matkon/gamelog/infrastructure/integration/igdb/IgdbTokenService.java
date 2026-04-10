package com.matkon.gamelog.infrastructure.integration.igdb;

import com.matkon.gamelog.infrastructure.integration.igdb.dto.TwitchTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(IgdbAuthProperties.class)
public class IgdbTokenService {

    private final RestClient.Builder builder;
    private final IgdbAuthProperties properties;

    private String accessToken;
    private Instant expiresAt;

    public synchronized String getValidToken() {
        if (accessToken == null || isExpired()) {
            refreshToken();
        }
        return accessToken;
    }

    private boolean isExpired() {
        return expiresAt == null || Instant.now().isAfter(expiresAt.minusSeconds(60));
    }

    private void refreshToken() {
        RestClient client = builder.build();
        TwitchTokenResponse response = client.post()
                .uri(properties.getTokenUrl() +
                        "?client_id=" + properties.getClientId() +
                        "&client_secret=" + properties.getClientSecret() +
                        "&grant_type=client_credentials")
                .retrieve()
                .body(TwitchTokenResponse.class);

        this.accessToken = response.getAccess_token();
        this.expiresAt = Instant.now().plusSeconds(response.getExpires_in());
    }
}
