package com.matkon.gamelog.repos;

import com.matkon.gamelog.data.Game;
import com.matkon.gamelog.data.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>
{
    // Find game by RAWG ID
    Optional<Game> findByRawgId(Long rawgId);

    // Find game by GAME ID
    Optional<Game> findById(Long id);

    @Query("SELECT g FROM Game g WHERE g.status = :status " +
            "AND (:searchTerm IS NULL OR :searchTerm = '' OR LOWER(g.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY g.updatedAt DESC")
    Page<Game> findWishlistGames(
            @Param("status") GameStatus status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("SELECT g FROM Game g WHERE g.status != 'WISHLIST' " +
            "AND (:status IS NULL OR g.status = :status) " +
            "AND (:searchTerm IS NULL OR :searchTerm = '' OR LOWER(g.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY CASE WHEN g.status = 'PLAYING' THEN 0 ELSE 1 END, g.updatedAt DESC")
    Page<Game> findLibraryGames(
            @Param("status") GameStatus status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    // for Wishlist table in Dashboard
    Page<Game> findByStatus(GameStatus status, Pageable pageable);

    Page<Game> findByStatusAndReleaseDateLessThanEqual(GameStatus status, LocalDate date, Pageable pageable);

    Page<Game> findByStatusAndReleaseDateAfter(GameStatus status, LocalDate date, Pageable pageable);
}
