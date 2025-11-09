package com.matkon.gamelog.api.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSearchResponse {

    private Long tmdbId;
    private String poster;
    private String title;
    private String releaseDate;
}