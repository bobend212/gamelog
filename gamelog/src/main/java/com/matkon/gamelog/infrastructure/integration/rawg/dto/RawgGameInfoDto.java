package com.matkon.gamelog.infrastructure.integration.rawg.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawgGameInfoDto {
    private Long id;
    private String name;
    private String background_image;
    private String released;
}
