package com.matkon.gamelog.api.movie;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.matkon.gamelog.IntegrationTest;
import com.matkon.gamelog.infrastructure.movie.database.MovieEntity;
import com.matkon.gamelog.infrastructure.movie.database.MovieJpaRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.matkon.gamelog.IntegrationTestHelper.MOVIES_API_URL;
import static com.matkon.gamelog.IntegrationTestHelper.RESPONSE_FILES_PATH;
import static com.matkon.gamelog.IntegrationTestHelper.readJsonFile;
import static com.matkon.gamelog.IntegrationTestHelper.startWireMock;
import static com.matkon.gamelog.IntegrationTestHelper.stopWireMock;
import static com.matkon.gamelog.IntegrationTestHelper.wireMockServer;
import static com.matkon.gamelog.IntegrationTestHelper.wiremockProperties;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class MovieControllerTest {

    public static final LocalDateTime MOCK_DATE = LocalDateTime.of(2023, 2, 9, 10, 10, 10);

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MovieJpaRepository movieJpaRepository;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        wiremockProperties(registry);
    }

    @BeforeAll
    static void beforeAll() {
        startWireMock();
    }

    @AfterAll
    static void afterAll() {
        stopWireMock();
    }

    @BeforeEach
    void setUp() {
        movieJpaRepository.deleteAll();

        MovieEntity me1 = createMovie(1L, "Wiedźmin", "The Wither", LocalDate.of(2015, 5, 19),
                "Released", "/1s4OrFrEOpP3Pb89ETNxSDQyvQX.jpg", List.of("Horror", "Fantasy"), List.of("Netflix", "HBO"));

        MovieEntity me2 = createMovie(2L, "Paranormalna misja", "Paranormal Mission", LocalDate.of(2025, 3, 12),
                "Released", "/x8TdWFrEOpMaPb8AENSxGvQyvC9.jpg", List.of("Horror", "Thriller"), List.of("Netflix", "Prime Video"));

        MovieEntity me3 = createMovie(3L, "Bugonia", "Bugonia", LocalDate.of(2025, 6, 7),
                "In Production", "/bugonia987sdsa3.jpg", List.of("Sci-Fi", "Mystery"), List.of("HBO", "Disney+"));

        MovieEntity me4 = createMovie(4L, "Jurassic World: Odrodzenie", "Jurassic World: Rebirth", LocalDate.of(2025, 7, 14),
                "Released", "/jwrebirthpl54fd.jpg", List.of("Action"), List.of());

        movieJpaRepository.saveAll(List.of(me1, me2, me3, me4));
    }

    @Test
    void getMoviesTest_all() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get(MOVIES_API_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "getMovies_all_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void getSingleMovieTest() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlPathMatching("/movie/\\d+/release_dates"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("wiremock/tmdb_get_movie_release_dates.json")));

        wireMockServer.stubFor(WireMock.get(urlPathMatching("/movie/\\d+"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/tmdb_get_movie_details.json")));

        MovieEntity movie = findByTitle("Wiedźmin").orElseThrow();

        MvcResult result = mockMvc.perform(get(MOVIES_API_URL + "/{movieId}", movie.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "getSingleMovie_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    public void searchMoviesTest() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/search/movie"))
                .withQueryParam("query", equalTo("bugonia"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/tmdb_search_movies_response.json")));

        MvcResult mvcResult = mockMvc.perform(get(MOVIES_API_URL + "/search")
                        .param("query", "bugonia"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "searchMovie_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    public void searchMoviesTest_shouldReturnNotFound() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/search/movie"))
                .withQueryParam("query", equalTo("non-existing-title"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile(null)));

        mockMvc.perform(MockMvcRequestBuilders.get(MOVIES_API_URL + "/search")
                        .param("query", "non-existing-title"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void saveMovieTest() throws Exception {
        Long tmdbId = 5001L;

        wireMockServer.stubFor(WireMock.get(urlPathMatching("/movie/\\d+"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/tmdb_get_movie_save_response.json")));

        wireMockServer.stubFor(WireMock.get(urlPathMatching("/movie/\\d+/watch/providers"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/tmdb_vod_providers_save_response.json")));


        MvcResult mvcResult = mockMvc.perform(post(MOVIES_API_URL + "/{tmdbId}", tmdbId))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "saveMovie_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    public void saveMovieTest_shouldThrowMovieAlreadyExistException() throws Exception {

        wireMockServer.stubFor(WireMock.get(urlPathMatching("/movie/\\d+"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/tmdb_get_movie_save_response.json")));

        wireMockServer.stubFor(WireMock.get(urlPathMatching("/movie/\\d+/watch/providers"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/tmdb_vod_providers_save_response.json")));

        mockMvc.perform(post(MOVIES_API_URL + "/{tmdbId}", 1L))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    void deleteMovieTest() throws Exception {
        // given
        MovieEntity movieEntity = findByTitle("Wiedźmin").orElseThrow();
        Long movieId = movieEntity.getId();

        // when & then
        mockMvc.perform(delete(MOVIES_API_URL + "/{id}", movieId))
                .andExpect(status().isNoContent());

        Optional<MovieEntity> deletedMovie = movieJpaRepository.findById(movieId);
        assertFalse(deletedMovie.isPresent());
    }

    @Test
    void deleteMovieTest_shouldReturnNotFound() throws Exception {
        // given
        Long nonExistentId = 99999L;

        // when & then
        mockMvc.perform(delete(MOVIES_API_URL + "/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    // -- HELPERS --

    private MovieEntity createMovie(Long tmdbId, String title, String originalTitle,
                                    LocalDate releaseDate, String status, String poster,
                                    List<String> genres, List<String> vodProviders) {
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTmdbId(tmdbId);
        movieEntity.setTitle(title);
        movieEntity.setOriginalTitle(originalTitle);
        movieEntity.setReleaseDate(releaseDate);
        movieEntity.setStatus(status);
        movieEntity.setPoster(poster);
        movieEntity.setGenres(genres);
        movieEntity.setVodProviders(vodProviders);
        movieEntity.setCreatedAt(MOCK_DATE);
        return movieEntity;
    }

    private Optional<MovieEntity> findByTitle(String title) {
        return movieJpaRepository.findAll().stream()
                .filter(g -> g.getTitle().equals(title))
                .findFirst();
    }

}