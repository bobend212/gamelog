package com.matkon.gamelog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.AbstractIntegrationTest;
import com.matkon.gamelog.data.game.Game;
import com.matkon.gamelog.data.game.GameStatus;
import com.matkon.gamelog.data.game.dto.GameUpdateRequestDto;
import com.matkon.gamelog.repos.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
class GameControllerTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    GameRepository gameRepository;

    private static final String RESPONSE_FILES_PATH = "src/test/resources/__files/response/";
    private static final String GAMES_API_URL = "/api/games";

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
    void shouldGetLibraryGames_ExcludingWishlist() throws Exception {
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
        String expectedResponse = readJsonFile("getLibraryGames_wishlistExcluded.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldGetLibraryGames_OnlyWishlist() throws Exception {
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
        String expectedResponse = readJsonFile("getWishlistGames.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldGetLibraryGames_OnlyWishlistForDashboard() throws Exception {
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
        String expectedResponse = readJsonFile("getWishlistGames_ForDashboard.json");

        JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.LENIENT);
    }

    @Test
    void shouldDeleteGame() throws Exception {
        // given
        Game gameToDelete = findByTitle("Elden Ring").orElseThrow();
        Long gameId = gameToDelete.getId();

        // when & then
        mockMvc.perform(delete(GAMES_API_URL + "/{id}", gameId))
                .andExpect(status().isNoContent());

        // verify game was deleted
        assert gameRepository.findById(gameId).isEmpty();
    }

    @Test
    void shouldUpdateGame() throws Exception {
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
        String expectedResponse = readJsonFile("updated_game_full.json");

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
    void shouldReturnBadRequest_WhenUpdatingNonExistentGame() throws Exception {
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

    private String readJsonFile(String fileName) {
        try {
            return Files.readString(Paths.get(RESPONSE_FILES_PATH + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Nie można wczytać pliku: " + fileName, e);
        }
    }

    private Optional<Game> findByTitle(String title) {
        return gameRepository.findAll().stream()
                .filter(g -> g.getTitle().equals(title))
                .findFirst();
    }
}