package com.matkon.gamelog.repos;

import com.matkon.gamelog.data.tvshow.TVShow;
import com.matkon.gamelog.data.tvshow.TVShowTrackingType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TVShowRepository extends JpaRepository<TVShow, Long> {
    Optional<TVShow> findByTmdbId(Long tmdbId);

    List<TVShow> findByTrackingType(TVShowTrackingType TVShowTrackingType, Sort sort);
}
