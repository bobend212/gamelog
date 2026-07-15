package com.matkon.gamelog.infrastructure.integration.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IgdbGameDetails {

    private String storyline;
    private String summary;

    @JsonProperty("updated_at")
    private Long updatedAt;

    private String url;

    private List<IgdbScreenshot> screenshots;

    @JsonProperty("release_dates")
    private List<IgdbReleaseDate> releaseDates;
}