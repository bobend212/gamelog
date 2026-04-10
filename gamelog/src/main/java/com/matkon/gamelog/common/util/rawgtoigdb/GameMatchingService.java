package com.matkon.gamelog.common.util.rawgtoigdb;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.infrastructure.game.database.GameEntity;
import com.matkon.gamelog.infrastructure.game.database.GameJpaRepository;
import com.matkon.gamelog.infrastructure.game.database.GameMapper;
import com.matkon.gamelog.infrastructure.integration.igdb.IgdbInfoAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameMatchingService {

    private final GameJpaRepository gameJpaRepository;
    private final IgdbInfoAdapter igdbClient;
    private final GameMatcher matcher;
    private final GameMapper gameMapper;

    public void testMatching() {

        List<Game> games = gameJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "updatedAt"))
                .stream().map(gameMapper::mapGameEntityToGame).toList();

        for (Game game : games) {
            log.info("RAWG: '{}' releaseDate={}", game.getTitle(), game.getReleaseDate());

            List<Game> candidates = igdbClient.searchGames(game.getTitle());

            matcher.findBestMatch(game, candidates)
                    .ifPresentOrElse(
                            best -> log.info("MATCH | '{}' -> '{}' (igdb_id={})\n",
                                    game.getTitle(),
                                    best.getTitle(),
                                    best.getId()),
                            () -> log.warn("NO MATCH | '{}'\n", game.getTitle())
                    );
        }
    }

    public void migrateToIgdb() {

        List<GameEntity> entities = gameJpaRepository.findAll(
                Sort.by(Sort.Direction.ASC, "updatedAt")
        );

        int counter = 1;

        for (GameEntity entity : entities) {
            log.info("RAWG: '{}' releaseDate={}", entity.getTitle(), entity.getReleaseDate());
            if (entity.getIgdbId() != null) {
                continue;
            }

            Game game = gameMapper.mapGameEntityToGame(entity);

            log.info("Processing ({} of {}): '{}'", counter, entities.size(), game.getTitle());

            List<Game> candidates = igdbClient.searchGames(game.getTitle());

            matcher.findBestMatch2(game, candidates)
                    .ifPresentOrElse(
                            scored -> {
                                if (scored.score() == 1.0) {

                                    Game best = scored.game();

                                    entity.setIgdbId(best.getId());
                                    gameJpaRepository.save(entity);

                                    log.info("UPDATED | (igdbId={}, score={})\n",
                                            best.getId(),
                                            scored.score());

                                } else {
                                    log.warn("SKIPPED (low score) | '{}' bestMatch='{}' score={}\n",
                                            game.getTitle(),
                                            scored.game().getTitle(),
                                            scored.score());
                                }
                            },
                            () -> log.warn("NO MATCH | '{}'", game.getTitle())
                    );

            counter++;
        }
    }

    public void syncFromIgdb() {
        List<GameEntity> entities = gameJpaRepository.findAll();
        int counter = 1;

        for (GameEntity entity : entities) {
            if (entity.getIgdbId() == null) {
                continue;
            }

            try {
                Game igdbGame = igdbClient.getGameById(entity.getIgdbId());

                if (igdbGame == null) {
                    log.warn("IGDB NOT FOUND | igdbId={}\n", entity.getIgdbId());
                    continue;
                }

                StringBuilder changes = new StringBuilder();
                boolean updated = false;

                if (igdbGame.getTitle() != null && !igdbGame.getTitle().equals(entity.getTitle())) {
                    changes.append("title: '")
                            .append(entity.getTitle())
                            .append("' -> '")
                            .append(igdbGame.getTitle())
                            .append("'\n");
                    entity.setTitle(igdbGame.getTitle());
                    updated = true;
                }

                if (igdbGame.getReleaseDate() != null &&
                        !igdbGame.getReleaseDate().equals(entity.getReleaseDate())) {
                    changes.append("releaseDate: '")
                            .append(entity.getReleaseDate())
                            .append("' -> '")
                            .append(igdbGame.getReleaseDate())
                            .append("'\n");
                    entity.setReleaseDate(igdbGame.getReleaseDate());
                    updated = true;
                }

                if (igdbGame.getImageUrl() != null &&
                        !igdbGame.getImageUrl().equals(entity.getImageUrl())) {
                    changes.append("imageUrl: '")
                            .append(entity.getImageUrl())
                            .append("' -> '")
                            .append(igdbGame.getImageUrl())
                            .append("'\n");
                    entity.setImageUrl(igdbGame.getImageUrl());
                    updated = true;
                }

                if (updated) {
                    gameJpaRepository.save(entity);

                    log.info("""
                                    
                                    ===== GAME UPDATED ({}/{}) =====
                                    igdbId: {}
                                    {}
                                    """,
                            counter,
                            entities.size(),
                            entity.getIgdbId(),
                            changes.toString()
                    );
                } else {
                    log.info("""
                                    
                                    ----- NO CHANGE ({}/{}) -----
                                    '{}' (igdbId={})
                                    
                                    """,
                            counter,
                            entities.size(),
                            entity.getTitle(),
                            entity.getIgdbId()
                    );
                }

                Thread.sleep(100);

            } catch (Exception e) {
                log.error("ERROR syncing '{}' (igdbId={})\n",
                        entity.getTitle(),
                        entity.getIgdbId(),
                        e);
            }

            counter++;
        }
    }

}
