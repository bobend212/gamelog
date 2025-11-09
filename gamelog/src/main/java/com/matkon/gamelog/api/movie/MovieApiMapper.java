package com.matkon.gamelog.api.movie;

import com.matkon.gamelog.domain.movie.model.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieApiMapper {

    MovieListResponse mapMovieToMovieListResponse(Movie movie);

    @Mapping(source = "releaseDatePL", target = "releaseDatePL")
    MovieResponse mapMovieToMovieResponse(Movie movie);

    MovieSearchResponse mapMovieToMovieSearchResponse(Movie movie);
}
