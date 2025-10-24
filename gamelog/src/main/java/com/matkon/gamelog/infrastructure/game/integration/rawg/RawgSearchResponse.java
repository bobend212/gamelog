package com.matkon.gamelog.infrastructure.game.integration.rawg;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
class RawgSearchResponse {
    private List<RawgGameInfoDto> results;
}
