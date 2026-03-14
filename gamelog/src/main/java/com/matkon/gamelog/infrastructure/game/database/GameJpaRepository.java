package com.matkon.gamelog.infrastructure.game.database;

import com.matkon.gamelog.domain.game.model.GameStatus;
import com.matkon.gamelog.domain.game.model.dashboard.PlatformStatDto;
import com.matkon.gamelog.domain.game.model.dashboard.RecentGameDto;
import com.matkon.gamelog.domain.game.model.dashboard.YearCompletionStatDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameJpaRepository extends JpaRepository<GameEntity, Long> {

    Optional<GameEntity> findByRawgId(Long rawgId);

    Optional<GameEntity> findById(Long id);

    @Query("""
                SELECT g FROM GameEntity g
                WHERE (:status IS NULL OR g.status = :status)
                  AND (:searchTerm IS NULL OR :searchTerm = '' OR LOWER(g.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
                ORDER BY CASE WHEN g.status = 'PLAYING' THEN 0 ELSE 1 END, g.updatedAt DESC
            """)
    Page<GameEntity> findGamesByStatus(
            @Param("status") GameStatus status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("SELECT COUNT(g) FROM GameEntity g")
    long countTotal();

    @Query("""
            SELECT COUNT(g)
            FROM GameEntity g
            WHERE g.status = :status
            """)
    long countByStatus(@Param("status") GameStatus status);

    @Query("""
            SELECT ROUND(AVG(g.rating), 1)
            FROM GameEntity g
            WHERE g.rating IS NOT NULL
            """)
    Double averageRating();

    @Query("""
            SELECT new com.matkon.gamelog.domain.game.model.dashboard.PlatformStatDto(g.platform, COUNT(g))
            FROM GameEntity g
            WHERE g.platform IS NOT NULL
            GROUP BY g.platform
            """)
    List<PlatformStatDto> platformStats();

    @Query("""
            SELECT new com.matkon.gamelog.domain.game.model.dashboard.YearCompletionStatDto(
                YEAR(g.completedAt),
                COUNT(g)
            )
            FROM GameEntity g
            WHERE g.completedAt IS NOT NULL
            GROUP BY YEAR(g.completedAt)
            ORDER BY YEAR(g.completedAt)
            """)
    List<YearCompletionStatDto> completionsPerYear();

    @Query("""
            SELECT new com.matkon.gamelog.domain.game.model.dashboard.RecentGameDto(
                g.rawgId,
                g.title,
                g.completedAt
            )
            FROM GameEntity g
            WHERE g.status = 'COMPLETED' AND g.completedAt IS NOT NULL
            ORDER BY g.completedAt DESC
            """)
    List<RecentGameDto> recentlyCompleted(Pageable pageable);
}
