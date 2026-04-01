package com.matkon.gamelog.infrastructure.integration.rawg.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawgGamePlatformDto {

    private RawgPlatformDto platform;
    private String released_at;
}
