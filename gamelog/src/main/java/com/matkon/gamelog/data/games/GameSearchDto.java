package com.matkon.gamelog.data.games;

import java.time.LocalDate;

public record GameSearchDto(
        Long rawgId,
        String title,
        String imageUrl,
        LocalDate releaseDate
) {}
