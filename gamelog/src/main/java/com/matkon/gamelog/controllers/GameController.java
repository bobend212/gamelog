package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.game.Game;
import com.matkon.gamelog.data.game.GameReleaseFilter;
import com.matkon.gamelog.data.game.GameStatus;
import com.matkon.gamelog.data.game.dto.GameForWishlistDto;
import com.matkon.gamelog.data.game.dto.GameSaveResultDto;
import com.matkon.gamelog.data.game.dto.GameSearchResultDto;
import com.matkon.gamelog.data.game.dto.GameUpdateRequestDto;
import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.services.GameService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {

    private final GameService gameService;

    @GetMapping("/library")
    @Operation(summary = "Get LIBRARY games (wishlist excluded)")
    public ResponseEntity<Page<Game>> getLibraryGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "") String search) {
        Page<Game> games = gameService.getLibraryGames(page, size, status, search);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/wishlist")
    @Operation(summary = "Get WISHLIST games")
    public ResponseEntity<Page<Game>> getWishlistGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        Page<Game> wishlistGames = gameService.getWishlistGames(page, size, search);
        return ResponseEntity.ok(wishlistGames);
    }

    @GetMapping("/wishlist/dashboard")
    @Operation(summary = "Get WISHLIST games -> DASHBOARD TABLE")
    public ResponseEntity<Page<GameForWishlistDto>> getWishlistGamesDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "releaseDate,asc") String sort,
            @RequestParam(defaultValue = "ALL") GameReleaseFilter release
    ) {
        Page<GameForWishlistDto> games = gameService.getWishlistGamesDashboard(page, size, sort, release);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/search")
    @Operation(summary = "[RAWG API] Search games by query")
    public ResponseEntity<List<GameSearchResultDto>> searchGames(@RequestParam String query) {
        return ResponseEntity.ok(gameService.searchGames(query));
    }

    @PostMapping("/add/{rawgId}")
    @Operation(summary = "[RAWG API] Save to LIBRARY by rawgId")
    public ResponseEntity<GameSaveResultDto> saveGame(@PathVariable Long rawgId, @RequestParam(defaultValue = "BACKLOG") GameStatus gameStatus) {
        try {
            GameSaveResultDto result = gameService.saveGame(rawgId, gameStatus);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete game from database by id")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update game in database by id")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody GameUpdateRequestDto gameUpdate) {
        try {
            Game updatedGame = gameService.updateGame(id, gameUpdate);
            return ResponseEntity.ok(updatedGame);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/sync-library")
    @Operation(summary = "[RAWG API]  Sync library")
    public ResponseEntity<SyncResultDto> syncLibraryGames(@RequestParam(defaultValue = "WISHLIST") GameStatus status) {
        SyncResultDto result = gameService.syncGamesByStatus(status);
        return ResponseEntity.ok(result);
    }

}