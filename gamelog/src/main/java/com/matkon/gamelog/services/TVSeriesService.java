package com.matkon.gamelog.services;

import com.matkon.gamelog.data.tvseries.Season;
import com.matkon.gamelog.data.tvseries.TVSeries;
import com.matkon.gamelog.data.tvseries.TVSeriesDto;
import com.matkon.gamelog.data.tvseries.TVSeriesListDto;
import com.matkon.gamelog.data.tvseries.TVSeriesSearchDto;
import com.matkon.gamelog.data.tvseries.TrackingType;
import com.matkon.gamelog.repos.SeasonRepository;
import com.matkon.gamelog.repos.TVSeriesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TVSeriesService
{

    private final TVSeriesRepository seriesRepo;
    private final SeasonRepository seasonRepo;
    private final WebClient webClient;
    private final String tmdbApiKey;

    public TVSeriesService(TVSeriesRepository seriesRepo,
                           SeasonRepository seasonRepo,
                           @Value("${tmdb.api.key}") String tmdbApiKey)
    {
        this.seriesRepo = seriesRepo;
        this.seasonRepo = seasonRepo;
        this.tmdbApiKey = tmdbApiKey;
        this.webClient = WebClient.create("https://api.themoviedb.org/3");
    }

    // ---- Search ----
    public List<TVSeriesSearchDto> searchByQuery(String query, String lang, int page)
    {
        TMDBSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/tv")
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("query", query)
                        .queryParam("language", lang != null ? lang : "en-US")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(TMDBSearchResponse.class)
                .block();

        if (response == null || response.results == null) return List.of();

        return response.results.stream()
                .map(r -> new TVSeriesSearchDto(r.id, r.name, parseDate(r.first_air_date), r.poster_path))
                .toList();
    }

    // ---- Add series ----
    @Transactional
    public void saveSeries(Long tmdbId, TrackingType trackingType)
    {
        Optional<TVSeries> existing = seriesRepo.findByTmdbId(tmdbId);
        if (existing.isPresent()) return;

        TMDBSeries details = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}")
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("language", "en-US")
                        .build(tmdbId))
                .retrieve()
                .bodyToMono(TMDBSeries.class)
                .block();

        if (details == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Series not found in TMDB API");
        }

        TVSeries tvSeries = new TVSeries();
        tvSeries.setTmdbId(details.id);
        tvSeries.setName(details.name);
        tvSeries.setFirst_air_date(details.first_air_date);
        tvSeries.setIn_production(details.in_production);
        tvSeries.setNumber_of_episodes(details.number_of_episodes);
        tvSeries.setNumber_of_seasons(details.number_of_seasons);
        tvSeries.setPoster_path(details.poster_path);
        tvSeries.setLast_air_date(details.last_air_date);
        tvSeries.setStatus(details.status);
        tvSeries.setTrackingType(trackingType);

        details.seasons.forEach(s -> {
            Season season = new Season();
            season.setName(s.name);
            season.setSeason_number(s.season_number);
            season.setAir_date(s.air_date);
            season.setEpisode_count(s.episode_count);
            season.setWatchedCount(0);
            tvSeries.addSeason(season);
        });

        seriesRepo.save(tvSeries);
    }

    // ---- Update season counts ----
    @Transactional
    public void incrementWatchedCount(Long seasonId)
    {
        Season season = seasonRepo.findById(seasonId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Season not found"));

        if (season.getWatchedCount() < season.getEpisode_count()) {
            season.setWatchedCount(season.getWatchedCount() + 1);
            seasonRepo.save(season);
        }
    }

    @Transactional
    public void updateWatchedCount(Long seasonId, int watchedCount)
    {
        Season season = seasonRepo.findById(seasonId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Season not found"));

        if (watchedCount >= 0 && watchedCount <= season.getEpisode_count()) {
            season.setWatchedCount(watchedCount);
            seasonRepo.save(season);
        }
    }

    // ---- Get all series ----
    public List<TVSeriesListDto> getAllSeries()
    {
        return seriesRepo.findAll().stream().map(TVSeriesListDto::fromEntity).toList();
    }

    // ---- Get series by id ----
    public TVSeriesDto getSeriesById(Long id)
    {
        Optional<TVSeries> seriesOpt = seriesRepo.findById(id);
        TVSeries series = seriesOpt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "TV series not found"));
        return TVSeriesDto.fromEntity(series);
    }

    // ---- Update trackingType ----
    @Transactional
    public void updateTrackingType(Long seriesId, TrackingType trackingType)
    {
        TVSeries series = seriesRepo.findById(seriesId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "TV series not found"));
        series.setTrackingType(trackingType);
        seriesRepo.save(series);
    }

    // ---- Utility Mappers ----
    private LocalDate parseDate(String date)
    {
        return (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
    }

    // ---- TMDb DTOs ----
    private static class TMDBSearchResponse
    {
        public List<TMDBSearchResult> results;
    }

    private static class TMDBSearchResult
    {
        public Long id;
        public String name;
        public String poster_path;
        public String first_air_date;
    }

    private static class TMDBSeries
    {
        public Long id;
        public String name;
        public String poster_path;
        public List<TMDBSeason> seasons;
        public String status;
        public LocalDate first_air_date;
        public boolean in_production;
        public int number_of_episodes;
        public int number_of_seasons;
        public LocalDate last_air_date;
    }

    private static class TMDBSeason
    {
        public String name;
        public int season_number;
        public LocalDate air_date;
        public int episode_count;
    }
}
