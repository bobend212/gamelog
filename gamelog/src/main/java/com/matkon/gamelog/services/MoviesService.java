
package com.matkon.gamelog.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.movies.Movie;
import com.matkon.gamelog.data.movies.MovieDto;
import com.matkon.gamelog.data.movies.MovieListDto;
import com.matkon.gamelog.data.movies.MovieSaveResultDto;
import com.matkon.gamelog.data.movies.MovieSearchDto;
import com.matkon.gamelog.repos.MoviesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MoviesService
{
    private final MoviesRepository moviesRepository;
    private final WebClient webClient;
    private final String tmdbApiKey;

    public MoviesService(MoviesRepository moviesRepository, @Value("${tmdb.api.key}") String tmdbApiKey)
    {
        this.moviesRepository = moviesRepository;
        this.tmdbApiKey = tmdbApiKey;
        this.webClient = WebClient.create("https://api.themoviedb.org/3");
    }

    public List<MovieSearchDto> searchByQuery(String query, String lang, int page)
    {
        TMDBMovieSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/movie")
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("query", query)
                        .queryParam("language", lang != null ? lang : "pl-PL")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(TMDBMovieSearchResponse.class)
                .block();

        if (response == null || response.results == null) return List.of();

        return response.results.stream()
                .map(r ->
                        new MovieSearchDto(r.getId(), r.getPoster_path(), r.getTitle(), r.getReleaseDate()))
                .toList();
    }

    @Transactional
    public MovieSaveResultDto saveMovie(Long tmdbId) throws Exception
    {
        Optional<Movie> existing = moviesRepository.findByTmdbId(tmdbId);
        if (existing.isPresent()) {
            return new MovieSaveResultDto(
                    existing.get().getId(),
                    true,
                    "Movie already exist in the database"
            );
        }

        TMDBMovie details = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("language", "pl-PL")
                        .build(tmdbId))
                .retrieve()
                .bodyToMono(TMDBMovie.class)
                .block();

        if (details == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found in TMDB API");
        }

        Movie movie = new Movie();
        movie.setTmdbId(details.id);

        moviesRepository.save(movie);
        return new MovieSaveResultDto(
                movie.getId(),
                false,
                "Movie added successfully"
        );
    }

    public List<MovieListDto> getAllMovies() throws Exception
    {
        List<Movie> dbMovies = moviesRepository.findAll();
        List<MovieListDto> movieDtos = new ArrayList<>();

        for (Movie dbMovie : dbMovies) {
            MovieListDto movieDetails = getMovieListDetails(dbMovie);
            movieDtos.add(movieDetails);
        }

        return movieDtos;
    }

    public Page<MovieListDto> getAllMoviesWithPagination(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Movie> dbMovies = moviesRepository.findAll(pageable);

        return dbMovies.map(movie -> {
            try {
                return getMovieListDetails(movie);
            } catch (Exception e) {
                throw new RuntimeException("Movie details fetch failed", e);
            }
        });
    }

    public MovieDto getMovieById(Long id) throws Exception
    {
        Optional<Movie> movieOpt = moviesRepository.findById(id);
        Movie movie = movieOpt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        return getMovieDetails(movie);
    }

    @Transactional
    public void deleteMovie(Long movieId)
    {
        if (!moviesRepository.existsById(movieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        moviesRepository.deleteById(movieId);
    }

    private MovieDto getMovieDetails(Movie dbMovie) throws Exception
    {
        String responseJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + dbMovie.getTmdbId())
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("language", "pl-PL")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseJson);

        MovieDto dto = new MovieDto();
        dto.setId(dbMovie.getId());
        dto.setTmdbId(dbMovie.getTmdbId());
        dto.setTitle(root.path("title").asText());
        dto.setOriginalTitle(root.path("original_title").asText());
        dto.setOverview(root.path("overview").asText());
        dto.setReleaseDate(root.path("release_date").asText());
        dto.setReleaseDatePL(getReleaseDate(dbMovie.getTmdbId()));
        dto.setRuntime(root.path("runtime").asInt());
        dto.setStatus(root.path("status").asText());
        dto.setPoster(root.path("poster_path").asText());
        dto.setCreatedAt(dbMovie.getCreatedAt());

        List<String> genres = new ArrayList<>();
        for (JsonNode genre : root.path("genres")) {
            genres.add(genre.path("name").asText());
        }
        dto.setGenres(genres);

        dto.setVodProviders(getVodProviders(dbMovie.getTmdbId()));

        return dto;
    }

    private MovieListDto getMovieListDetails(Movie dbMovie) throws Exception
    {
        String responseJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + dbMovie.getTmdbId())
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("language", "pl-PL")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseJson);

        MovieListDto dto = new MovieListDto();
        dto.setId(dbMovie.getId());
        dto.setTitle(root.path("title").asText());
        dto.setOriginalTitle(root.path("original_title").asText());
        dto.setReleaseDate(root.path("release_date").asText());
        dto.setStatus(root.path("status").asText());
        dto.setPoster(root.path("poster_path").asText());

        List<String> genres = new ArrayList<>();
        for (JsonNode genre : root.path("genres")) {
            genres.add(genre.path("name").asText());
        }
        dto.setGenres(genres);

        dto.setVodProviders(getVodProviders(dbMovie.getTmdbId()));

        return dto;
    }

    // ---- TMDb DTOs ----
    private static class TMDBMovieSearchResponse
    {
        public List<MovieSearchDto> results;
    }

    private static class TMDBMovie
    {
        public Long id;
    }

    private LocalDate getReleaseDate(long tmdbId) throws Exception
    {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tmdbId + "/release_dates")
                        .queryParam("api_key", tmdbApiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);

        JsonNode results = root.path("results");
        if (results.isMissingNode() || !results.isArray()) {
            return null;
//            throw new IllegalStateException("Missing or invalid 'results' field in response");
        }

        JsonNode plNode = null;
        for (JsonNode countryNode : results) {
            if ("PL".equals(countryNode.path("iso_3166_1").asText())) {
                plNode = countryNode;
                break;
            }
        }

        if (plNode == null) {
            return null;
//            throw new IllegalStateException("No release info found for country code 'PL'");
        }

        JsonNode releaseDates = plNode.path("release_dates");
        JsonNode releaseDateNode = null;
        if (releaseDates.isArray()) {
            for (JsonNode rd : releaseDates) {
                if (rd.path("type").asInt(-1) == 3) {
                    releaseDateNode = rd;
                    break;
                }
            }
        }

        if (releaseDateNode == null) {
            return null;
//            throw new IllegalStateException("No release date of type 3 found for 'PL'");
        }

        String releaseDateStr = releaseDateNode.path("release_date").asText(null);
        if (releaseDateStr == null) {
            return null;
//            throw new IllegalStateException("No release date string found");
        }

        return OffsetDateTime.parse(releaseDateStr).toLocalDate();
    }

    private List<String> getVodProviders(long tmdbId) throws Exception
    {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + tmdbId + "/watch/providers")
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
