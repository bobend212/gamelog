package com.matkon.gamelog.infrastructure.game.integration.rawg;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class RawgGameInfoDto {
    private Long id;
    private String name;
    private String background_image;
    private String released;
}
