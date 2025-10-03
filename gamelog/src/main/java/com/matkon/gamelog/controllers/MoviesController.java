package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.movies.MovieDto;
import com.matkon.gamelog.data.movies.MovieListDto;
import com.matkon.gamelog.data.movies.MovieSaveResultDto;
import com.matkon.gamelog.data.movies.MovieSearchDto;
import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.services.MoviesService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MoviesController {

    private final MoviesService moviesService;

    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    @GetMapping("/search")
    @Operation(summary = "[TMDB API] Search movies by query")
    public ResponseEntity<List<MovieSearchDto>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "pl-PL") String language,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(moviesService.searchByQuery(query, language, page));
    }

    @PostMapping("/save/{tmdbId}")
    @Operation(summary = "[TMDB API] Save to library by tmdbId")
    public ResponseEntity<MovieSaveResultDto> add(@PathVariable Long tmdbId) {
        try {
            MovieSaveResultDto result = moviesService.saveMovie(tmdbId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    @Operation(summary = "[TMDB API] Get all movies")
    public ResponseEntity<List<MovieListDto>> getAllMovies() {
        try {
            return ResponseEntity.ok(moviesService.getAllMovies());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/pageable")
    @Operation(summary = "[TMDB API] Get all movies with pagination")
    public ResponseEntity<Page<MovieListDto>> getAllMoviesWithPagination(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(moviesService.getAllMoviesWithPagination(page, size));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{movieId}")
    @Operation(summary = "[TMDB API] Get Movie by ID")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long movieId) {
        try {
            return ResponseEntity.ok(moviesService.getMovieById(movieId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Delete Movie by ID")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        moviesService.deleteMovie(movieId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/sync-library/{movieId}")
    @Operation(summary = "[TMDB API]  Sync movies")
    public ResponseEntity<SyncResultDto> syncLibrarySeries(@PathVariable Long movieId) throws Exception {
        SyncResultDto result = moviesService.syncMovies(movieId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test-endpoint/")
    @Operation(summary = "test endpoint")
    public ResponseEntity<Void> test() throws Exception {
        moviesService.test();
        return ResponseEntity.noContent().build();
    }
}
