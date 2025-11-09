package com.matkon.gamelog.infrastructure.movie.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieJpaRepository extends JpaRepository<MovieEntity, Long> {

    Optional<MovieEntity> findByTmdbId(Long tmdbId);

    @Query("SELECT DISTINCT m FROM MovieEntity m JOIN m.genres g " +
            "WHERE (LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(m.originalTitle) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(g) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MovieEntity> findMovies(Pageable pageable, @Param("search") String search);
}
