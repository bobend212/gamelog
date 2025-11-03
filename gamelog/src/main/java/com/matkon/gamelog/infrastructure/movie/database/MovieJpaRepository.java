package com.matkon.gamelog.infrastructure.movie.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieJpaRepository extends JpaRepository<MovieEntity, Long> {

    Optional<MovieEntity> findByTmdbId(Long tmdbId);

    Page<MovieEntity> findAll(Pageable pageable);
}
