package com.matkon.gamelog.data.game.dto.rawg;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RawgSearchResponse {
    private List<RawgSearchResult> results;
}
