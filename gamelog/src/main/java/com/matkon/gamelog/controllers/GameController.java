package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.Game;
import com.matkon.gamelog.data.GameSaveResult;
import com.matkon.gamelog.data.GameStatus;
import com.matkon.gamelog.data.GameUpdateRequest;
import com.matkon.gamelog.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController
{
    private final GameService gameService;

    public GameController(GameService gameService)
    {
        this.gameService = gameService;
    }

    @GetMapping("/library")
    @Operation(summary = "Get LIBRARY games (wishlist excluded)")
    public ResponseEntity<Page<Game>> getLibraryGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "") String search)
    {

        Page<Game> games = gameService.getLibraryGames(page, size, status, search);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/wishlist")
    @Operation(summary = "Get WISHLIST games")
    public ResponseEntity<Page<Game>> getWishlistGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String search
    )
    {
        Page<Game> wishlistGames = gameService.getWishlistGames(page, size, search);
        return ResponseEntity.ok(wishlistGames);
    }

    @GetMapping("/search")
    @Operation(summary = "[RAWG API] Search games by query")
    public ResponseEntity<?> searchGames(@RequestParam String query)
    {
        try {
            return ResponseEntity.ok(gameService.searchGames(query));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error searching external games: " + e.getMessage());
        }
    }

    @PostMapping("/add-library/{rawgId}")
    @Operation(summary = "[RAWG API] Save to LIBRARY by rawgId")
    public ResponseEntity<GameSaveResult> addGameToLibrary(@PathVariable Long rawgId)
    {
        try {
            GameSaveResult result = gameService.saveGameToDatabase(rawgId, GameStatus.BACKLOG);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add-wishlist/{rawgId}")
    @Operation(summary = "[RAWG API] Save to WISHLIST by rawgId")
    public ResponseEntity<GameSaveResult> addToWishlist(@PathVariable Long rawgId)
    {
        try {
            GameSaveResult result = gameService.saveGameToDatabase(rawgId, GameStatus.WISHLIST);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete game from database by id")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id)
    {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update game in database by id")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody GameUpdateRequest gameUpdate)
    {
        try {
            Game updatedGame = gameService.updateGame(id, gameUpdate);
            return ResponseEntity.ok(updatedGame);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}