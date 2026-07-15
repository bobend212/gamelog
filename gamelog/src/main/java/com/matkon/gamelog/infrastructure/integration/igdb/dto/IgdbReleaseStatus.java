package com.matkon.gamelog.infrastructure.integration.igdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbReleaseStatus {
    private String name;
}
