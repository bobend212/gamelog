package com.matkon.gamelog.repos;

import com.matkon.gamelog.data.tvshow.TVShow;
import com.matkon.gamelog.data.tvshow.TrackingType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TVShowRepository extends JpaRepository<TVShow, Long> {
    Optional<TVShow> findByTmdbId(Long tmdbId);

    List<TVShow> findByTrackingType(TrackingType trackingType, Sort sort);

    @Query("SELECT tv FROM TVShow tv LEFT JOIN FETCH tv.seasons WHERE tv.id = :id")
    Optional<TVShow> findByIdWithSeasons(@Param("id") Long id);

    @EntityGraph(attributePaths = {"seasons"})
    Optional<TVShow> findById(Long id);
}
