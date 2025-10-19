package com.matkon.gamelog.legacy.data.tvshow.dto;

import java.time.LocalDate;

public record TVShowSearchResultDto(
        Long tmdbId,
        String name,
        LocalDate firstAirDate,
        String poster_path
) {}
