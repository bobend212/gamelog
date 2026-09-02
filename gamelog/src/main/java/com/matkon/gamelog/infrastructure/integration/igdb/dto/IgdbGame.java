package com.matkon.gamelog.infrastructure.integration.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record IgdbGame(
        Long id,
        String name,

        @JsonProperty("release_dates")
        List<IgdbFinalReleaseDate> releaseDates,
        IgdbCover cover
) {
}