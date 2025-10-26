package com.matkon.gamelog.api.sync;

import com.matkon.gamelog.domain.game.model.SyncResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncApiMapper {

    @Mapping(source = "fieldDifferences", target = "fieldChanges")
    SyncResponse mapSyncResultToSyncResponse(SyncResult syncResult);
}
