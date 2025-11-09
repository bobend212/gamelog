package com.matkon.gamelog.domain.movie.ports.in;

import com.matkon.gamelog.domain.common.sync.SyncResult;

public interface SyncAllMoviesUseCase {

    SyncResult syncAllMovies();
}
