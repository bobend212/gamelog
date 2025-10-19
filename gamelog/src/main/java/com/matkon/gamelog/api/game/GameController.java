package com.matkon.gamelog.api.game;

import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.GameUpdate;
import com.matkon.gamelog.domain.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
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

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;

    @GetMapping()
    @Operation(summary = "Get ALL games - by status and search query")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<Page<GameResponse>> getGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(
                gameService.getGames(page, size, status, search)
                        .map(gameMapper::mapGameToGameResponse)
        );
    }

    @GetMapping("/wishlist")
    @Operation(summary = "Get only WISHLIST games - by search query")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    public ResponseEntity<Page<GameWishlistResponse>> getWishlistGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String search
    ) {
        return ResponseEntity.ok(
                gameService.getGames(page, size, GameStatus.WISHLIST.name(), search)
                        .map(gameMapper::mapGameToGameWishlistResponse)
        );
    }

    @GetMapping("/search")
    @Operation(summary = "[RAWG API] Search games - by query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found - Nothing found by query.")
    })
    public ResponseEntity<List<GameSearchResponse>> searchGames(@RequestParam String query) {
        return ResponseEntity.ok(
                gameService.searchGames(query)
                        .stream().map(gameMapper::mapGameToGameSearchResponse).toList());
    }

    @PostMapping("/save/{rawgId}")
    @Operation(summary = "[RAWG API] Save game - by rawgId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added to library."),
            @ApiResponse(responseCode = "409", description = "Conflict - The game already exist in the db."),
            @ApiResponse(responseCode = "404", description = "Not Found - The game does not exist in the API")
    })
    public ResponseEntity<GameResponse> saveGame(@PathVariable Long rawgId,
                                                 @RequestParam(defaultValue = "BACKLOG") GameStatus gameStatus) {
        return ResponseEntity.ok(gameMapper.mapGameToGameResponse(
                gameService.saveGame(rawgId, gameStatus)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete game - by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully removed game from library."),
            @ApiResponse(responseCode = "404", description = "Not found - The game was not found.")
    })
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update game - by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the game."),
            @ApiResponse(responseCode = "404", description = "Not found - The game was not found."),
    })
    public ResponseEntity<GameResponse> updateGame(@PathVariable Long id, @RequestBody GameUpdateRequest gameUpdateRequest) {

        GameUpdate gameUpdate = gameMapper.mapGameUpdateRequestToGameUpdate(gameUpdateRequest);
        return ResponseEntity.ok(
                gameMapper.mapGameToGameResponse(
                        gameService.updateGame(id, gameUpdate)));

    }
}