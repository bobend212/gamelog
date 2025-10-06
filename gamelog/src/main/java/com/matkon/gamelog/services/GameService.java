package com.matkon.gamelog.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.game.Game;
import com.matkon.gamelog.data.game.GameReleaseFilter;
import com.matkon.gamelog.data.game.GameStatus;
import com.matkon.gamelog.data.game.dto.GameForWishlistDto;
import com.matkon.gamelog.data.game.dto.GameSaveResultDto;
import com.matkon.gamelog.data.game.dto.GameSearchResultDto;
import com.matkon.gamelog.data.game.dto.GameUpdateRequestDto;
import com.matkon.gamelog.data.sync.ChangeDetail;
import com.matkon.gamelog.data.sync.FieldChange;
import com.matkon.gamelog.data.sync.SyncResultDto;
import com.matkon.gamelog.data.sync.SyncUtils;
import com.matkon.gamelog.repos.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${rawg.api.url}")
    private String rawgApiUrl;

    @Value("${rawg.api.key}")
    private String rawgApiKey;

    public GameService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public Page<Game> getWishlistGames(int page, int size, String searchTerm) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (searchTerm != null && !searchTerm.isBlank()) {
            return gameRepository.findWishlistGames(GameStatus.WISHLIST, searchTerm, pageable);
        }

        return gameRepository.findWishlistGames(GameStatus.WISHLIST, searchTerm, pageable);
    }

    public Page<GameForWishlistDto> getWishlistGamesDashboard(int page, int size, String sort, GameReleaseFilter gameReleaseFilter) {
        String[] sortParts = sort.split(",");
        String field = sortParts[0].trim();
        Sort.Direction direction = (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, field));
        LocalDate today = LocalDate.now();

        Page<Game> games = switch (gameReleaseFilter) {
            case RELEASED_ONLY ->
                    gameRepository.findByStatusAndReleaseDateLessThanEqual(GameStatus.WISHLIST, today, pageable);
            case NOT_RELEASED_ONLY ->
                    gameRepository.findByStatusAndReleaseDateAfter(GameStatus.WISHLIST, today, pageable);
            case TBA -> gameRepository.findByStatusAndReleaseDateIsNull(GameStatus.WISHLIST, pageable);
            default -> gameRepository.findByStatus(GameStatus.WISHLIST, pageable);
        };

        return games.map(GameForWishlistDto::fromEntity);
    }


    public Page<Game> getLibraryGames(int page, int size, String status, String searchTerm) {
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

    public List<GameSearchResultDto> searchGames(String query) {
        RAWGSearchResponse response = webClient.get()
                .uri(rawgApiUrl + "/games?key=" + rawgApiKey + "&search=" + query + "&page_size=8")
                .retrieve()
                .bodyToMono(RAWGSearchResponse.class)
                .block();

        if (response == null || response.results == null) return List.of();

        return response.results.stream()
                .map(game -> new GameSearchResultDto(game.id, game.name, game.background_image, parseDate(game.released)))
                .toList();
    }

    private LocalDate parseDate(String date) {
        return (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
    }

    public GameSaveResultDto saveGameToDatabase(Long rawgId, GameStatus gameStatus) {
        Optional<Game> existingGame = gameRepository.findByRawgId(rawgId);
        if (existingGame.isPresent()) {
            return new GameSaveResultDto(
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
                    return new GameSaveResultDto(
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


    public void deleteGame(Long gameId) {
        gameRepository.deleteById(gameId);
    }

    public Game updateGame(Long id, GameUpdateRequestDto updateRequest) {
        Game existingGame = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        existingGame.setPlatform(updateRequest.getPlatform());
        existingGame.setStatus(updateRequest.getStatus());
        existingGame.setRating(updateRequest.getRating());
        existingGame.setNotes(updateRequest.getNotes());
        existingGame.setCompletedAt(updateRequest.getCompletedAt());
        existingGame.setUpdatedAt(LocalDateTime.now());
        existingGame.setFavourite(updateRequest.isFavourite());

        return gameRepository.save(existingGame);
    }

    // -- RAWG Helpers

    private Game parseGameFromRawg(String response, Long rawgId) {
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

    private List<Game> parseGamesFromResponse(String response) {
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

    private Game createGameFromRAWGResponse(JsonNode gameNode) {
        try {
            Game game = new Game();

            if (gameNode.has("id")) {
                game.setRawgId(gameNode.get("id").asLong());
            }

            if (gameNode.has("name")) {
                game.setTitle(gameNode.get("name").asText());
            } else {
                game.setTitle("<no_title>");
            }

            if (gameNode.has("released") && !gameNode.get("released").isNull()) {
                String rawDate = gameNode.get("released").asText();

                try {
                    // RAWG typically uses "yyyy-MM-dd"
                    LocalDate releaseDate = LocalDate.parse(rawDate, DateTimeFormatter.ISO_LOCAL_DATE);
                    game.setReleaseDate(releaseDate);
                } catch (Exception e) {
                    System.err.println("Error parsing releaseDate: " + rawDate);
                }
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

    public SyncResultDto syncLibraryGames(GameStatus status) {
        List<Game> libraryGames = gameRepository.findAll()
                .stream()
                .filter(game -> game.getStatus() == status)
                .toList();

        int updatedCount = 0;
        List<ChangeDetail> changes = new ArrayList<>();

        for (Game localGame : libraryGames) {
            String response = webClient.get()
                    .uri(rawgApiUrl + "/games/" + localGame.getRawgId() + "?key=" + rawgApiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) continue;

            Game latestData = parseGameFromRawg(response, localGame.getRawgId());

            if (latestData == null) continue;

            List<FieldChange> fieldChanges = new ArrayList<>();
            boolean changed = false;

            // Release date
            if (SyncUtils.areDatesDifferent(localGame.getReleaseDate(), latestData.getReleaseDate())) {
                fieldChanges.add(new FieldChange("Release_Date", String.valueOf(localGame.getReleaseDate()), String.valueOf(latestData.getReleaseDate())));
                localGame.setReleaseDate(latestData.getReleaseDate());
                changed = true;
            }

            // Title
            if (SyncUtils.areStringsDifferent(localGame.getTitle(), latestData.getTitle())) {
                fieldChanges.add(new FieldChange("Title", localGame.getTitle(), latestData.getTitle()));
                localGame.setTitle(latestData.getTitle());
                changed = true;
            }

            // Image url
            if (SyncUtils.areStringsDifferent(localGame.getImageUrl(), latestData.getImageUrl())) {
                fieldChanges.add(new FieldChange("Image_Url", localGame.getImageUrl(), latestData.getImageUrl()));
                localGame.setImageUrl(latestData.getImageUrl());
                changed = true;
            }

            if (changed) {
                gameRepository.save(localGame);
                updatedCount++;
                changes.add(new ChangeDetail(localGame.getId(), localGame.getTitle(), fieldChanges));
            }
        }

        return new SyncResultDto(libraryGames.size(), updatedCount, changes);
    }

    private static class RAWGSearchResponse {
        public List<RAWGSearchResult> results;
    }

    private static class RAWGSearchResult {
        public Long id;
        public String name;
        public String background_image;
        public String released;
    }
}
