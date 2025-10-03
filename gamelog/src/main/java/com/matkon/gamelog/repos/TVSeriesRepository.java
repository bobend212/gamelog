package com.matkon.gamelog.repos;

import com.matkon.gamelog.data.tvseries.TVSeries;
import com.matkon.gamelog.data.tvseries.TrackingType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TVSeriesRepository extends JpaRepository<TVSeries, Long> {
    Optional<TVSeries> findByTmdbId(Long tmdbId);

    List<TVSeries> findByTrackingType(TrackingType trackingType, Sort sort);
}
