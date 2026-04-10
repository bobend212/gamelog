package com.matkon.gamelog.infrastructure.integration.igdb;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameDetails;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbCover;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbGame;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbGameDetails;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbScreenshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring")
public interface IgdbMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "igdbId", source = "id")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "releaseDate", source = "firstReleaseDate", qualifiedByName = "mapToLocalDate")
    @Mapping(target = "imageUrl", source = "cover", qualifiedByName = "mapImageUrl")
    Game magIgdbGameResponseToGame(IgdbGame source);

    @Mapping(target = "igdbId", source = "id")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "releaseDate", source = "firstReleaseDate", qualifiedByName = "mapToLocalDate")
    @Mapping(target = "imageUrl", source = "cover", qualifiedByName = "mapImageUrl")
    Game mapIgdbGameResponseToGameForMatcher(IgdbGame response);

    @Mapping(target = "igdbUrl", source = "url")
    @Mapping(target = "igdbLastUpdated", source = "updatedAt", qualifiedByName = "mapToLocalDate")
    @Mapping(target = "additionalImageUrl", source = "screenshots", qualifiedByName = "mapFirstScreenshot")
    GameDetails mapIgdbGameDetailsDtoToGameDetails(IgdbGameDetails igdbGameDetails);

    @Named("mapFirstScreenshot")
    default String mapFirstScreenshot(List<IgdbScreenshot> screenshots) {
        if (screenshots == null || screenshots.isEmpty()) {
            return null;
        }

        return "https://images.igdb.com/igdb/image/upload/t_1080p/"
                + screenshots.get(0).imageId()
                + ".jpg";
    }

    @Named("mapImageUrl")
    default String mapImageUrl(IgdbCover cover) {
        if (cover == null || cover.imageId() == null) {
            return null;
        }

        return "https://images.igdb.com/igdb/image/upload/t_1080p/"
                + cover.imageId()
                + ".jpg";
    }

    @Named("mapEpochToInstant")
    static Instant mapEpochToInstant(Long epoch) {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }

    @Named("mapToLocalDate")
    static LocalDate mapToLocalDate(Long epoch) {
        return epoch != null
                ? Instant.ofEpochSecond(epoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                : null;
    }
}
