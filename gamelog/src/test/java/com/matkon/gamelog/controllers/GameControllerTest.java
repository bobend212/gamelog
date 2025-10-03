package com.matkon.gamelog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.game.Game;
import com.matkon.gamelog.data.game.dto.GameSaveResultDto;
import com.matkon.gamelog.data.game.dto.GameSearchResultDto;
import com.matkon.gamelog.data.game.GameStatus;
import com.matkon.gamelog.data.game.dto.GameUpdateRequestDto;
import com.matkon.gamelog.data.game.GameReleaseFilter;
import com.matkon.gamelog.data.game.dto.GameForWishlistDto;
import com.matkon.gamelog.data.sync.ChangeDetail;
import com.matkon.gamelog.data.sync.FieldChange;
import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.services.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    GameService gameService;

    @Test
    void getLibraryGamesTest() throws Exception {
        Game game = new Game();
        game.setTitle("title1");
        Game game2 = new Game();
        game2.setTitle("title2");
        List<Game> games = List.of(game, game2);
        Page<Game> gamePage = new PageImpl<>(games);

        when(gameService.getLibraryGames(0, 8, "ALL", "test"))
                .thenReturn(gamePage);

        mockMvc.perform(get("/api/games/library")
                        .param("page", "0")
                        .param("size", "8")
                        .param("status", "ALL")
                        .param("search", "test"))
                .andExpect(status().isOk());

        verify(gameService).getLibraryGames(anyInt(), anyInt(), eq("ALL"), eq("test"));
    }

    @Test
    void getWishlistGamesTest() throws Exception {
        Game game = new Game();
        game.setTitle("title1");
        Game game2 = new Game();
        game2.setTitle("title2");
        List<Game> games = List.of(game, game2);
        Page<Game> gamePage = new PageImpl<>(games);

        when(gameService.getWishlistGames(0, 8, "test"))
                .thenReturn(gamePage);

        mockMvc.perform(get("/api/games/wishlist")
                        .param("page", "0")
                        .param("size", "8")
                        .param("search", "test"))
                .andExpect(status().isOk());

        verify(gameService).getWishlistGames(anyInt(), anyInt(), eq("test"));
    }

    @Test
    void getWishlistGamesDashboardTest() throws Exception {
        GameForWishlistDto game = new GameForWishlistDto();
        GameForWishlistDto game2 = new GameForWishlistDto();
        List<GameForWishlistDto> games = List.of(game, game2);
        Page<GameForWishlistDto> gamePage = new PageImpl<>(games);

        when(gameService.getWishlistGamesDashboard(0, 50, "releaseDate,asc", GameReleaseFilter.ALL))
                .thenReturn(gamePage);

        mockMvc.perform(get("/api/games/wishlist/dashboard")
                        .param("page", "0")
                        .param("size", "8")
                        .param("sort", "releaseDate,asc")
                        .param("release", GameReleaseFilter.ALL.name()))
                .andExpect(status().isOk());

        verify(gameService).getWishlistGamesDashboard(anyInt(), anyInt(), eq("releaseDate,asc"), eq(GameReleaseFilter.ALL));
    }

    @Test
    void searchGamesTest() throws Exception {

        GameSearchResultDto dto = new GameSearchResultDto(123L, "Test Game", "http://image.jpg", LocalDate.of(2023, 6, 15));
        List<GameSearchResultDto> dtoList = List.of(dto);

        when(gameService.searchGames("test")).thenReturn(dtoList);

        mockMvc.perform(get("/api/games/search")
                        .param("query", "test"))
                .andExpect(status().isOk());

        verify(gameService).searchGames("test");
    }

    @Test
    void addGameToLibrarySuccessTest() throws Exception {
        Long rawgId = 1L;
        GameSaveResultDto result = new GameSaveResultDto(new Game(), false, "ok");

        when(gameService.saveGameToDatabase(rawgId, GameStatus.BACKLOG))
                .thenReturn(result);

        mockMvc.perform(post("/api/games/add-library/{rawgId}", rawgId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(result.getMessage()));
    }

    @Test
    void addGameToLibraryFailureTest() throws Exception {
        Long rawgId = 1L;

        when(gameService.saveGameToDatabase(rawgId, GameStatus.BACKLOG))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/games/add-library/{rawgId}", rawgId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addGameToWishlistSuccessTest() throws Exception {
        Long rawgId = 1L;
        GameSaveResultDto result = new GameSaveResultDto(new Game(), false, "ok");

        when(gameService.saveGameToDatabase(rawgId, GameStatus.WISHLIST))
                .thenReturn(result);

        mockMvc.perform(post("/api/games/add-wishlist/{rawgId}", rawgId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(result.getMessage()));
    }

    @Test
    void addGameToWishlistFailureTest() throws Exception {
        Long rawgId = 1L;

        when(gameService.saveGameToDatabase(rawgId, GameStatus.WISHLIST))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/api/games/add-wishlist/{rawgId}", rawgId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteGameTest() throws Exception {
        mockMvc.perform(delete("/api/games/" + 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateGameSuccessTest() throws Exception {
        Long gameId = 1L;
        String updatedNotesText = "notes text";
        GameUpdateRequestDto updateRequest = new GameUpdateRequestDto();
        updateRequest.setNotes(updatedNotesText);

        Game updatedGame = new Game();
        updatedGame.setId(gameId);
        updatedGame.setNotes(updatedNotesText);

        when(gameService.updateGame(eq(gameId), any(GameUpdateRequestDto.class)))
                .thenReturn(updatedGame);

        mockMvc.perform(put("/api/games/{id}", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.notes").value(updatedNotesText));
    }

    @Test
    void updateGameBadRequestTest() throws Exception {
        Long gameId = 3L;
        GameUpdateRequestDto updateRequest = new GameUpdateRequestDto();

        when(gameService.updateGame(eq(gameId), any(GameUpdateRequestDto.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(put("/api/games/{id}", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void syncLibraryGamesTest() throws Exception {
        FieldChange change1 = new FieldChange("a", "b", "c");
        FieldChange change2 = new FieldChange("a2", "b2", "c2");
        List<FieldChange> changes1 = List.of(change1);
        List<FieldChange> changes2 = List.of(change2);
        List<ChangeDetail> changeDetails = List.of(
                new ChangeDetail(1L, "game", changes1),
                new ChangeDetail(1L, "game", changes2)
        );

        SyncResultDto syncResultDto = new SyncResultDto(2, 2, changeDetails);

        when(gameService.syncLibraryGames(eq(GameStatus.WISHLIST)))
                .thenReturn(syncResultDto);

        mockMvc.perform(patch("/api/games/sync-library")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalChecked").value(2))
                .andExpect(jsonPath("$.updatedCount").value(2));
    }
}