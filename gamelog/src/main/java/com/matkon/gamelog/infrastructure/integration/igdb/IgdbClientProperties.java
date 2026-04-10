package com.matkon.gamelog.infrastructure.integration.igdb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "igdb.api")
public class IgdbClientProperties {
    private String baseUrl;
    private String clientId;
    private Integer timeoutInSeconds;
}
