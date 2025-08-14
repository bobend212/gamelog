package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.tvseries.MyStatus;
import com.matkon.gamelog.data.tvseries.SeasonProgressDto;
import com.matkon.gamelog.data.tvseries.TVSeriesSearchDto;
import com.matkon.gamelog.data.tvseries.TVSeriesWithProgressDto;
import com.matkon.gamelog.services.TVSeriesService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
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
public class TVSeriesController
{

    private final TVSeriesService service;

    public TVSeriesController(TVSeriesService service)
    {
        this.service = service;
    }

    @GetMapping("/search")
    @Operation(summary = "[TMDB API] Search series by query")
    public ResponseEntity<List<TVSeriesSearchDto>> search(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "en-US") String language,
            @RequestParam(required = false, defaultValue = "1") int page)
    {
        return ResponseEntity.ok(service.searchTVSeries(query, language, page));
    }

    @PostMapping("/{tmdbId}")
    @Operation(summary = "[TMDB API] Save to LIBRARY by tmdbId")
    public ResponseEntity<Void> add(
            @PathVariable Long tmdbId,
            @RequestParam MyStatus status)
    {
        service.addSeriesByTmdbId(tmdbId, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/seasons/{seasonId}/watched/increment")
    @Operation(summary = "Increment watched episodes by 1")
    public ResponseEntity<Void> incrementWatched(@PathVariable Long seasonId)
    {
        service.incrementWatchedCount(seasonId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/seasons/{seasonId}/watched")
    @Operation(summary = "Increment watched episodes by count")
    public ResponseEntity<Void> setWatchedCount(
            @PathVariable Long seasonId,
            @RequestParam int count)
    {
        service.updateWatchedCount(seasonId, count);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{seriesId}/seasons/{seasonId}/progress")
    @Operation(summary = "Get actual progress for series")
    public ResponseEntity<SeasonProgressDto> getProgress(
            @PathVariable Long seriesId,
            @PathVariable Long seasonId)
    {
        return ResponseEntity.ok(service.getSeasonProgress(seriesId, seasonId));
    }

    @GetMapping
    public ResponseEntity<List<TVSeriesWithProgressDto>> getAllSeriesWithProgress()
    {
        return ResponseEntity.ok(service.getAllSeriesWithProgress());
    }

}

