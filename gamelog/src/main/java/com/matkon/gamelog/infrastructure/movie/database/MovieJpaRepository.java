package com.matkon.gamelog.infrastructure.movie.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieJpaRepository extends JpaRepository<MovieEntity, Long> {

    @Query("SELECT m FROM MovieEntity m " +
            "LEFT JOIN FETCH m.genres " +
            "LEFT JOIN FETCH m.vodProviders " +
            "WHERE m.id = :id")
    Optional<MovieEntity> findById(@Param("id") Long id);

    Optional<MovieEntity> findByTmdbId(Long tmdbId);

    @EntityGraph(attributePaths = {"genres", "vodProviders"})
    @Query(
            value = """
                    SELECT DISTINCT m
                    FROM MovieEntity m
                    LEFT JOIN m.genres g
                    LEFT JOIN m.vodProviders vp
                    WHERE
                          LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(m.originalTitle) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(g) LIKE LOWER(CONCAT('%', :search, '%'))
                       OR LOWER(vp) LIKE LOWER(CONCAT('%', :search, '%'))
                    ORDER BY m.releaseDate DESC
                    """,
            countQuery = """
                     SELECT COUNT(DISTINCT m)
                     FROM MovieEntity m
                     LEFT JOIN m.genres g
                     LEFT JOIN m.vodProviders vp
                     WHERE
                           LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR LOWER(m.originalTitle) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR LOWER(g) LIKE LOWER(CONCAT('%', :search, '%'))
                        OR LOWER(vp) LIKE LOWER(CONCAT('%', :search, '%'))
                    """
    )
    Page<MovieEntity> findMovies(Pageable pageable, @Param("search") String search);
}
