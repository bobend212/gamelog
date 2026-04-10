package com.matkon.gamelog.infrastructure.integration.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IgdbGame(
        Long id,
        String name,
        @JsonProperty("first_release_date")
        Long firstReleaseDate,
        IgdbCover cover
) {
}