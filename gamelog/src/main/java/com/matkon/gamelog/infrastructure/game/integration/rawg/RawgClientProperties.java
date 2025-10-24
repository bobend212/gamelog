package com.matkon.gamelog.infrastructure.game.integration.rawg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rawg.api")
class RawgClientProperties {
    private String key;
    private String url;
    private Integer timeoutInSeconds;
}
