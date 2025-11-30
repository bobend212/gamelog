package com.matkon.gamelog.infrastructure.tvshow.database;

import com.matkon.gamelog.domain.tvshow.model.TrackingType;
import com.matkon.gamelog.infrastructure.movie.database.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TVShowJpaRepository extends JpaRepository<TVShowEntity, Long> {

    @Query("SELECT t FROM TVShowEntity t " +
            "LEFT JOIN FETCH t.seasons " +
            "LEFT JOIN FETCH t.vodProviders " +
            "WHERE t.id = :id")
    Optional<TVShowEntity> findById(@Param("id") Long id);

    Optional<TVShowEntity> findByTmdbId(Long tmdbId);

    @Query("SELECT t FROM TVShowEntity t " +
            "LEFT JOIN FETCH t.vodProviders " +
            "LEFT JOIN FETCH t.seasons " +
            "WHERE (:search IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:trackingType IS NULL OR t.trackingType = :trackingType)")
    Page<TVShowEntity> findAllBySearchAndTrackingType(
            @Param("search") String search,
            @Param("trackingType") TrackingType trackingType,
            Pageable pageable);
}