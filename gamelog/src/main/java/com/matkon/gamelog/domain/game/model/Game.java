package com.matkon.gamelog.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Game {

    private Long id;
    private GameStatus status;
    private Double rating;
    private String notes;
    private String platform;
    private boolean favourite;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private LocalDate completedAt;
    private Long rawgId;
    private Long igdbId;
    private String title;
    private LocalDate releaseDate;
    private String imageUrl;
}
