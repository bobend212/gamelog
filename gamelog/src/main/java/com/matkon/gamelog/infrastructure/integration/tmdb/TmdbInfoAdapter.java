package com.matkon.gamelog.infrastructure.integration.tmdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.common.exception.ItemNotFoundException;
import com.matkon.gamelog.domain.movie.model.Movie;
import com.matkon.gamelog.domain.movie.ports.out.MovieInfoPort;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import com.matkon.gamelog.domain.tvshow.ports.out.TVShowInfoPort;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieInfoDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieSaveDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbMovieSearchResponse;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbTVShowSaveDto;
import com.matkon.gamelog.infrastructure.integration.tmdb.dto.TmdbTVShowSearchResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
class TmdbInfoAdapter implements MovieInfoPort, TVShowInfoPort {

    RestClient restClient;
    TmdbMapper tmdbMapper;

    public TmdbInfoAdapter(@Qualifier("tmdbRestClient") RestClient restClient, TmdbMapper tmdbMapper) {
        this.restClient = restClient;
        this.tmdbMapper = tmdbMapper;
    }

    @Override
    public List<Movie> searchMovies(String query) {
        TmdbMovieSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .body(TmdbMovieSearchResponse.class);

        if (response == null || response.getResults().isEmpty()) {
            throw new ItemNotFoundException("No movies found matching the query: '%s'".formatted(query));
        }

        return response.getResults().stream()
                .map(tmdbMapper::mapTmdbMovieSearchInfoDtoToMovie)
                .toList();
    }

    @Override
    public Movie getSingleMovieDetails(Movie movie) {
        TmdbMovieInfoDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + movie.getTmdbId())
                        .build())
                .retrieve()
                .body(TmdbMovieInfoDto.class);

        if (response == null) {
            throw new ItemNotFoundException(
                    "Movie with following ID '%s' does not exist in the API".formatted(movie.getTmdbId()));
        }

        return tmdbMapper.mapTmdbMovieInfoDtoToMovie(movie, response);
    }

    @Override
    public Movie getSaveMovieDetails(Long tmdbId) {
        TmdbMovieSaveDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tmdbId)
                        .build())
                .retrieve()
                .body(TmdbMovieSaveDto.class);

        if (response == null) {
            throw new ItemNotFoundException(
                    "Movie with following ID '%s' does not exist in the API".formatted(tmdbId));
        }

        return tmdbMapper.mapTmdbMovieSaveDtoToMovie(response);
    }

    @Override
    public LocalDate getReleaseDatePL(Long tmdbId) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tmdbId + "/release_dates")
                        .build())
                .retrieve()
                .body(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root;
        try {
            root = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode results = root.path("results");
        if (results.isMissingNode() || !results.isArray()) {
            return null;
        }

        JsonNode plNode = null;
        for (JsonNode countryNode : results) {
            if ("PL".equals(countryNode.path("iso_3166_1").asText())) {
                plNode = countryNode;
                break;
            }
        }

        if (plNode == null) {
            return null;
        }

        JsonNode releaseDates = plNode.path("release_dates");
        JsonNode releaseDateNode = null;
        if (releaseDates.isArray()) {
            for (JsonNode rd : releaseDates) {
                if (rd.path("type").asInt(-1) == 3) {
                    releaseDateNode = rd;
                    break;
                }
            }
        }

        if (releaseDateNode == null) {
            return null;
        }

        String releaseDateStr = releaseDateNode.path("release_date").asText(null);
        if (releaseDateStr == null) {
            return null;
        }

        return OffsetDateTime.parse(releaseDateStr).toLocalDate();
    }

    @Override
    public Set<String> getMovieVodProviders(Long tmdbId) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tmdbId + "/watch/providers")
                        .build())
                .retrieve()
                .body(String.class);

        return extractVodProviders(response);
    }

    private static Set<String> extractVodProviders(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode providersForCountry = root.path("results").path("PL");

        Set<String> providerNames = new HashSet<>();
        if (providersForCountry != null && providersForCountry.has("flatrate")) {
            JsonNode flatrateArray = providersForCountry.get("flatrate");
            if (flatrateArray.isArray()) {
                for (JsonNode provider : flatrateArray) {
                    String name = provider.path("provider_name").asText();
                    String logoPath = provider.path("logo_path").asText();
                    if (!name.isEmpty() && !logoPath.isEmpty()) {
                        providerNames.add(MessageFormat.format("{0};{1}", logoPath, name));
                    }
                }
            }
        }

        return providerNames;
    }

    @Override
    public List<TVShow> searchTVShows(String query) {
        TmdbTVShowSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/tv")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .body(TmdbTVShowSearchResponse.class);

        if (response == null || response.getResults().isEmpty()) {
            throw new ItemNotFoundException("Nothing found matching the query: '%s'".formatted(query));
        }

        return response.getResults().stream()
                .map(tmdbMapper::mapTmdbTVShowSearchInfoDtoToTVShow)
                .toList();
    }

    @Override
    public TVShow getSaveTVShowDetails(Long tmdbId) {
        TmdbTVShowSaveDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/" + tmdbId)
                        .build())
                .retrieve()
                .body(TmdbTVShowSaveDto.class);

        if (response == null) {
            throw new ItemNotFoundException(
                    "TV Show with following ID '%s' does not exist in the API".formatted(tmdbId));
        }

        return tmdbMapper.mapTmdbTVShowSaveDtoToTVShow(response);
    }

    @Override
    public Set<String> getTVShowVodProviders(Long tmdbId) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/" + tmdbId + "/watch/providers")
                        .build())
                .retrieve()
                .body(String.class);

        return extractVodProviders(response);
    }
}