package com.matkon.gamelog.legacy.data.movie.dto;

import com.matkon.gamelog.legacy.data.movie.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieListDto {
    private Long id;
    private String title;
    private String originalTitle;
    private LocalDate releaseDate;
    private String status;
    private String poster;
    private List<String> genres;
    private List<String> vodProviders;

    public static MovieListDto fromEntity(Movie movie) {
        MovieListDto dto = new MovieListDto();
        dto.id = movie.getId();
        dto.title = movie.getTitle();
        dto.originalTitle = movie.getOriginalTitle();
        dto.releaseDate = movie.getReleaseDate();
        dto.status = movie.getStatus();
        dto.poster = movie.getPoster();
        dto.genres = movie.getGenres();
        dto.vodProviders = movie.getVodProviders();

        return dto;
    }
}