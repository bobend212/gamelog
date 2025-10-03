package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.data.tvseries.TVSeriesDto;
import com.matkon.gamelog.data.tvseries.TVSeriesListDto;
import com.matkon.gamelog.data.tvseries.TVSeriesSaveResultDto;
import com.matkon.gamelog.data.tvseries.TVSeriesSearchDto;
import com.matkon.gamelog.data.tvseries.TrackingType;
import com.matkon.gamelog.services.TVSeriesService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tv-series")
@CrossOrigin(origins = "*")
public class TVSeriesController {

    private final TVSeriesService tvSeriesService;

    public TVSeriesController(TVSeriesService tvSeriesService) {
        this.tvSeriesService = tvSeriesService;
    }

    @GetMapping("/search")
    @Operation(summary = "[TMDB API] Search series by query")
    public ResponseEntity<List<TVSeriesSearchDto>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "en-US") String language,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tvSeriesService.searchByQuery(query, language, page));
    }

    @PostMapping("/save/{tmdbId}")
    @Operation(summary = "[TMDB API] Save to library by tmdbId")
    public ResponseEntity<TVSeriesSaveResultDto> add(
            @PathVariable Long tmdbId,
            @RequestParam TrackingType status) {
        try {
            TVSeriesSaveResultDto result = tvSeriesService.saveSeries(tmdbId, status);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/seasons/{seasonId}/watched/increment")
    @Operation(summary = "Increment watched episodes by 1")
    public ResponseEntity<Void> incrementWatched(@PathVariable Long seasonId) {
        tvSeriesService.incrementWatchedCount(seasonId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/seasons/{seasonId}/watched")
    @Operation(summary = "Set watched episodes by count")
    public ResponseEntity<Void> setWatchedCount(
            @PathVariable Long seasonId,
            @RequestParam int count) {
        tvSeriesService.updateWatchedCount(seasonId, count);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all series")
    public ResponseEntity<List<TVSeriesListDto>> getAllSeries() {
        return ResponseEntity.ok(tvSeriesService.getAllSeries());
    }

    @GetMapping("/{seriesId}")
    @Operation(summary = "Get TV series by ID")
    public ResponseEntity<TVSeriesDto> getSeriesById(@PathVariable Long seriesId) {
        TVSeriesDto dto = tvSeriesService.getSeriesById(seriesId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/filter")
    @Operation(summary = "Get all series filtered by trackingType")
    public ResponseEntity<List<TVSeriesListDto>> getSeriesByTrackingType(@RequestParam(defaultValue = "WATCHING") TrackingType trackingType) {
        return ResponseEntity.ok(tvSeriesService.getAllSeriesByTrackingType(trackingType));
    }

    @PatchMapping("/{seriesId}/trackingType")
    @Operation(summary = "Change tracking type")
    public ResponseEntity<Void> updateTrackingType(
            @PathVariable Long seriesId,
            @RequestParam TrackingType trackingType) {
        tvSeriesService.updateTrackingType(seriesId, trackingType);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{seasonId}/rate")
    @Operation(summary = "Set season rating")
    public ResponseEntity<Void> updateRating(
            @PathVariable Long seasonId,
            @RequestParam(required = false) Double rating) {
        tvSeriesService.rateSeason(seasonId, rating);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seriesId}")
    @Operation(summary = "Delete TV series by ID")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long seriesId) {
        tvSeriesService.deleteSeries(seriesId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/sync-library/{seriesId}")
    @Operation(summary = "[TMDB API]  Sync specified TVSeries")
    public ResponseEntity<SyncResultDto> syncLibrarySeries(@PathVariable Long seriesId) {
        SyncResultDto result = tvSeriesService.syncSeries(seriesId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test/{seriesId}")
    @Operation(summary = "test")
    public ResponseEntity<List<String>> test(@PathVariable Long seriesId) throws Exception {
        return ResponseEntity.ok(tvSeriesService.test(seriesId));
    }
}