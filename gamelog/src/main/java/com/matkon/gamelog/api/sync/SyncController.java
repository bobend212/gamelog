package com.matkon.gamelog.api.sync;

import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.service.GameService;
import com.matkon.gamelog.domain.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/sync")
@CrossOrigin(origins = "*")
public class SyncController {

    private final GameService gameService;
    private final MovieService movieService;
    private final SyncApiMapper syncApiMapper;

    @PatchMapping("/games")
    @Operation(summary = "[RAWG API] Sync games data with RAWG API - by status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<SyncResponse> syncGames(@RequestParam(defaultValue = "WISHLIST") GameStatus status) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(syncApiMapper.mapSyncResultToSyncResponse(
                        gameService.syncGamesByStatus(status)));
    }

    @PatchMapping("/movies")
    @Operation(summary = "[TMDB API] Sync all movies")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<SyncResponse> syncMovies() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(syncApiMapper.mapSyncResultToSyncResponse(
                        movieService.syncAllMovies()));
    }

    @PatchMapping("/movies/{movieId}")
    @Operation(summary = "[TMDB API] Sync single movie - by movieId")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<SyncResponse> syncSingleMovie(@PathVariable Long movieId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(syncApiMapper.mapSyncResultToSyncResponse(
                        movieService.syncSingleMovie(movieId)));
    }
}
