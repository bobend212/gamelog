package com.matkon.gamelog.data.game.dto;

import java.time.LocalDate;

public record GameSearchResultDto(
        Long rawgId,
        String title,
        String imageUrl,
        LocalDate releaseDate
) {}
