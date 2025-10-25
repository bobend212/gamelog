package com.matkon.gamelog.api.game;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameUpdate;
import com.matkon.gamelog.domain.game.model.SyncResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface GameApiMapper {

    GameResponse mapGameToGameResponse(Game game);

    GameSearchResponse mapGameToGameSearchResponse(Game game);

    @Mapping(target = "daysToRelease", expression = "java(calculateDaysToRelease(game.getReleaseDate()))")
    @Mapping(target = "tba", expression = "java(game.getReleaseDate() == null)")
    @Mapping(target = "released", expression = "java(isReleased(game.getReleaseDate()))")
    GameWishlistResponse mapGameToGameWishlistResponse(Game game);

    GameUpdate mapGameUpdateRequestToGameUpdate(GameUpdateRequest gameUpdateRequest);

    @Mapping(source = "fieldDifferences", target = "fieldChanges")
    SyncResponse mapSyncResultToSyncResponse(SyncResult syncResult);

    // default methods
    default Long calculateDaysToRelease(LocalDate releaseDate) {
        if (releaseDate == null) return null;
        LocalDate today = LocalDate.now();
        return releaseDate.isAfter(today) ? ChronoUnit.DAYS.between(today, releaseDate) : null;
    }

    default boolean isReleased(LocalDate releaseDate) {
        return releaseDate != null && !releaseDate.isAfter(LocalDate.now());
    }
}
