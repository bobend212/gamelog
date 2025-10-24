package com.matkon.gamelog.legacy.data.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieSearchResultDto {
    private Long id;
    private String poster_path;
    private String title;
    private String release_date;
}