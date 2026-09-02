package com.matkon.gamelog.infrastructure.integration.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IgdbFinalReleaseDate {

    private Long date;
    private IgdbReleaseDateStatus status;
    @JsonProperty("release_region")
    private Long releaseRegion;

    public Long getDate() {
        return date;
    }

    public IgdbReleaseDateStatus getStatus() {
        return status;
    }

    public Long getReleaseRegion() {
        return releaseRegion;
    }
}
