package com.matkon.gamelog.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.sync.ChangeDetail;
import com.matkon.gamelog.data.sync.FieldChange;
import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.data.sync.SyncUtils;
import com.matkon.gamelog.data.tvseries.Season;
import com.matkon.gamelog.data.tvseries.SeasonDto;
import com.matkon.gamelog.data.tvseries.TVSeries;
import com.matkon.gamelog.data.tvseries.TVSeriesDto;
import com.matkon.gamelog.data.tvseries.TVSeriesListDto;
import com.matkon.gamelog.data.tvseries.TVSeriesSaveResultDto;
import com.matkon.gamelog.data.tvseries.TVSeriesSearchDto;
import com.matkon.gamelog.data.tvseries.TrackingType;
import com.matkon.gamelog.repos.SeasonRepository;
import com.matkon.gamelog.repos.TVSeriesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public TVSeriesSaveResultDto saveSeries(Long tmdbId, TrackingType trackingType)
    {
        Optional<TVSeries> existing = seriesRepo.findByTmdbId(tmdbId);
        if (existing.isPresent()) {
            return new TVSeriesSaveResultDto(
                    existing.get().getId(),
                    true,
                    "TV Series already exist in the database"
            );
        }

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
        tvSeries.setNumber_of_episodes(details.number_of_episodes);
        tvSeries.setNumber_of_seasons(details.number_of_seasons);
        tvSeries.setPoster_path(details.poster_path);
        tvSeries.setLast_air_date(details.last_air_date);
        tvSeries.setStatus(details.status);
        tvSeries.setTrackingType(trackingType);
        tvSeries.setUpdatedAt(LocalDateTime.now());

        if (tvSeries.getTrackingType() != TrackingType.WISHLIST) {
            details.seasons.forEach(s -> {
                if (s.season_number == 0 || s.episode_count == 0) {
                    return; // skip specials or no episodes provided
                }
                Season season = new Season();
                season.setName(s.name);
                season.setSeason_number(s.season_number);
                season.setAir_date(s.air_date);
                season.setEpisode_count(s.episode_count);
                season.setWatchedCount(0);
                tvSeries.addSeason(season);
            });
        }

        try {
            tvSeries.setVodProviders(getVodProviders(webClient, tmdbApiKey, tmdbId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        seriesRepo.save(tvSeries);
        return new TVSeriesSaveResultDto(
                tvSeries.getId(),
                false,
                "TV Series added successfully"
        );
    }

    // ---- Update season counts ----
    @Transactional
    public void incrementWatchedCount(Long seasonId)
    {
        Season season = seasonRepo.findById(seasonId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Season not found"));

        if (season.getWatchedCount() < season.getEpisode_count()) {
            season.setWatchedCount(season.getWatchedCount() + 1);
            season.getSeries().setUpdatedAt(LocalDateTime.now());
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
            season.getSeries().setUpdatedAt(LocalDateTime.now());
            seasonRepo.save(season);
        }
    }

    // ---- Get all series ----
    public List<TVSeriesListDto> getAllSeries()
    {
        return seriesRepo.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"))
                .stream().map(TVSeriesListDto::fromEntity).toList();
    }

    // ---- Get series by id ----
    public TVSeriesDto getSeriesById(Long id)
    {
        Optional<TVSeries> seriesOpt = seriesRepo.findById(id);
        TVSeries series = seriesOpt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "TV series not found"));

        if (series.getSeasons() != null) {
            series.getSeasons().sort(Comparator.comparing(Season::getSeason_number));
        }

        return TVSeriesDto.fromEntity(series);
    }

    public List<TVSeriesListDto> getAllSeriesByTrackingType(TrackingType trackingType)
    {
        return seriesRepo.findByTrackingType(trackingType, Sort.by(Sort.Direction.DESC, "updatedAt"))
                .stream().map(TVSeriesListDto::fromEntity).toList();
    }

    // ---- Update trackingType ----
    @Transactional
    public void updateTrackingType(Long seriesId, TrackingType trackingType)
    {
        TVSeries series = seriesRepo.findById(seriesId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "TV series not found"));
        series.setTrackingType(trackingType);
        series.setUpdatedAt(LocalDateTime.now());
        seriesRepo.save(series);
    }

    // ---- Update season rating ----
    @Transactional
    public void rateSeason(Long seasonId, Double rating)
    {
        Season season = seasonRepo.findById(seasonId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Season not found"));
        season.setRating(rating);
        season.getSeries().setUpdatedAt(LocalDateTime.now());
        seasonRepo.save(season);
    }

    // ---- Delete series by Id ----
    @Transactional
    public void deleteSeries(Long seriesId)
    {
        if (!seriesRepo.existsById(seriesId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TV series not found");
        }
        seriesRepo.deleteById(seriesId);
    }

    public SyncResultDto syncSeries(Long id)
    {
        if (id != 0) {
            return syncLibrarySeries(id);
        } else {
            int totalSeries = 0;
            int updatedCount = 0;
            List<ChangeDetail> allChanges = new ArrayList<>();

            List<Long> ids = seriesRepo.findAll().stream()
                    .filter(series -> series.getTrackingType() != TrackingType.WISHLIST
                            && series.getTrackingType() != TrackingType.DROPPED
                            && series.getTrackingType() != TrackingType.COMPLETED)
                    .map(TVSeries::getId)
                    .toList();
            totalSeries = ids.size();

            for (Long seriesId : ids) {
                SyncResultDto result = syncLibrarySeries(seriesId);
                if (result != null) {
                    updatedCount += result.getUpdatedCount();
                    allChanges.addAll(result.getChanges());
                }
            }

            return new SyncResultDto(totalSeries, updatedCount, allChanges);
        }
    }

    @Transactional
    private SyncResultDto syncLibrarySeries(Long id)
    {
        TVSeriesDto localTVSeries = getSeriesById(id);

        int updatedCount = 0;
        List<ChangeDetail> changes = new ArrayList<>();

        TMDBSeriesToUpdate seriesToUpdate = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/{id}")
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("language", "en-US")
                        .build(localTVSeries.getTmdbId()))
                .retrieve()
                .bodyToMono(TMDBSeriesToUpdate.class)
                .block();

        if (seriesToUpdate == null) return null;

        List<FieldChange> fieldChanges = new ArrayList<>();
        boolean changed = false;

        // Name
        if (SyncUtils.areStringsDifferent(localTVSeries.getName(), seriesToUpdate.name)) {
            fieldChanges.add(new FieldChange("Name",
                    String.valueOf(localTVSeries.getName()),
                    String.valueOf(seriesToUpdate.name)));
            localTVSeries.setName(seriesToUpdate.name);
            changed = true;
        }

        // Number_Of_Episodes
        if (SyncUtils.areIntsDifferent(localTVSeries.getNumber_of_episodes(), seriesToUpdate.number_of_episodes)) {
            fieldChanges.add(new FieldChange("Number_Of_Episodes",
                    String.valueOf(localTVSeries.getNumber_of_episodes()),
                    String.valueOf(seriesToUpdate.number_of_episodes)));
            localTVSeries.setNumber_of_episodes(seriesToUpdate.number_of_episodes);
            changed = true;
        }

        // Number_Of_Seasons
        if (SyncUtils.areIntsDifferent(localTVSeries.getNumber_of_seasons(), seriesToUpdate.number_of_seasons)) {
            fieldChanges.add(new FieldChange("Number_Of_Seasons",
                    String.valueOf(localTVSeries.getNumber_of_seasons()),
                    String.valueOf(seriesToUpdate.number_of_seasons)));
            localTVSeries.setNumber_of_seasons(seriesToUpdate.number_of_seasons);
            changed = true;
        }

        // Last_Air_Date
        if (SyncUtils.areDatesDifferent(localTVSeries.getLast_air_date(), seriesToUpdate.last_air_date)) {
            fieldChanges.add(new FieldChange("Last_Air_Date",
                    String.valueOf(localTVSeries.getLast_air_date()),
                    String.valueOf(seriesToUpdate.last_air_date)));
            localTVSeries.setLast_air_date(seriesToUpdate.last_air_date);
            changed = true;
        }

        // First_Air_Date
        if (SyncUtils.areDatesDifferent(localTVSeries.getFirst_air_date(), seriesToUpdate.first_air_date)) {
            fieldChanges.add(new FieldChange("First_Air_Date",
                    String.valueOf(localTVSeries.getFirst_air_date()),
                    String.valueOf(seriesToUpdate.first_air_date)));
            localTVSeries.setFirst_air_date(seriesToUpdate.first_air_date);
            changed = true;
        }

        // Status
        if (SyncUtils.areStringsDifferent(localTVSeries.getStatus(), seriesToUpdate.status)) {
            fieldChanges.add(new FieldChange("Status",
                    String.valueOf(localTVSeries.getStatus()),
                    String.valueOf(seriesToUpdate.status)));
            localTVSeries.setStatus(seriesToUpdate.status);
            changed = true;
        }

        // Poster
        if (SyncUtils.areStringsDifferent(localTVSeries.getPoster_path(), seriesToUpdate.poster_path)) {
            fieldChanges.add(new FieldChange("Poster_Path",
                    String.valueOf(localTVSeries.getPoster_path()),
                    String.valueOf(seriesToUpdate.poster_path)));
            localTVSeries.setPoster_path(seriesToUpdate.poster_path);
            changed = true;
        }

        // Seasons
        Map<Integer, SeasonDto> localSeasonMap = new HashMap<>();
        for (Object obj : localTVSeries.getSeasons()) {
            if (obj instanceof SeasonDto seasonDto) {
                localSeasonMap.put(seasonDto.getSeason_number(), seasonDto);
            }
        }

        List<SeasonDto> updatedSeasons = new ArrayList<>();
        for (TMDBSeason tmdbSeason : seriesToUpdate.seasons.stream().filter(season -> season.season_number != 0).toList()) {
            SeasonDto localSeason = localSeasonMap.get(tmdbSeason.season_number);
            if (tmdbSeason.season_number == 0 || tmdbSeason.episode_count == 0) {
                continue; // skip specials or no episodes provided
            }
            if (localSeason == null) {
                // New season
                SeasonDto newSeason = new SeasonDto();
                newSeason.setName(tmdbSeason.name);
                newSeason.setSeason_number(tmdbSeason.season_number);
                newSeason.setEpisode_count(tmdbSeason.episode_count);
                newSeason.setAir_date(tmdbSeason.air_date);
                updatedSeasons.add(newSeason);
                fieldChanges.add(new FieldChange(
                        "Season_Added", null,
                        tmdbSeason.name + " (" + tmdbSeason.episode_count + " episodes)"
                ));
                changed = true;
            } else {
                if (SyncUtils.areStringsDifferent(localSeason.getName(), tmdbSeason.name)) {
                    fieldChanges.add(new FieldChange(
                            "Season_" + tmdbSeason.season_number + "_Name",
                            String.valueOf(localSeason.getName()),
                            String.valueOf(tmdbSeason.name)
                    ));
                    localSeason.setName(tmdbSeason.name);
                    changed = true;
                }

                if (SyncUtils.areDatesDifferent(localSeason.getAir_date(), tmdbSeason.air_date)) {
                    fieldChanges.add(new FieldChange(
                            "Season_" + tmdbSeason.season_number + "_Air_Date",
                            String.valueOf(localSeason.getAir_date()),
                            String.valueOf(tmdbSeason.air_date)
                    ));
                    localSeason.setAir_date(tmdbSeason.air_date);
                    changed = true;
                }

                if (SyncUtils.areIntsDifferent(localSeason.getEpisode_count(), tmdbSeason.episode_count)) {
                    fieldChanges.add(new FieldChange(
                            "Season_" + tmdbSeason.season_number + "_Episode_Count",
                            String.valueOf(localSeason.getEpisode_count()),
                            String.valueOf(tmdbSeason.episode_count)
                    ));
                    localSeason.setEpisode_count(tmdbSeason.episode_count);
                    changed = true;
                }

                updatedSeasons.add(localSeason);
            }
        }
        localTVSeries.setSeasons(updatedSeasons);

        // VOD Providers
        try {
            seriesToUpdate.vodProviders = getVodProviders(webClient, tmdbApiKey, localTVSeries.getTmdbId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (SyncUtils.areStringListsDifferent(localTVSeries.getVodProviders(), seriesToUpdate.vodProviders)) {
            fieldChanges.add(new FieldChange("VOD_Providers",
                    String.valueOf(localTVSeries.getVodProviders()),
                    String.valueOf(seriesToUpdate.vodProviders)));
            localTVSeries.setVodProviders(seriesToUpdate.vodProviders);
            changed = true;
        }

        TVSeries seriesToSave = TVSeriesDto.toEntity(localTVSeries);

        if (changed) {
            seriesRepo.save(seriesToSave);
            updatedCount++;
            changes.add(new ChangeDetail(seriesToSave.getId(), seriesToSave.getName(), fieldChanges));
        }

        return new SyncResultDto(1, updatedCount, changes);
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
        public int number_of_episodes;
        public int number_of_seasons;
        public LocalDate last_air_date;
    }

    private static class TMDBSeriesToUpdate
    {
        public String name;
        public int number_of_episodes;
        public int number_of_seasons;
        public LocalDate last_air_date;
        public LocalDate first_air_date;
        public String status;
        public String poster_path;
        public List<TMDBSeason> seasons;
        public List<String> vodProviders;
    }

    private static class TMDBSeason
    {
        public String name;
        public int season_number;
        public LocalDate air_date;
        public int episode_count;
    }

    public List<String> test(Long seriesId) throws Exception
    {
//        String response = webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/tv/1920/watch/providers")
//                        .queryParam("api_key", tmdbApiKey)
//                        .queryParam("language", "en-US")
//                        .queryParam("region", "PL")
//                        .build())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
        List<String> vodProviders = getVodProviders(webClient, tmdbApiKey, seriesId);

        return vodProviders;
    }

    public List<String> getVodProviders(WebClient webClient, String tmdbApiKey, long tvSeriesId) throws Exception
    {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/" + tvSeriesId + "/watch/providers")
                        .queryParam("api_key", tmdbApiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);
        JsonNode providersForCountry = root.path("results").path("PL");

        List<String> providerNames = new ArrayList<>();
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

}