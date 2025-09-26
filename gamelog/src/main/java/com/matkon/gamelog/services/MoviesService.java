
package com.matkon.gamelog.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.movies.Movie;
import com.matkon.gamelog.data.movies.MovieDto;
import com.matkon.gamelog.data.movies.MovieListDto;
import com.matkon.gamelog.data.movies.MovieSaveResultDto;
import com.matkon.gamelog.data.movies.MovieSearchDto;
import com.matkon.gamelog.data.sync.ChangeDetail;
import com.matkon.gamelog.data.sync.FieldChange;
import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.data.sync.SyncUtils;
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
import java.time.LocalDateTime;
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

        TMDBMovieSaveDto details = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("language", "pl-PL")
                        .build(tmdbId))
                .retrieve()
                .bodyToMono(TMDBMovieSaveDto.class)
                .block();

        if (details == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found in TMDB API");
        }

        Movie movie = new Movie();
        movie.setTmdbId(details.id);
        movie.setTitle(details.title);
        movie.setOriginalTitle(details.original_title);
        movie.setReleaseDate(details.release_date);
        movie.setStatus(details.status);
        movie.setPoster(details.poster_path);

        List<String> genresToSave = new ArrayList<>();
        details.genres.forEach(g -> {
            genresToSave.add(g.name);
        });
        movie.setGenres(genresToSave);

        try {
            movie.setVodProviders(getVodProviders(tmdbId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        moviesRepository.save(movie);
        return new MovieSaveResultDto(
                movie.getId(),
                false,
                "Movie added successfully"
        );
    }

    public List<MovieListDto> getAllMovies() throws Exception
    {
        return moviesRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(MovieListDto::fromEntity).toList();
    }

    public Page<MovieListDto> getAllMoviesWithPagination(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return moviesRepository.findAll(pageable).map(MovieListDto::fromEntity);
    }

    public MovieDto getMovieById(Long id) throws Exception
    {
        Optional<Movie> movieOpt = moviesRepository.findById(id);
        Movie movie = movieOpt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        return getMovieDetails(movie);
    }

    private MovieDto getMovieDetails(Movie dbMovie) throws Exception
    {
        TMDBMovieDetailsDto responseJson = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + dbMovie.getTmdbId())
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("language", "pl-PL")
                        .build())
                .retrieve()
                .bodyToMono(TMDBMovieDetailsDto.class)
                .block();

        if (responseJson == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found in TMDB API");
        }

        MovieDto dto = new MovieDto();
        dto.setId(dbMovie.getId());
        dto.setTmdbId(dbMovie.getTmdbId());
        dto.setTitle(dbMovie.getTitle());
        dto.setOriginalTitle(dbMovie.getOriginalTitle());
        dto.setOverview(responseJson.overview);
        dto.setReleaseDate(dbMovie.getReleaseDate());
        dto.setReleaseDatePL(getReleaseDatePL(dbMovie.getTmdbId()));
        dto.setRuntime(responseJson.runtime);
        dto.setStatus(dbMovie.getStatus());
        dto.setPoster(dbMovie.getPoster());
        dto.setCreatedAt(dbMovie.getCreatedAt());
        dto.setGenres(dbMovie.getGenres());
        dto.setVodProviders(dbMovie.getVodProviders());

        return dto;
    }

    @Transactional
    public void deleteMovie(Long movieId)
    {
        if (!moviesRepository.existsById(movieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }
        moviesRepository.deleteById(movieId);
    }

    // ---- TMDb DTOs ----
    private static class TMDBMovieSearchResponse
    {
        public List<MovieSearchDto> results;
    }

    private static class TMDBMovieSaveDto
    {
        public Long id;
        public String title;
        public String original_title;
        public String poster_path;
        public String status;
        public LocalDate release_date;
        public List<TMDBMovieGenre> genres;
    }

    private static class TMDBMovieDetailsDto
    {
        public String overview;
        public int runtime;
    }

    private static class TMDBMovieToUpdateDto
    {
        public String title;
        public String original_title;
        public LocalDate release_date;
        public String status;
        public String poster_path;
        public List<String> vodProviders;
    }

    private static class TMDBMovieGenre
    {
        public String name;
    }

    private LocalDate getReleaseDatePL(long tmdbId) throws Exception
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
        }

        String releaseDateStr = releaseDateNode.path("release_date").asText(null);
        if (releaseDateStr == null) {
            return null;
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

    public SyncResultDto syncMovies(Long id) throws Exception
    {
        if (id != 0) {
            return syncMovie(id);
        } else {
            int total = 0;
            int updatedCount = 0;
            List<ChangeDetail> allChanges = new ArrayList<>();

            List<Long> ids = moviesRepository.findAll().stream()
                    .map(Movie::getId)
                    .toList();
            total = ids.size();

            for (Long seriesId : ids) {
                SyncResultDto result = syncMovie(seriesId);
                if (result != null) {
                    updatedCount += result.getUpdatedCount();
                    allChanges.addAll(result.getChanges());
                }
            }

            return new SyncResultDto(total, updatedCount, allChanges);
        }
    }

    @Transactional
    private SyncResultDto syncMovie(Long id) throws Exception
    {
        Optional<Movie> movieOpt = moviesRepository.findById(id);
        MovieDto dbMovie = getMovieDetails(movieOpt.orElseThrow());

        int updatedCount = 0;
        List<ChangeDetail> changes = new ArrayList<>();

        TMDBMovieToUpdateDto movieToUpdate = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/" + dbMovie.getTmdbId())
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("language", "pl-PL")
                        .build())
                .retrieve()
                .bodyToMono(TMDBMovieToUpdateDto.class)
                .block();

        if (movieToUpdate == null) return null;

        List<FieldChange> fieldChanges = new ArrayList<>();
        boolean changed = false;

        // Title
        if (SyncUtils.areStringsDifferent(dbMovie.getTitle(), movieToUpdate.title)) {
            fieldChanges.add(new FieldChange("Title",
                    String.valueOf(dbMovie.getTitle()),
                    String.valueOf(movieToUpdate.title)));
            dbMovie.setTitle(movieToUpdate.title);
            changed = true;
        }

        // Original Title
        if (SyncUtils.areStringsDifferent(dbMovie.getOriginalTitle(), movieToUpdate.original_title)) {
            fieldChanges.add(new FieldChange("Original Title",
                    String.valueOf(dbMovie.getOriginalTitle()),
                    String.valueOf(movieToUpdate.original_title)));
            dbMovie.setOriginalTitle(movieToUpdate.original_title);
            changed = true;
        }

        // Release Date
        if (SyncUtils.areDatesDifferent(dbMovie.getReleaseDate(), movieToUpdate.release_date)) {
            fieldChanges.add(new FieldChange("Release Date",
                    String.valueOf(dbMovie.getReleaseDate()),
                    String.valueOf(movieToUpdate.release_date)));
            dbMovie.setReleaseDate(movieToUpdate.release_date);
            changed = true;
        }

        // Status
        if (SyncUtils.areStringsDifferent(dbMovie.getStatus(), movieToUpdate.status)) {
            fieldChanges.add(new FieldChange("Status",
                    String.valueOf(dbMovie.getStatus()),
                    String.valueOf(movieToUpdate.status)));
            dbMovie.setStatus(movieToUpdate.status);
            changed = true;
        }

        // Poster
        if (SyncUtils.areStringsDifferent(dbMovie.getPoster(), movieToUpdate.poster_path)) {
            fieldChanges.add(new FieldChange("Poster",
                    String.valueOf(dbMovie.getPoster()),
                    String.valueOf(movieToUpdate.poster_path)));
            dbMovie.setPoster(movieToUpdate.poster_path);
            changed = true;
        }

        // VOD Providers
        try {
            movieToUpdate.vodProviders = getVodProviders(dbMovie.getTmdbId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (SyncUtils.areStringListsDifferent(dbMovie.getVodProviders(), movieToUpdate.vodProviders)) {

            List<String> oldProviders = dbMovie.getVodProviders().stream()
                    .map(s -> {
                        String[] parts = s.split(";", 2);
                        return parts.length > 1 ? parts[1] : parts[0];
                    }).toList();

            List<String> newProviders = movieToUpdate.vodProviders.stream()
                    .map(s -> {
                        String[] parts = s.split(";", 2);
                        return parts.length > 1 ? parts[1] : parts[0];
                    }).toList();

            fieldChanges.add(new FieldChange("VOD_Providers",
                    String.valueOf(oldProviders),
                    String.valueOf(newProviders)));
            dbMovie.setVodProviders(movieToUpdate.vodProviders);
            changed = true;
        }

        Movie movieToSave = MovieDto.toEntity(dbMovie);

        if (changed) {
            moviesRepository.save(movieToSave);
            updatedCount++;
            changes.add(new ChangeDetail(movieToSave.getId(), movieToSave.getTitle(), fieldChanges));
        }

        return new SyncResultDto(1, updatedCount, changes);
    }


    public void test() throws JsonProcessingException
    {
        List<Movie> dbMovies = moviesRepository.findAll();

        for (Movie dbMovie : dbMovies) {
            Long tmdbId = dbMovie.getTmdbId();

            String responseJson = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/" + tmdbId)
                            .queryParam("api_key", tmdbApiKey)
                            .queryParam("language", "pl-PL")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
//
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);

//            dbMovie.setOriginalTitle(root.path("original_title").asText());
            dbMovie.setReleaseDate(parseDate(root.path("release_date").asText()));
//            dbMovie.setStatus(root.path("status").asText());
//            dbMovie.setPoster(root.path("poster_path").asText());

//            try {
//                List<String> vodProviders = getVodProviders(tmdbId);
//                dbMovie.setVodProviders(vodProviders);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }

        }

        moviesRepository.saveAll(dbMovies);
    }

    private LocalDate parseDate(String date)
    {
        return (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
    }

}
