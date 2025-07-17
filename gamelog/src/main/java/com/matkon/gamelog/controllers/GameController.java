package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.Game;
import com.matkon.gamelog.data.GameStatus;
import com.matkon.gamelog.data.GameUpdateRequest;
import com.matkon.gamelog.services.RawgApiService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController
{
    private final RawgApiService rawgApiService;

    public GameController(RawgApiService rawgApiService)
    {
        this.rawgApiService = rawgApiService;
    }

    @GetMapping("/all-games")
    @Operation(summary = "Get all library games (wishlist excluded)")
    public ResponseEntity<List<Game>> getAllGames()
    {
        List<Game> games = rawgApiService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/wishlist")
    @Operation(summary = "Get all wishlist games")
    public ResponseEntity<List<Game>> getWishlistGames()
    {
        List<Game> games = rawgApiService.getWishlistGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/search")
    @Operation(summary = "[RAWG API] Search games by query")
    public ResponseEntity<?> searchExternalGames(@RequestParam String query)
    {
        try {
            return ResponseEntity.ok(rawgApiService.getGamesFromRawgByQuery(query));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error searching external games: " + e.getMessage());
        }
    }

    @GetMapping("/add-library/{rawgId}")
    @Operation(summary = "[RAWG API] Get game by rawgId and save to Library")
    public ResponseEntity<?> addToLibrary(@PathVariable Long rawgId)
    {
        try {
            return ResponseEntity.ok(rawgApiService.getByIdAndSaveGame(rawgId, GameStatus.BACKLOG));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error searching external games: " + e.getMessage());
        }
    }

    @GetMapping("/add-wishlist/{rawgId}")
    @Operation(summary = "[RAWG API] Get game by rawgId and save to Wishlist")
    public ResponseEntity<?> addToWishlist(@PathVariable Long rawgId)
    {
        try {
            return ResponseEntity.ok(rawgApiService.getByIdAndSaveGame(rawgId, GameStatus.WISHLIST));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error searching external games: " + e.getMessage());
        }
    }

    // Add search endpoint
    @GetMapping("/advanced-search")
    @Operation(summary = "TODO")
    public ResponseEntity<List<Game>> searchGames(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize)
    {
        List<Game> games = rawgApiService.searchGames(query, page, pageSize);
        return ResponseEntity.ok(games);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete game from Library by id")
    public ResponseEntity<Void> deleteGameFromLibrary(@PathVariable Long id)
    {
        rawgApiService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update game in the Library")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody GameUpdateRequest gameUpdate)
    {
        try {
            Game updatedGame = rawgApiService.updateGame(id, gameUpdate);
            return ResponseEntity.ok(updatedGame);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}