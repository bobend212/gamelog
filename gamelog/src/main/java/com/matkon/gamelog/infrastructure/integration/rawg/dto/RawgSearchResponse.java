package com.matkon.gamelog.infrastructure.integration.rawg.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RawgSearchResponse {
    private List<RawgGameInfoDto> results;
}
