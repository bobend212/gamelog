package com.matkon.gamelog.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class GameDetails {

    private String description;
    private Integer metacritic;
    private LocalDateTime updatedRawg;
    private String websiteUrl;
    private String metacriticUrl;
    private String additionalImageUrl;
    private List<GamePlatformDto> platforms;
}
