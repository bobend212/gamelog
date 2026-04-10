package com.matkon.gamelog.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class GameDetails {

    private String storyline;
    private String summary;
    private LocalDate igdbLastUpdated;
    private String additionalImageUrl;
    private String igdbUrl;
}
