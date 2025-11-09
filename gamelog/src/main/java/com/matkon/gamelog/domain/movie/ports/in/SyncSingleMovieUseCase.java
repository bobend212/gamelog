package com.matkon.gamelog.domain.movie.ports.in;

import com.matkon.gamelog.domain.common.sync.SyncResult;

public interface SyncSingleMovieUseCase {

    SyncResult syncSingleMovie(Long movieId);
}
