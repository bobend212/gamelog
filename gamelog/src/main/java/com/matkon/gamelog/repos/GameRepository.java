package com.matkon.gamelog.repos;

import com.matkon.gamelog.data.Game;
import com.matkon.gamelog.data.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>
{
    // Find game by RAWG API ID
    Optional<Game> findByRawgId(Long rawgId);

    // Find game by RAWG API ID
    Optional<Game> findById(Long id);

    // Find games by status
    Page<Game> findByStatus(GameStatus status, Pageable pageable);

    // Find games by name (case-insensitive)
    List<Game> findByTitleContainingIgnoreCase(String name);

    List<Game> findByStatusNot(GameStatus status, Sort sort);

    // Check if game exists by RAWG ID
    boolean existsByRawgId(Long rawgId);

    // Get recently updated games
    @Query("SELECT g FROM Game g ORDER BY g.updatedAt DESC")
    List<Game> findRecentlyUpdated();

    // Custom query to order by PLAYING status first, then by updatedAt
    @Query("SELECT g FROM Game g WHERE g.status != :excludedStatus " +
            "ORDER BY CASE WHEN g.status = :playingStatus THEN 0 ELSE 1 END, g.updatedAt DESC")
    Page<Game> findByStatusNotOrderByPlayingFirstThenUpdatedAt(
            @Param("excludedStatus") GameStatus excludedStatus,
            @Param("playingStatus") GameStatus playingStatus,
            Pageable pageable
    );

    @Query("SELECT g FROM Game g WHERE g.status != 'WISHLIST' " +
            "AND (:status IS NULL OR g.status = :status) " +
            "AND (:searchTerm IS NULL OR :searchTerm = '' OR LOWER(g.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY CASE WHEN g.status = 'PLAYING' THEN 0 ELSE 1 END, g.updatedAt DESC")
    Page<Game> findLibraryGamesWithFilter(
            @Param("status") GameStatus status,  // ✅ Changed to GameStatus
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );


}
