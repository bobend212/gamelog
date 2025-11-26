package com.matkon.gamelog.domain.tvshow.ports.in;

public interface SetWatchCountTVShowUseCase {

    void setWatchedCount(Long seasonId, Integer watchedCount);
}
