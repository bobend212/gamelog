package com.matkon.gamelog.services;

import com.matkon.gamelog.data.tvseries.EpisodeDto;
import com.matkon.gamelog.data.tvseries.MyStatus;
import com.matkon.gamelog.data.tvseries.Season;
import com.matkon.gamelog.data.tvseries.SeasonProgressDto;
import com.matkon.gamelog.data.tvseries.TVSeries;
import com.matkon.gamelog.data.tvseries.TVSeriesSearchDto;
import com.matkon.gamelog.data.tvseries.TVSeriesWithProgressDto;
import com.matkon.gamelog.repos.SeasonRepository;
import com.matkon.gamelog.repos.TVSeriesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<TVSeriesSearchDto> searchTVSeries(String query, String lang, int page)
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
                .map(r -> new TVSeriesSearchDto(r.id, r.name, parseDate(r.first_air_date)))
                .collect(Collectors.toList());
    }

    // ---- Add minimal series ----
    public void addSeriesByTmdbId(Long tmdbId, MyStatus status)
    {
        seriesRepo.findByTmdbId(tmdbId)
                .orElseGet(() -> {
                    TMDBSeriesDetails details = webClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/tv/{id}")
                                    .queryParam("api_key", tmdbApiKey)
                                    .queryParam("language", "en-US")
                                    .build(tmdbId))
                            .retrieve()
                            .bodyToMono(TMDBSeriesDetails.class)
                            .block();

                    TVSeries tvSeries = new TVSeries();
                    tvSeries.setTmdbId(details.id);
                    tvSeries.setName(details.name);
                    tvSeries.setMyStatus(status);

                    // Load seasons with total episode count
                    details.seasons.forEach(s -> {
                        Season season = new Season();
                        season.setTmdbId(s.id);
                        season.setName(s.name);
                        season.setSeasonNumber(s.season_number);
                        season.setTotalEpisodes(s.episode_count);
                        season.setWatchedCount(0);
                        tvSeries.addSeason(season);
                    });

                    seriesRepo.save(tvSeries);
                    return tvSeries;
                });
    }

    // ---- Progress ----
    public SeasonProgressDto getSeasonProgress(Long seriesId, Long seasonId)
    {
        TVSeries series = seriesRepo.findById(seriesId)
                .orElseThrow(() -> new RuntimeException("Series not found"));
        Season season = seasonRepo.findById(seasonId)
                .orElseThrow(() -> new RuntimeException("Season not found"));

        TMDBSeasonDetails seasonDetails = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/season/{num}")
                        .queryParam("api_key", tmdbApiKey)
                        .build(series.getTmdbId(), season.getSeasonNumber()))
                .retrieve()
                .bodyToMono(TMDBSeasonDetails.class)
                .block();

        EpisodeDto lastWatched = null;
        EpisodeDto nextToWatch = null;

        if (season.getWatchedCount() > 0) {
            TMDBEpisode lastEp = seasonDetails.episodes.get(season.getWatchedCount() - 1);
            lastWatched = new EpisodeDto(lastEp.episode_number, lastEp.name, lastEp.air_date);
        }
        if (season.getWatchedCount() < season.getTotalEpisodes()) {
            TMDBEpisode nextEp = seasonDetails.episodes.get(season.getWatchedCount());
            nextToWatch = new EpisodeDto(nextEp.episode_number, nextEp.name, nextEp.air_date);
        }

        double percentage = 0.0;
        if (season.getTotalEpisodes() > 0) {
            percentage = (season.getWatchedCount() * 100.0) / season.getTotalEpisodes();
        }


        return new SeasonProgressDto(
                season.getId(),
                season.getTmdbId(),
                season.getSeasonNumber(),
                season.getName(),
                season.getTotalEpisodes(),
                season.getWatchedCount(),
                lastWatched,
                nextToWatch,
                percentage
        );
    }

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
        public String first_air_date;
    }

    private static class TMDBSeriesDetails
    {
        public Long id;
        public String name;
        public List<TMDBSeasonMeta> seasons;
    }

    private static class TMDBSeasonMeta
    {
        public Long id;
        public String name;
        public int season_number;
        public int episode_count;
    }

    private static class TMDBSeasonDetails
    {
        public List<TMDBEpisode> episodes;
    }

    private static class TMDBEpisode
    {
        public int episode_number;
        public String name;
        public String air_date;
    }

    public void incrementWatchedCount(Long seasonId)
    {
        Season season = seasonRepo.findById(seasonId)
                .orElseThrow(() -> new RuntimeException("Season not found"));

        if (season.getWatchedCount() < season.getTotalEpisodes()) {
            season.setWatchedCount(season.getWatchedCount() + 1);
            seasonRepo.save(season);
        }
    }

    public void updateWatchedCount(Long seasonId, int watchedCount)
    {
        Season season = seasonRepo.findById(seasonId)
                .orElseThrow(() -> new RuntimeException("Season not found"));

        if (watchedCount >= 0 && watchedCount <= season.getTotalEpisodes()) {
            season.setWatchedCount(watchedCount);
            seasonRepo.save(season);
        }
    }

    public List<TVSeriesWithProgressDto> getAllSeriesWithProgress()
    {
        List<TVSeries> seriesList = seriesRepo.findAll();
        return seriesList.stream()
                .map(series -> {
                    List<SeasonProgressDto> seasonProgressList = series.getSeasons().stream()
                            .map(season -> buildSeasonProgress(series.getTmdbId(), season))
                            .toList();
                    return new TVSeriesWithProgressDto(
                            series.getId(),
                            series.getTmdbId(),
                            series.getName(),
                            series.getMyStatus(),
                            seasonProgressList
                    );
                })
                .toList();
    }

    private SeasonProgressDto buildSeasonProgress(Long seriesTmdbId, Season season)
    {
        TMDBSeasonDetails seasonDetails = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}/season/{num}")
                        .queryParam("api_key", tmdbApiKey)
                        .build(seriesTmdbId, season.getSeasonNumber()))
                .retrieve()
                .bodyToMono(TMDBSeasonDetails.class)
                .block();

        EpisodeDto lastWatched = null;
        EpisodeDto nextToWatch = null;
        if (season.getWatchedCount() > 0 && season.getWatchedCount() <= seasonDetails.episodes.size()) {
            TMDBEpisode lastEp = seasonDetails.episodes.get(season.getWatchedCount() - 1);
            lastWatched = new EpisodeDto(lastEp.episode_number, lastEp.name, lastEp.air_date);
        }
        if (season.getWatchedCount() < season.getTotalEpisodes()) {
            TMDBEpisode nextEp = seasonDetails.episodes.get(season.getWatchedCount());
            nextToWatch = new EpisodeDto(nextEp.episode_number, nextEp.name, nextEp.air_date);
        }

        double perc = 0.0;
        if (season.getTotalEpisodes() > 0) {
            perc = (season.getWatchedCount() * 100.0) / season.getTotalEpisodes();
        }

        return new SeasonProgressDto(
                season.getId(),
                season.getTmdbId(),
                season.getSeasonNumber(),
                season.getName(),
                season.getTotalEpisodes(),
                season.getWatchedCount(),
                lastWatched,
                nextToWatch,
                perc
        );
    }
}