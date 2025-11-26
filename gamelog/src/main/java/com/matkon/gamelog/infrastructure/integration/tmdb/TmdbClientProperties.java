package com.matkon.gamelog.infrastructure.integration.tmdb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tmdb.api")
class TmdbClientProperties {

    private String key;
    private String url;
    private Integer timeoutInSeconds;
}
