package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.data.tvshow.TrackingType;
import com.matkon.gamelog.data.tvshow.dto.TVShowDto;
import com.matkon.gamelog.data.tvshow.dto.TVShowListDto;
import com.matkon.gamelog.data.tvshow.dto.TVShowSaveResultDto;
import com.matkon.gamelog.data.tvshow.dto.TVShowSearchResultDto;
import com.matkon.gamelog.services.TVShowService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping("/api/tv-show")
@CrossOrigin(origins = "*")
public class TVShowController {

    private final TVShowService tvShowService;

    @GetMapping("/search")
    @Operation(summary = "[TMDB API] Search TV Shows by query")
    public ResponseEntity<List<TVShowSearchResultDto>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "en-US") String language,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(tvShowService.searchByQuery(query, language, page));
    }

    @PostMapping("/save/{tmdbId}")
    @Operation(summary = "[TMDB API] Save to library by tmdbId")
    public ResponseEntity<TVShowSaveResultDto> add(
            @PathVariable Long tmdbId,
            @RequestParam TrackingType status) {
        try {
            TVShowSaveResultDto result = tvShowService.saveSeries(tmdbId, status);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/seasons/{seasonId}/watched/increment")
    @Operation(summary = "Increment watched episodes by 1")
    public ResponseEntity<Void> incrementWatched(@PathVariable Long seasonId) {
        tvShowService.incrementWatchedCount(seasonId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/seasons/{seasonId}/watched")
    @Operation(summary = "Set watched episodes by count")
    public ResponseEntity<Void> setWatchedCount(
            @PathVariable Long seasonId,
            @RequestParam int count) {
        tvShowService.updateWatchedCount(seasonId, count);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all series")
    public ResponseEntity<List<TVShowListDto>> getAllSeries() {
        return ResponseEntity.ok(tvShowService.getAllSeries());
    }

    @GetMapping("/{seriesId}")
    @Operation(summary = "Get TV series by ID")
    public ResponseEntity<TVShowDto> getSeriesById(@PathVariable Long seriesId) {
        TVShowDto dto = tvShowService.getSeriesById(seriesId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/filter")
    @Operation(summary = "Get all series filtered by trackingType")
    public ResponseEntity<List<TVShowListDto>> getAllSeriesByTrackingType(@RequestParam(defaultValue = "WATCHING") TrackingType trackingType) {
        return ResponseEntity.ok(tvShowService.getAllSeriesByTrackingType(trackingType));
    }

    @PatchMapping("/{seriesId}/trackingType")
    @Operation(summary = "Change tracking type")
    public ResponseEntity<Void> updateTrackingType(
            @PathVariable Long seriesId,
            @RequestParam TrackingType trackingType) {
        tvShowService.updateTrackingType(seriesId, trackingType);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{seasonId}/rate")
    @Operation(summary = "Set season rating")
    public ResponseEntity<Void> updateRating(
            @PathVariable Long seasonId,
            @RequestParam(required = false) Double rating) {
        tvShowService.rateSeason(seasonId, rating);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seriesId}")
    @Operation(summary = "Delete TV series by ID")
    public ResponseEntity<Void> deleteSeries(@PathVariable Long seriesId) {
        tvShowService.deleteSeries(seriesId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/sync-library/{seriesId}")
    @Operation(summary = "[TMDB API]  Sync specified TVSeries")
    public ResponseEntity<SyncResultDto> syncLibrarySeries(@PathVariable Long seriesId) {
        SyncResultDto result = tvShowService.syncSeries(seriesId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test/{seriesId}")
    @Operation(summary = "test")
    public ResponseEntity<List<String>> test(@PathVariable Long seriesId) throws Exception {
        return ResponseEntity.ok(tvShowService.test(seriesId));
    }
}