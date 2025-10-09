package com.matkon.gamelog.services;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final RawgClientService rawgClientService;

    public GameService(GameRepository gameRepository, RawgClientService rawgClientService) {
        this.gameRepository = gameRepository;
        this.rawgClientService = rawgClientService;
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
        return rawgClientService.searchGames(query);
    }

    public GameSaveResultDto saveGame(Long rawgId, GameStatus gameStatus) {
        Optional<Game> existingGame = gameRepository.findByRawgId(rawgId);
        if (existingGame.isPresent()) {
            return new GameSaveResultDto(
                    existingGame.get(),
                    true,
                    "Game already exists in the library"
            );
        }

        try {
            Game game = rawgClientService.getGameDetails(rawgId);

            if (game != null) {
                game.setStatus(gameStatus);
                Game savedGame = gameRepository.save(game);
                return new GameSaveResultDto(
                        savedGame,
                        false,
                        "Game added successfully"
                );
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

    @Transactional
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

    public SyncResultDto syncGamesByStatus(GameStatus gameStatus) {
        List<Game> games = gameRepository.findAll()
                .stream()
                .filter(game -> game.getStatus() == gameStatus)
                .toList();

        int updatedCount = 0;
        List<ChangeDetail> changes = new ArrayList<>();

        for (Game localGame : games) {
            Game latestData = rawgClientService.getGameDetails(localGame.getRawgId());
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

        return new SyncResultDto(games.size(), updatedCount, changes);
    }
}