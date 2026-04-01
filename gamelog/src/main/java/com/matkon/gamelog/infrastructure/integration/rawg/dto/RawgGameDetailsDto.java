package com.matkon.gamelog.infrastructure.integration.rawg.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RawgGameDetailsDto {

    private String description;
    private Integer metacritic;
    private String updated;
    private String website;
    private String metacritic_url;
    private String background_image_additional;
    private List<RawgGamePlatformDto> platforms;
}
