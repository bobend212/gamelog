package com.matkon.gamelog.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.Game;
import com.matkon.gamelog.data.GameSaveResult;
import com.matkon.gamelog.data.GameStatus;
import com.matkon.gamelog.data.GameUpdateRequest;
import com.matkon.gamelog.repos.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameService
{
    @Autowired
    private GameRepository gameRepository;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${rawg.api.url}")
    private String rawgApiUrl;

    @Value("${rawg.api.key}")
    private String rawgApiKey;

    public GameService()
    {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public Page<Game> getWishlistGames(int page, int size, String searchTerm)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (searchTerm != null && !searchTerm.isBlank()) {
            return gameRepository.findWishlistGames(GameStatus.WISHLIST, searchTerm, pageable);
        }

        return gameRepository.findWishlistGames(GameStatus.WISHLIST, searchTerm, pageable);
    }

    public Page<Game> getLibraryGames(int page, int size, String status, String searchTerm)
    {
        Pageable pageable = PageRequest.of(page, size);

        GameStatus dbStatus;
        if (status == null || "ALL".equals(status) || status.trim().isEmpty()) {
            dbStatus = null;
        } else {
            try {
                dbStatus = GameStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        String dbSearchTerm = (searchTerm == null || searchTerm.trim().isEmpty()) ? null : searchTerm;

        return gameRepository.findLibraryGames(dbStatus, dbSearchTerm, pageable);
    }

    public List<Game> searchGames(String query)
    {
        try {
            String response = webClient.get()
                    .uri(rawgApiUrl + "/games?key=" + rawgApiKey + "&search=" + query + "&page_size=8")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseGamesFromResponse(response);
        } catch (Exception e) {
            System.err.println("Error searching games: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public GameSaveResult saveGameToDatabase(Long rawgId, GameStatus gameStatus)
    {
        Optional<Game> existingGame = gameRepository.findByRawgId(rawgId);
        if (existingGame.isPresent()) {
            return new GameSaveResult(
                    existingGame.get(),
                    true,
                    "Game already exists in the library"
            );
        }

        try {
            String response = webClient.get()
                    .uri(rawgApiUrl + "/games/" + rawgId + "?key=" + rawgApiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null) {
                Game game = parseGameFromRawg(response, rawgId);
                if (game != null) {
                    game.setStatus(gameStatus);
                    Game savedGame = gameRepository.save(game);
                    return new GameSaveResult(
                            savedGame,
                            false,
                            "Game added successfully"
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching game: " + e.getMessage());
            throw new RuntimeException("Error adding game to library");
        }

        throw new RuntimeException("Game not found with ID: " + rawgId);
    }


    public void deleteGame(Long gameId)
    {
        gameRepository.deleteById(gameId);
    }

    public Game updateGame(Long id, GameUpdateRequest updateRequest)
    {
        Game existingGame = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        existingGame.setPlayedOn(updateRequest.getPlayedOn());
        existingGame.setStatus(updateRequest.getStatus());
        existingGame.setRating(updateRequest.getRating());
        existingGame.setNotes(updateRequest.getNotes());
        existingGame.setCompletedAt(updateRequest.getCompletedAt());
        existingGame.setUpdatedAt(LocalDateTime.now());

        return gameRepository.save(existingGame);
    }

    // -- RAWG Helpers

    private Game parseGameFromRawg(String response, Long rawgId)
    {
        try {
            JsonNode gameNode = objectMapper.readTree(response);
            Game game = createGameFromRAWGResponse(gameNode);
            if (game != null) {
                game.setRawgId(rawgId);
            }
            return game;
        } catch (Exception e) {
            System.err.println("Error parsing game response: " + e.getMessage());
            return null;
        }
    }

    private List<Game> parseGamesFromResponse(String response)
    {
        List<Game> games = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.get("results");

            if (results != null && results.isArray()) {
                for (JsonNode gameNode : results) {
                    Game game = createGameFromRAWGResponse(gameNode);
                    if (game != null) {
                        games.add(game);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing games response: " + e.getMessage());
        }
        return games;
    }

    private Game createGameFromRAWGResponse(JsonNode gameNode)
    {
        try {
            Game game = new Game();

            if (gameNode.has("id")) {
                game.setRawgId(gameNode.get("id").asLong());
            }

            if (gameNode.has("name")) {
                game.setTitle(gameNode.get("name").asText());
            }

            if (gameNode.has("released") && !gameNode.get("released").isNull()) {
                game.setReleaseDate(gameNode.get("released").asText());
            }

            if (gameNode.has("background_image") && !gameNode.get("background_image").isNull()) {
                game.setImageUrl(gameNode.get("background_image").asText());
            }

            return game;
        } catch (Exception e) {
            System.err.println("Error creating game from node: " + e.getMessage());
            return null;
        }
    }
}
