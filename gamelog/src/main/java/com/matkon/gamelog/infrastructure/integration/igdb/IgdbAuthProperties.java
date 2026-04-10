package com.matkon.gamelog.infrastructure.integration.igdb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "igdb.auth")
public class IgdbAuthProperties {
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
}
