package com.matkon.gamelog.repos;

import com.matkon.gamelog.data.Game;
import com.matkon.gamelog.data.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    List<Game> findByStatus(GameStatus category);

    // Find games by name (case-insensitive)
    List<Game> findByTitleContainingIgnoreCase(String name);

    List<Game> findByStatusNot(GameStatus status);

    // Check if game exists by RAWG ID
    boolean existsByRawgId(Long rawgId);

    // Get recently updated games
    @Query("SELECT g FROM Game g ORDER BY g.updatedAt DESC")
    List<Game> findRecentlyUpdated();
}
