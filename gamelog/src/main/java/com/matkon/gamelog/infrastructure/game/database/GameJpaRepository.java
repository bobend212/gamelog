package com.matkon.gamelog.infrastructure.game.database;

import com.matkon.gamelog.domain.game.model.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface GameJpaRepository extends JpaRepository<GameEntity, Long> {

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
}
