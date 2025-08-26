package com.matkon.gamelog.controllers;

import com.matkon.gamelog.data.games.Game;
import com.matkon.gamelog.data.games.GameSaveResultDto;
import com.matkon.gamelog.data.games.GameSearchDto;
import com.matkon.gamelog.data.games.GameStatus;
import com.matkon.gamelog.data.games.GameUpdateRequest;
import com.matkon.gamelog.data.games.ReleaseFilter;
import com.matkon.gamelog.data.games.WishlistGameForTableDto;
import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.services.GamesService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GamesController
{
    private final GamesService gamesService;

    public GamesController(GamesService gamesService)
    {
        this.gamesService = gamesService;
    }

    @GetMapping("/library")
    @Operation(summary = "Get LIBRARY games (wishlist excluded)")
    public ResponseEntity<Page<Game>> getLibraryGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "") String search)
    {
        Page<Game> games = gamesService.getLibraryGames(page, size, status, search);
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
        Page<Game> wishlistGames = gamesService.getWishlistGames(page, size, search);
        return ResponseEntity.ok(wishlistGames);
    }

    @GetMapping("/wishlist/dashboard")
    @Operation(summary = "Get WISHLIST games -> DASHBOARD TABLE")
    public ResponseEntity<Page<WishlistGameForTableDto>> getWishlistGamesDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate,asc") String sort,
            @RequestParam(defaultValue = "ALL") ReleaseFilter release
    )
    {
        Page<WishlistGameForTableDto> games = gamesService.getWishlistGamesDashboard(page, size, sort, release);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/search")
    @Operation(summary = "[RAWG API] Search games by query")
    public ResponseEntity<List<GameSearchDto>> searchGames(@RequestParam String query)
    {
        return ResponseEntity.ok(gamesService.searchGames(query));
    }

    @PostMapping("/add-library/{rawgId}")
    @Operation(summary = "[RAWG API] Save to LIBRARY by rawgId")
    public ResponseEntity<GameSaveResultDto> addGameToLibrary(@PathVariable Long rawgId)
    {
        try {
            GameSaveResultDto result = gamesService.saveGameToDatabase(rawgId, GameStatus.BACKLOG);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add-wishlist/{rawgId}")
    @Operation(summary = "[RAWG API] Save to WISHLIST by rawgId")
    public ResponseEntity<GameSaveResultDto> addToWishlist(@PathVariable Long rawgId)
    {
        try {
            GameSaveResultDto result = gamesService.saveGameToDatabase(rawgId, GameStatus.WISHLIST);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete game from database by id")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id)
    {
        gamesService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update game in database by id")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody GameUpdateRequest gameUpdate)
    {
        try {
            Game updatedGame = gamesService.updateGame(id, gameUpdate);
            return ResponseEntity.ok(updatedGame);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/sync-library")
    @Operation(summary = "[RAWG API]  Sync library")
    public ResponseEntity<SyncResultDto> syncLibraryGames(@RequestParam(defaultValue = "WISHLIST") GameStatus status)
    {
        SyncResultDto result = gamesService.syncLibraryGames(status);
        return ResponseEntity.ok(result);
    }

}