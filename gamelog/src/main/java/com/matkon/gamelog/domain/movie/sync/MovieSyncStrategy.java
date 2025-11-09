package com.matkon.gamelog.domain.movie.sync;

import com.matkon.gamelog.domain.common.sync.SyncResult;

public interface MovieSyncStrategy {

    SyncResult sync(MovieSyncContext context);

    boolean supports(MovieSyncContext context);
}

