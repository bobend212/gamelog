package com.matkon.gamelog.api.movie;

import com.matkon.gamelog.domain.movie.service.MovieService;
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
    private final MovieApiMapper movieApiMapper;

    @GetMapping()
    @Operation(summary = "Get ALL movies")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<Page<MovieListResponse>> getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(movieService.getMovies(page, size)
                        .map(movieApiMapper::mapMovieToMovieListResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "[TMDB API] Get Movie by ID + fetch TMDB additional data")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(movieApiMapper.mapMovieToMovieResponse(
                        movieService.getSingleMovie(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "[TMDB API] Search movies - by query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Nothing found by query.")
    })
    public ResponseEntity<List<MovieSearchResponse>> searchMovies(@RequestParam String query) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(movieService.searchMovies(query)
                        .stream().map(movieApiMapper::mapMovieToMovieSearchResponse).toList());
    }

    @PostMapping("/{tmdbId}")
    @Operation(summary = "[TMDB API] Save movie - by tmdbId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added to library."),
            @ApiResponse(responseCode = "409", description = "Conflict - The movie already exist in the db."),
            @ApiResponse(responseCode = "404", description = "Not Found - The movie does not exist in the API")
    })
    public ResponseEntity<MovieResponse> saveMovie(@PathVariable Long tmdbId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movieApiMapper.mapMovieToMovieResponse(
                        movieService.saveMovie(tmdbId)));
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Delete Movie - by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully removed movie from library."),
            @ApiResponse(responseCode = "404", description = "Not found - The movie was not found.")
    })
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}