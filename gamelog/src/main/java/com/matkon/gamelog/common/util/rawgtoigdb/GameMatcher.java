package com.matkon.gamelog.common.util.rawgtoigdb;

import com.matkon.gamelog.domain.game.model.Game;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class GameMatcher {

    public Optional<Game> findBestMatch(Game game, List<Game> candidates) {
        return candidates.stream()
                .map(candidate -> new ScoredGame(candidate, calculateScore(game, candidate)))
                .sorted((a, b) -> Double.compare(b.score(), a.score()))
                .peek(scored -> log.info("IGDB: '{}' releaseDate={} | score={}",
                        scored.game().getTitle(),
                        scored.game().getReleaseDate(),
                        scored.score()))
                .filter(scored -> scored.score() >= 0.7)
                .map(ScoredGame::game)
                .findFirst();
    }

    public Optional<ScoredGame> findBestMatch2(Game game, List<Game> candidates) {
        return candidates.stream()
                .map(candidate -> new ScoredGame(candidate, calculateScore(game, candidate)))
                .sorted((a, b) -> Double.compare(b.score(), a.score()))
                .peek(scored -> log.info("IGDB: '{}' releaseDate={} | score={}",
                        scored.game().getTitle(),
                        scored.score(),
                        scored.game().getReleaseDate()))
                .findFirst();
    }

    private double calculateScore(Game game, Game candidate) {
        double score = 0.0;

        String gameName = normalize(game.getTitle());
        String candidateName = normalize(candidate.getTitle());

        if (gameName.equals(candidateName)) {
            score += 0.6;
        } else if (candidateName.contains(gameName) || gameName.contains(candidateName)) {
            score += 0.4;
        }

        if (sameYear(game.getReleaseDate(), candidate.getReleaseDate())) {
            score += 0.4;
        }

        return score;
    }

    private String normalize(String name) {
        return name == null ? "" :
                name.toLowerCase()
                        .replaceAll("[^a-z0-9 ]", "")
                        .trim();
    }

    private boolean sameYear(LocalDate d1, LocalDate d2) {
        if (d1 == null || d2 == null) return false;
        return d1.getYear() == d2.getYear();
    }

    public record ScoredGame(Game game, double score) {
    }
}
