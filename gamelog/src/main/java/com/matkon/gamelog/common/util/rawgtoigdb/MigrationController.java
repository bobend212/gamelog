package com.matkon.gamelog.common.util.rawgtoigdb;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.infrastructure.integration.igdb.IgdbInfoAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/migration")
public class MigrationController {

    private final GameMatchingService gameMatchingService;
    private final IgdbInfoAdapter igdbClient;

    @PostMapping("/test-igdb-matching")
    public ResponseEntity<Void> testMatching() {
        gameMatchingService.migrateToIgdb();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/game/{igdbId}")
    public ResponseEntity<Game> getGameDetails(@PathVariable Long igdbId) {

        Game game = igdbClient.getGameById(igdbId);

        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(game);
    }

    @PostMapping("/sync-igdb")
    public ResponseEntity<Void> sync() {
        gameMatchingService.syncFromIgdb();
        return ResponseEntity.ok().build();
    }
}
