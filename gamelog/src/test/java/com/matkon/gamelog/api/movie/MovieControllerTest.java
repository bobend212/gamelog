package com.matkon.gamelog.api.movie;

import com.matkon.gamelog.IntegrationTest;
import com.matkon.gamelog.infrastructure.movie.database.MovieEntity;
import com.matkon.gamelog.infrastructure.movie.database.MovieJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static com.matkon.gamelog.IntegrationTestHelper.MOVIES_API_URL;
import static com.matkon.gamelog.IntegrationTestHelper.RESPONSE_FILES_PATH;
import static com.matkon.gamelog.IntegrationTestHelper.readJsonFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class MovieControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MovieJpaRepository movieJpaRepository;

    @BeforeEach
    void setUp() {
        movieJpaRepository.deleteAll();

        MovieEntity me1 = createMovie(1L, "Wiedźmin", "The Wither", LocalDate.of(2015, 5, 19),
                "Released", "/1s4OrFrEOpP3Pb89ETNxSDQyvQX.jpg", List.of("Horror", "Fantasy"), List.of("Netflix", "HBO"));

        MovieEntity me2 = createMovie(2L, "Paranormalna misja", "Paranormal Mission", LocalDate.of(2025, 3, 12),
                "Released", "/x8TdWFrEOpMaPb8AENSxGvQyvC9.jpg", List.of("Horror", "Thriller"), List.of("Netflix", "Prime Video"));

        MovieEntity me3 = createMovie(3L, "Bugonia", "Bugonia", LocalDate.of(2025, 6, 7),
                "Released", "/bugonia987sdsa3.jpg", List.of("Sci-Fi", "Mystery"), List.of("HBO", "Disney+"));

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
        return movieEntity;
    }

}