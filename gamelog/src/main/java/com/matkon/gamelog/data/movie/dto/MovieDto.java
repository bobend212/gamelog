package com.matkon.gamelog.data.movie.dto;

import com.matkon.gamelog.data.movie.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieDto {
    private Long id;
    private Long tmdbId;
    private String title;
    private String originalTitle;
    private String overview; // API
    private LocalDate releaseDate;
    private LocalDate releaseDatePL; // API
    private int runtime; // API
    private String status;
    private String poster;
    private List<String> genres;
    private List<String> vodProviders;
    private LocalDateTime createdAt;

    public static Movie toEntity(MovieDto dto) {
        Movie entity = new Movie();
        entity.setId(dto.getId());
        entity.setTmdbId(dto.getTmdbId());
        entity.setTitle(dto.getTitle());
        entity.setOriginalTitle(dto.getOriginalTitle());
        entity.setReleaseDate(dto.getReleaseDate());
        entity.setStatus(dto.getStatus());
        entity.setPoster(dto.getPoster());
        entity.setGenres(dto.getGenres());
        entity.setVodProviders(dto.getVodProviders());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}