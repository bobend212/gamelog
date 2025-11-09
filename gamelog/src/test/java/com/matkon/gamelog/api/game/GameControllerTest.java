package com.matkon.gamelog.api.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.matkon.gamelog.IntegrationTest;
import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.infrastructure.game.database.GameEntity;
import com.matkon.gamelog.infrastructure.game.database.GameJpaRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.matkon.gamelog.IntegrationTestHelper.GAMES_API_URL;
import static com.matkon.gamelog.IntegrationTestHelper.RESPONSE_FILES_PATH;
import static com.matkon.gamelog.IntegrationTestHelper.readJsonFile;
import static com.matkon.gamelog.IntegrationTestHelper.startWireMock;
import static com.matkon.gamelog.IntegrationTestHelper.stopWireMock;
import static com.matkon.gamelog.IntegrationTestHelper.wireMockServer;
import static com.matkon.gamelog.IntegrationTestHelper.wiremockProperties;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class GameControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    GameJpaRepository gameJpaRepository;

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
        gameJpaRepository.deleteAll();

        GameEntity gameEntity1 = createGame(1001L, "The Witcher 3", GameStatus.COMPLETED, 9.5,
                LocalDate.of(2015, 5, 19), true);
        GameEntity gameEntity2 = createGame(1002L, "Cyberpunk 2077", GameStatus.COMPLETED, 8.0,
                LocalDate.of(2020, 12, 10), false);
        GameEntity gameEntity3 = createGame(1003L, "Baldur's Gate 3", GameStatus.WISHLIST, null,
                LocalDate.of(2023, 8, 3), false);
        GameEntity gameEntity4 = createGame(1004L, "Elden Ring", GameStatus.BACKLOG, null,
                LocalDate.of(2022, 2, 25), false);

        gameJpaRepository.saveAll(List.of(gameEntity1, gameEntity2, gameEntity3, gameEntity4));
    }

    @Test
    void getGamesTest_All() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get(GAMES_API_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "ALL")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "getGames_all_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void getGamesTest_SpecifiedStatus() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get(GAMES_API_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "COMPLETED")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "getGames_specifiedStatus_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void getWishlistGamesTest() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get(GAMES_API_URL + "/wishlist")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "getWishlistGames_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void deleteGameTest() throws Exception {
        // given
        GameEntity gameEntityToDelete = findByTitle("Elden Ring").orElseThrow();
        Long gameId = gameEntityToDelete.getId();

        // when & then
        mockMvc.perform(delete(GAMES_API_URL + "/{id}", gameId))
                .andExpect(status().isNoContent());

        Optional<GameEntity> deletedGame = gameJpaRepository.findById(gameId);
        assertFalse(deletedGame.isPresent());
    }

    @Test
    void deleteGameTest_shouldReturnNotFound() throws Exception {
        // given
        Long nonExistentId = 99999L;

        // when & then
        mockMvc.perform(delete(GAMES_API_URL + "/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateGameTest() throws Exception {
        // given
        GameEntity gameEntityToUpdate = findByTitle("Cyberpunk 2077").orElseThrow();
        Long gameId = gameEntityToUpdate.getId();

        GameUpdateRequest updateRequest = new GameUpdateRequest();
        updateRequest.setNotes("Amazing game after patches!");
        updateRequest.setRating(9.0);
        updateRequest.setStatus(GameStatus.COMPLETED);
        updateRequest.setFavourite(true);
        updateRequest.setPlatform("PC");

        // when
        MvcResult result = mockMvc.perform(patch(GAMES_API_URL + "/{id}", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "updateGame_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);

        // verify in database
        GameEntity updatedGameEntity = gameJpaRepository.findById(gameId).orElseThrow();
        assertAll("Verify updated game fields",
                () -> assertEquals("Amazing game after patches!", updatedGameEntity.getNotes(), "Notes do not match"),
                () -> assertEquals(9.0, updatedGameEntity.getRating(), "Rating does not match"),
                () -> assertEquals(GameStatus.COMPLETED, updatedGameEntity.getStatus(), "Status does not match"),
                () -> assertTrue(updatedGameEntity.isFavourite(), "Favourite flag should be true")
        );
    }

    @Test
    void updateGameTest_shouldReturnNotFound() throws Exception {
        // given
        Long nonExistentId = 99999L;
        GameUpdateRequest updateRequest = new GameUpdateRequest();
        updateRequest.setNotes("This should fail");

        // when & then
        mockMvc.perform(patch(GAMES_API_URL + "/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchGamesTest() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(GAMES_API_URL))
                .withQueryParam("key", equalTo("dummy-key"))
                .withQueryParam("search", equalTo("witcher"))
                .withQueryParam("page_size", equalTo("8"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/rawg_search_games_response.json")));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(GAMES_API_URL + "/search")
                        .param("query", "witcher"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "searchGame_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    public void searchGamesTest_shouldReturnNotFound() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(GAMES_API_URL))
                .withQueryParam("key", equalTo("dummy-key"))
                .withQueryParam("search", equalTo("non-existing-title"))
                .withQueryParam("page_size", equalTo("8"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile(null)));

        mockMvc.perform(MockMvcRequestBuilders.get(GAMES_API_URL + "/search")
                        .param("query", "non-existing-title"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void saveGameTest() throws Exception {
        Long rawgId = 5001L;

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/games/" + rawgId))
                .withQueryParam("key", equalTo("dummy-key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/rawg_get_game_response.json")));

        MvcResult mvcResult = mockMvc.perform(post(GAMES_API_URL + "/{rawgId}", rawgId)
                        .param("gameStatus", GameStatus.PLAYING.name()))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "saveGame_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    public void saveGameTest_shouldThrowGameAlreadyExistException() throws Exception {
        mockMvc.perform(post(GAMES_API_URL + "/{rawgId}", 1001L)
                        .param("gameStatus", GameStatus.PLAYING.name()))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void saveGameTest_shouldThrowGameNotFoundException() throws Exception {
        Long rawgId = 5001L;

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/games/" + rawgId))
                .withQueryParam("key", equalTo("dummy-key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/rawg_get_game_response.json")));

        mockMvc.perform(post(GAMES_API_URL + "/{rawgId}", 5005L)
                        .param("gameStatus", GameStatus.PLAYING.name()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void syncGamesTest() throws Exception {
        Long rawgId = 1003L;

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/games/" + rawgId))
                .withQueryParam("key", equalTo("dummy-key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/rawg_get_game_response.json")));

        MvcResult mvcResult = mockMvc.perform(patch("/api/sync/games")
                        .param("status", "WISHLIST"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "syncGames_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse,
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("changes[0].mediaId", (o1, o2) -> true)
                )
        );
    }

    // -- HELPERS --

    private GameEntity createGame(Long rawgId, String title, GameStatus status, Double rating,
                                  LocalDate releaseDate, boolean favourite) {
        GameEntity gameEntity = new GameEntity();
        gameEntity.setRawgId(rawgId);
        gameEntity.setTitle(title);
        gameEntity.setStatus(status);
        gameEntity.setRating(rating);
        gameEntity.setReleaseDate(releaseDate);
        gameEntity.setFavourite(favourite);
        gameEntity.setImageUrl("https://example.com/" + title.toLowerCase().replace(" ", "-") + ".jpg");
        gameEntity.setPlatform("PC");
        return gameEntity;
    }

    private Optional<GameEntity> findByTitle(String title) {
        return gameJpaRepository.findAll().stream()
                .filter(g -> g.getTitle().equals(title))
                .findFirst();
    }
}