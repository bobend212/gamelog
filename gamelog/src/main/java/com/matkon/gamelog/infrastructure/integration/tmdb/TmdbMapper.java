package com.matkon.gamelog.infrastructure.integration.tmdb;

import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.tvshow.model.Season;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieInfoDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieSaveDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieSaveGenreDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieSearchInfoDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbSeasonSaveDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbTVShowSaveDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbTVShowSearchInfoDto;
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

    @Mapping(source = "id", target = "tmdbId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "poster_path", target = "posterPath")
    @Mapping(source = "first_air_date", target = "firstAirDate", qualifiedByName = "mapReleaseDate")
    TVShow mapTmdbTVShowSearchInfoDtoToTVShow(TmdbTVShowSearchInfoDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "tmdbId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "poster_path", target = "posterPath")
    @Mapping(source = "first_air_date", target = "firstAirDate", qualifiedByName = "mapReleaseDate")
    @Mapping(source = "number_of_episodes", target = "numberOfEpisodes")
    @Mapping(source = "number_of_seasons", target = "numberOfSeasons")
    @Mapping(source = "last_air_date", target = "lastAirDate", qualifiedByName = "mapReleaseDate")
    TVShow mapTmdbTVShowSaveDtoToTVShow(TmdbTVShowSaveDto tmdbTVShowSaveDto);

    @Mapping(source = "season_number", target = "seasonNumber")
    @Mapping(source = "air_date", target = "airDate", qualifiedByName = "mapReleaseDate")
    @Mapping(source = "episode_count", target = "episodeCount")
    Season mapTmdbSeasonSaveDtoToSeason(TmdbSeasonSaveDto tmdbSeasonSaveDto);
}