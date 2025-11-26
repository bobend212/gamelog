package com.matkon.gamelog.api.tvshow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TVShowSearchResponse {

    private Long tmdbId;
    private String name;
    private LocalDate firstAirDate;
    private String posterPath;
}