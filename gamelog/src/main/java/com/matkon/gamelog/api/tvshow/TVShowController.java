package com.matkon.gamelog.api.tvshow;

import com.matkon.gamelog.domain.tvshow.model.TrackingType;
import com.matkon.gamelog.domain.tvshow.service.TVShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/tv-shows")
@CrossOrigin(origins = "*")
public class TVShowController {

    private final TVShowService tvShowService;
    private final TVShowApiMapper tvShowApiMapper;

    @GetMapping
    @Operation(summary = "Get ALL TV Shows + filter by TrackingType and search by title")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<Page<TVShowListResponse>> getAllTVShows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") TrackingType trackingType) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tvShowService.getAllTVShows(page, size, search, trackingType)
                        .map(tvShowApiMapper::mapTVShowToTVShowListResponse));
    }

    @GetMapping("/{tvShowId}")
    @Operation(summary = "Get TV Show by Id")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<TVShowResponse> getSingleTVShow(@PathVariable Long tvShowId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tvShowApiMapper.mapTVShowToTVShowResponse(
                        tvShowService.getSingleTVShow(tvShowId)));
    }

    @GetMapping("/search")
    @Operation(summary = "[TMDB API] Search TV Shows - by query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Nothing found by query.")
    })
    public ResponseEntity<List<TVShowSearchResponse>> searchTVShow(@RequestParam String query) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(tvShowService.searchTVShows(query)
                        .stream().map(tvShowApiMapper::mapTVShowToTVShowSearchResponse).toList());
    }

    @PostMapping("/{tmdbId}")
    @Operation(summary = "[TMDB API] Save TV Show - by tmdbId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added to library."),
            @ApiResponse(responseCode = "409", description = "Conflict - The item already exist in the db."),
            @ApiResponse(responseCode = "404", description = "Not Found - The item does not exist in the API")
    })
    public ResponseEntity<TVShowResponse> saveTVShow(@PathVariable Long tmdbId,
                                                     @RequestParam(defaultValue = "WATCHING") TrackingType trackingType) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tvShowApiMapper.mapTVShowToTVShowResponse(
                        tvShowService.saveTVShow(tmdbId, trackingType)));
    }

    @DeleteMapping("/{tvShowId}")
    @Operation(summary = "Delete TV Show - by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully removed item from library."),
            @ApiResponse(responseCode = "404", description = "Not found - Item was not found.")
    })
    public ResponseEntity<Void> deleteTVShow(@PathVariable Long tvShowId) {
        tvShowService.deleteTVShow(tvShowId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{tvShowId}")
    @Operation(summary = "Update tracking type")
    public ResponseEntity<Void> updateTrackingType(
            @PathVariable Long tvShowId,
            @RequestParam TrackingType trackingType) {
        tvShowService.updateTrackingType(tvShowId, trackingType);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/season/{seasonId}/rate")
    @Operation(summary = "Rate season")
    public ResponseEntity<Void> rateSeason(
            @PathVariable Long seasonId,
            @RequestParam(required = false) Double rating) {
        tvShowService.rateSeason(seasonId, rating);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/season/{seasonId}/watched")
    @Operation(summary = "Set watched count for season")
    public ResponseEntity<Void> setWatchedCount(
            @PathVariable Long seasonId,
            @RequestParam(required = false) Integer count) {
        tvShowService.setWatchedCount(seasonId, count);
        return ResponseEntity.ok().build();
    }
}