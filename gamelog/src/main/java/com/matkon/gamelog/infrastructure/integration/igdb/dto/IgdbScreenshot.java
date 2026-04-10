package com.matkon.gamelog.infrastructure.integration.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IgdbScreenshot(
        @JsonProperty("image_id")
        String imageId
) {
}
