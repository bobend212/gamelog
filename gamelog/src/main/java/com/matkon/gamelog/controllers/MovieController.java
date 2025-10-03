package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.movie.dto.MovieDto;
import com.matkon.gamelog.data.movie.dto.MovieListDto;
import com.matkon.gamelog.data.movie.dto.MovieSaveResultDto;
import com.matkon.gamelog.data.movie.dto.MovieSearchResultDto;
import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/search")
    @Operation(summary = "[TMDB API] Search movies by query")
    public ResponseEntity<List<MovieSearchResultDto>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "pl-PL") String language,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(movieService.searchByQuery(query, language, page));
    }

    @PostMapping("/save/{tmdbId}")
    @Operation(summary = "[TMDB API] Save to library by tmdbId")
    public ResponseEntity<MovieSaveResultDto> add(@PathVariable Long tmdbId) {
        try {
            MovieSaveResultDto result = movieService.saveMovie(tmdbId);
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
            return ResponseEntity.ok(movieService.getAllMovies());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/pageable")
    @Operation(summary = "[TMDB API] Get all movies with pagination")
    public ResponseEntity<Page<MovieListDto>> getAllMoviesWithPagination(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(movieService.getAllMoviesWithPagination(page, size));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{movieId}")
    @Operation(summary = "[TMDB API] Get Movie by ID")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long movieId) {
        try {
            return ResponseEntity.ok(movieService.getMovieById(movieId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Delete Movie by ID")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/sync-library/{movieId}")
    @Operation(summary = "[TMDB API]  Sync movies")
    public ResponseEntity<SyncResultDto> syncLibrarySeries(@PathVariable Long movieId) throws Exception {
        SyncResultDto result = movieService.syncMovies(movieId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test-endpoint/")
    @Operation(summary = "test endpoint")
    public ResponseEntity<Void> test() throws Exception {
        movieService.test();
        return ResponseEntity.noContent().build();
    }
}
