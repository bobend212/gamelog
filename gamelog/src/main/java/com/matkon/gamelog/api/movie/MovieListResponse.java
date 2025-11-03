package com.matkon.gamelog.api.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieListResponse {

    private Long id;
    private String title;
    private String originalTitle;
    private LocalDate releaseDate;
    private String status;
    private String poster;
    private List<String> genres;
    private List<String> vodProviders;
}