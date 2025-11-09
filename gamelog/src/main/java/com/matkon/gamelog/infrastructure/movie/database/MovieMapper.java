package com.matkon.gamelog.infrastructure.movie.database;

import com.matkon.gamelog.domain.movie.model.Movie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie mapMovieEntityToMovie(MovieEntity movieEntity);

    MovieEntity mapMovieToMovieEntity(Movie movie);
}
