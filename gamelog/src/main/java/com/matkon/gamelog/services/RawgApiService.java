package com.matkon.gamelog.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.Game;
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
public class RawgApiService
{
    @Autowired
    private GameRepository gameRepository;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${rawg.api.url}")
    private String rawgApiUrl;

    @Value("${rawg.api.key}")
    private String rawgApiKey;

    public RawgApiService()
    {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    // Get all library games
//    public List<Game> getAllGames()
//    {
//        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
//        return gameRepository.findByStatusNot(GameStatus.WISHLIST, sort);
//    }

    public Page<Game> getAllGames(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return gameRepository.findByStatusNotOrderByPlayingFirstThenUpdatedAt(
                GameStatus.WISHLIST,
                GameStatus.PLAYING,
                pageable
        );
    }

    // Get all wishlist games
    public Page<Game> getWishlistGames(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return gameRepository.findByStatus(GameStatus.WISHLIST, pageable);
    }

    public List<Game> getGamesFromRawgByQuery(String query)
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

    public Page<Game> getLibraryGamesWithFilter(int page, int size, String status, String searchTerm)
    {
        Pageable pageable = PageRequest.of(page, size);

        // Convert string status to GameStatus enum
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

        // Handle search term
        String dbSearchTerm = (searchTerm == null || searchTerm.trim().isEmpty()) ? null : searchTerm;

        return gameRepository.findLibraryGamesWithFilter(dbStatus, dbSearchTerm, pageable);
    }

    public Game getByIdAndSaveGame(Long rawgId, GameStatus gameStatus)
    {
        Optional<Game> existingGame = gameRepository.findByRawgId(rawgId);
        if (existingGame.isPresent()) {
            return existingGame.get();
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
                    return saveGameToLibrary(game);
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching game: " + e.getMessage());
            return null;
        }

        throw new RuntimeException("Game not found with ID: " + rawgId);
    }

    public Game saveGameToLibrary(Game game)
    {
        return gameRepository.save(game);
    }

    private Game parseGameFromRawg(String response, Long rawgId)
    {
        try {
            JsonNode gameNode = objectMapper.readTree(response);
            Game game = createGameFromNode(gameNode);
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
                    Game game = createGameFromNode(gameNode);
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

    private Game createGameFromNode(JsonNode gameNode)
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

    // Search games method with WebClient
    public List<Game> searchGames(String query, int page, int pageSize)
    {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        String response = webClient.get()
                .uri(rawgApiUrl + "/games?key=" + rawgApiKey + "&search=" + query + "&page=" + page + "&page_size=" + pageSize)
                .retrieve()
                .bodyToMono(String.class)
                .block();


        return parseGamesFromResponse(response);
    }

    // Helper method to check if game exists in database
    public boolean gameExistsInDatabase(Long rawgId)
    {
        return gameRepository.existsByRawgId(rawgId);
    }

    // Delete game from database
    public void deleteGame(Long gameId)
    {
        gameRepository.deleteById(gameId);
    }

    public Optional<Game> getGameById(Long id)
    {
        return gameRepository.findById(id);
    }

    public Game updateGame(Long id, GameUpdateRequest updateRequest)
    {
        Game existingGame = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        // Update only the specified fields
        existingGame.setPlayedOn(updateRequest.getPlayedOn());
        existingGame.setStatus(updateRequest.getStatus());
        existingGame.setRating(updateRequest.getRating());
        existingGame.setNotes(updateRequest.getNotes());
        existingGame.setCompletedAt(updateRequest.getCompletedAt());

        // Update the updatedAt timestamp
        existingGame.setUpdatedAt(LocalDateTime.now());

        return gameRepository.save(existingGame);
    }
}
