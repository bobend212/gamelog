package com.matkon.gamelog.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate completedAt;
    private Long rawgId;
    private String title;
    private LocalDate releaseDate;
    private String imageUrl;
}
