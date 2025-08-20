package com.matkon.gamelog.data.tvseries;

import java.time.LocalDate;

public record TVSeriesSearchDto(
        Long tmdbId,
        String name,
        LocalDate firstAirDate,
        String poster_path
) {}
