package com.matkon.gamelog.infrastructure.integration.tmdb;

import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieInfoDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieSaveDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieSaveGenreDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieSearchInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
interface TmdbMapper {

    @Mapping(target = "overview", source = "tmdbInfo.overview")
    @Mapping(target = "runtime", source = "tmdbInfo.runtime")
    Movie mapTmdbMovieInfoDtoToMovie(Movie movie, TmdbMovieInfoDto tmdbInfo);

    @Mapping(source = "id", target = "tmdbId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "poster_path", target = "poster")
    @Mapping(source = "release_date", target = "releaseDate", qualifiedByName = "mapReleaseDate")
    Movie mapTmdbMovieSearchInfoDtoToMovie(TmdbMovieSearchInfoDto dto);

    @Named("mapReleaseDate")
    default LocalDate mapReleaseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "tmdbId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "original_title", target = "originalTitle")
    @Mapping(source = "poster_path", target = "poster")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "release_date", target = "releaseDate")
    Movie mapTmdbMovieSaveDtoToMovie(TmdbMovieSaveDto tmdbMovieSaveDto);

    default String map(TmdbMovieSaveGenreDto genreDto) {
        return genreDto.getName();
    }
}