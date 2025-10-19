package com.matkon.gamelog.api.sync;

import com.matkon.gamelog.api.game.GameMapper;
import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/sync")
@CrossOrigin(origins = "*")
class SyncController {

    private final GameService gameService;
    private final GameMapper gameMapper;

    @PatchMapping("/games")
    @Operation(summary = "[RAWG API] Sync games data with RAWG API - by status")
    public ResponseEntity<SyncResponse> syncGames(@RequestParam(defaultValue = "WISHLIST") GameStatus status) {
        return ResponseEntity.ok(
                gameMapper.mapSyncResultToSyncResponse(
                        gameService.syncGamesByStatus(status)));
    }
}
