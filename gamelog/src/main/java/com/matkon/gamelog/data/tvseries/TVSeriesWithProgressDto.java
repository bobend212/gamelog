package com.matkon.gamelog.data.tvseries;

import java.util.List;

public record TVSeriesWithProgressDto(
        Long id,
        Long tmdbId,
        String name,
        MyStatus myStatus,
        List<SeasonProgressDto> seasons
) {}

