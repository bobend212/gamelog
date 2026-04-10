package com.matkon.gamelog.api.game;

import java.time.LocalDate;

record GameSearchResponse(
        Long externalId,
        String title,
        String imageUrl,
        LocalDate releaseDate
) {}
