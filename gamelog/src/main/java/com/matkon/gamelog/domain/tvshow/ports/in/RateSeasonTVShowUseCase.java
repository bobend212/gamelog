package com.matkon.gamelog.domain.tvshow.ports.in;

public interface RateSeasonTVShowUseCase {

    void rateSeason(Long seasonId, Double rating);
}
