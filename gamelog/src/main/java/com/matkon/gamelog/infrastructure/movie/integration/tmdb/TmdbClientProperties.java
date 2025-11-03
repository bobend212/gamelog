package com.matkon.gamelog.infrastructure.movie.integration.tmdb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tmdb.api")
public class TmdbClientProperties {

    private String key;
    private String url;
    private Integer timeoutInSeconds;
}
