package com.matkon.gamelog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.matkon.gamelog.AbstractIntegrationTest;
import com.matkon.gamelog.data.game.Game;
import com.matkon.gamelog.data.game.GameStatus;
import com.matkon.gamelog.data.game.dto.GameUpdateRequestDto;
import com.matkon.gamelog.repos.GameRepository;
import com.matkon.gamelog.services.GameService;
import com.matkon.gamelog.services.RawgClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class GameControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    GameRepository gameRepository;

    @Mock
    private GameService gameService;

    @Mock
    private RawgClientService rawgClientService;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();

        Game game1 = createGame(1001L, "The Witcher 3", GameStatus.COMPLETED, 9.5,
                LocalDate.of(2015, 5, 19), true);
        Game game2 = createGame(1002L, "Cyberpunk 2077", GameStatus.PLAYING, 8.0,
                LocalDate.of(2020, 12, 10), false);
        Game game3 = createGame(1003L, "Baldur's Gate 3", GameStatus.WISHLIST, null,
                LocalDate.of(2023, 8, 3), false);
        Game game4 = createGame(1004L, "Elden Ring", GameStatus.BACKLOG, null,
                LocalDate.of(2022, 2, 25), false);

        gameRepository.saveAll(List.of(game1, game2, game3, game4));
    }

    @Test
    void getGamesTest_WishlistExcluded() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get(GAMES_API_URL + "/library")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "ALL")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "getLibraryGames_wishlistExcluded.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void getGamesTest_OnlyWishlist() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get(GAMES_API_URL + "/wishlist")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "ALL")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "getWishlistGames_onlyWishlist.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void getGamesTest_ForDashboard() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get(GAMES_API_URL + "/wishlist/dashboard")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "releaseDate,asc")
                        .param("release", "ALL"))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "getWishlistGames_ForDashboard.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void deleteGameTest() throws Exception {
        // given
        Game gameToDelete = findByTitle("Elden Ring").orElseThrow();
        Long gameId = gameToDelete.getId();

        // when & then
        mockMvc.perform(delete(GAMES_API_URL + "/{id}", gameId))
                .andExpect(status().isNoContent());

        Optional<Game> deletedGame = gameRepository.findById(gameId);
        assertFalse(deletedGame.isPresent());
    }

    @Test
    void updateGameTest() throws Exception {
        // given
        Game gameToUpdate = findByTitle("Cyberpunk 2077").orElseThrow();
        Long gameId = gameToUpdate.getId();

        GameUpdateRequestDto updateRequest = new GameUpdateRequestDto();
        updateRequest.setNotes("Amazing game after patches!");
        updateRequest.setRating(9.0);
        updateRequest.setStatus(GameStatus.COMPLETED);
        updateRequest.setFavourite(true);
        updateRequest.setPlatform("PC");

        // when
        MvcResult result = mockMvc.perform(put(GAMES_API_URL + "/{id}", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String actualResponse = result.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "updated_game_full.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);

        // verify in database
        Game updatedGame = gameRepository.findById(gameId).orElseThrow();
        assertAll("Verify updated game fields",
                () -> assertEquals("Amazing game after patches!", updatedGame.getNotes(), "Notes do not match"),
                () -> assertEquals(9.0, updatedGame.getRating(), "Rating does not match"),
                () -> assertEquals(GameStatus.COMPLETED, updatedGame.getStatus(), "Status does not match"),
                () -> assertTrue(updatedGame.isFavourite(), "Favourite flag should be true")
        );
    }

    @Test
    void updateGameTest_shouldReturnBadRequest() throws Exception {
        // given
        Long nonExistentId = 99999L;
        GameUpdateRequestDto updateRequest = new GameUpdateRequestDto();
        updateRequest.setNotes("This should fail");

        // when & then
        mockMvc.perform(put(GAMES_API_URL + "/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
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
    public void saveGameTest() throws Exception {
        Long rawgId = 5001L;

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/games/" + rawgId))
                .withQueryParam("key", equalTo("dummy-key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/rawg_get_game_response.json")));

        MvcResult mvcResult = mockMvc.perform(post("/api/games/add/{rawgId}", rawgId)
                        .param("gameStatus", GameStatus.PLAYING.name()))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = readJsonFile(RESPONSE_FILES_PATH, "saveGame_response.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse,
                new CustomComparator(JSONCompareMode.LENIENT,
                        new Customization("game.createdAt", (o1, o2) -> true),
                        new Customization("game.updatedAt", (o1, o2) -> true),
                        new Customization("game.id", (o1, o2) -> true)
                )
        );
    }

    @Test
    void saveGameTest_badRequest() throws Exception {
        Long rawgId = 9999L;

        Mockito.when(gameService.saveGame(rawgId, GameStatus.BACKLOG))
                .thenThrow(new RuntimeException("Error adding game"));

        mockMvc.perform(post("/api/games/add/{rawgId}", rawgId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void syncGamesTest() throws Exception {
        Long rawgId = 1003L;

        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/api/games/" + rawgId))
                .withQueryParam("key", equalTo("dummy-key"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/rawg_get_game_response.json")));

        MvcResult mvcResult = mockMvc.perform(patch("/api/games/sync-library")
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

    private Game createGame(Long rawgId, String title, GameStatus status, Double rating,
                            LocalDate releaseDate, boolean favourite) {
        Game game = new Game();
        game.setRawgId(rawgId);
        game.setTitle(title);
        game.setStatus(status);
        game.setRating(rating);
        game.setReleaseDate(releaseDate);
        game.setFavourite(favourite);
        game.setImageUrl("https://example.com/" + title.toLowerCase().replace(" ", "-") + ".jpg");
        game.setPlatform("PC");
        return game;
    }

    private Optional<Game> findByTitle(String title) {
        return gameRepository.findAll().stream()
                .filter(g -> g.getTitle().equals(title))
                .findFirst();
    }
}