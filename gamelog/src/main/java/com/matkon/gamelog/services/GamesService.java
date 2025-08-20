package com.matkon.gamelog.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.games.Game;
import com.matkon.gamelog.data.games.GameSaveResult;
import com.matkon.gamelog.data.games.GameStatus;
import com.matkon.gamelog.data.games.GameUpdateRequest;
import com.matkon.gamelog.data.games.ReleaseFilter;
import com.matkon.gamelog.data.games.WishlistGameForTableDTO;
import com.matkon.gamelog.data.games.sync.FieldChange;
import com.matkon.gamelog.data.games.sync.GameChangeDetail;
import com.matkon.gamelog.data.games.sync.GameSyncResultDto;
import com.matkon.gamelog.repos.GamesRepository;
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
public class GamesService
{
    @Autowired
    private GamesRepository gamesRepository;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${rawg.api.url}")
    private String rawgApiUrl;

    @Value("${rawg.api.key}")
    private String rawgApiKey;

    public GamesService()
    {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public Page<Game> getWishlistGames(int page, int size, String searchTerm)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (searchTerm != null && !searchTerm.isBlank()) {
            return gamesRepository.findWishlistGames(GameStatus.WISHLIST, searchTerm, pageable);
        }

        return gamesRepository.findWishlistGames(GameStatus.WISHLIST, searchTerm, pageable);
    }

    public Page<WishlistGameForTableDTO> getWishlistGamesDashboard(int page, int size, String sort, ReleaseFilter releaseFilter)
    {
        String[] sortParts = sort.split(",");
        String field = sortParts[0].trim();
        Sort.Direction direction = (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, field));
        LocalDate today = LocalDate.now();

        Page<Game> games = switch (releaseFilter) {
            case RELEASED_ONLY ->
                    gamesRepository.findByStatusAndReleaseDateLessThanEqual(GameStatus.WISHLIST, today, pageable);
            case NOT_RELEASED_ONLY ->
                    gamesRepository.findByStatusAndReleaseDateAfter(GameStatus.WISHLIST, today, pageable);
            default -> gamesRepository.findByStatus(GameStatus.WISHLIST, pageable);
        };

        return games.map(WishlistGameForTableDTO::fromEntity);
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

        return gamesRepository.findLibraryGames(dbStatus, dbSearchTerm, pageable);
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
        Optional<Game> existingGame = gamesRepository.findByRawgId(rawgId);
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
                    Game savedGame = gamesRepository.save(game);
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
        gamesRepository.deleteById(gameId);
    }

    public Game updateGame(Long id, GameUpdateRequest updateRequest)
    {
        Game existingGame = gamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        existingGame.setPlatform(updateRequest.getPlatform());
        existingGame.setStatus(updateRequest.getStatus());
        existingGame.setRating(updateRequest.getRating());
        existingGame.setNotes(updateRequest.getNotes());
        existingGame.setCompletedAt(updateRequest.getCompletedAt());
        existingGame.setUpdatedAt(LocalDateTime.now());
        existingGame.setFavourite(updateRequest.getFavourite());

        return gamesRepository.save(existingGame);
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

    public GameSyncResultDto syncLibraryGames(GameStatus status)
    {
        List<Game> libraryGames = gamesRepository.findAll()
                .stream()
                .filter(game -> game.getStatus() == status)
                .toList();

        int updatedCount = 0;
        List<GameChangeDetail> changes = new ArrayList<>();

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
            if (areDatesDifferent(localGame.getReleaseDate(), latestData.getReleaseDate())) {
                fieldChanges.add(new FieldChange("Release_Date", String.valueOf(localGame.getReleaseDate()), String.valueOf(latestData.getReleaseDate())));
                localGame.setReleaseDate(latestData.getReleaseDate());
                changed = true;
            }

            // Title
            if (areStringsDifferent(localGame.getTitle(), latestData.getTitle())) {
                fieldChanges.add(new FieldChange("Title", localGame.getTitle(), latestData.getTitle()));
                localGame.setTitle(latestData.getTitle());
                changed = true;
            }

            // Image url
            if (areStringsDifferent(localGame.getImageUrl(), latestData.getImageUrl())) {
                fieldChanges.add(new FieldChange("Image_Url", localGame.getImageUrl(), latestData.getImageUrl()));
                localGame.setImageUrl(latestData.getImageUrl());
                changed = true;
            }

            if (changed) {
                gamesRepository.save(localGame);
                updatedCount++;
                changes.add(new GameChangeDetail(localGame.getId(), localGame.getTitle(), fieldChanges));
            }
        }

        return new GameSyncResultDto(libraryGames.size(), updatedCount, changes);
    }

    public boolean areDatesDifferent(LocalDate oldDate, LocalDate newDate)
    {
        if (oldDate == null && newDate == null) return false;           // both null = no change
        if (oldDate == null || newDate == null) return true;            // one null, one not = change
        return !oldDate.equals(newDate);                                // both non-null compare values
    }

    public boolean areStringsDifferent(String oldStr, String newStr)
    {
        if (oldStr == null && newStr == null) return false;          // both null = no change
        if (oldStr == null || newStr == null) return true;           // one null, one not = change
        return !oldStr.equals(newStr);                               // both non-null compare values
    }

}
