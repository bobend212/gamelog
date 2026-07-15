package com.matkon.gamelog.infrastructure.integration.igdb;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameDetails;
import com.matkon.gamelog.domain.game.model.PlatformReleaseDates;
import com.matkon.gamelog.domain.game.model.ReleaseInfo;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbCover;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbGame;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbGameDetails;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbReleaseDate;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbReleaseStatus;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbScreenshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    @Mapping(target = "releaseDates", source = "releaseDates", qualifiedByName = "mapReleaseDates")
    GameDetails mapIgdbGameDetailsDtoToGameDetails(IgdbGameDetails igdbGameDetails);

    @Named("mapReleaseDates")
    default List<PlatformReleaseDates> mapReleaseDates(List<IgdbReleaseDate> dates) {
        if (dates == null || dates.isEmpty()) {
            return List.of();
        }

        return dates.stream()
                .filter(d -> d.getDate() != null && d.getPlatform() != null && d.getPlatform().getName() != null)
                .collect(Collectors.groupingBy(d -> d.getPlatform().getName()))
                .entrySet()
                .stream()
                .map(entry -> new PlatformReleaseDates(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(this::mapReleaseInfo)
                                .sorted(Comparator.comparing(ReleaseInfo::date))
                                .toList()
                ))
                .sorted(Comparator.comparing(PlatformReleaseDates::platform))
                .toList();
    }

    default ReleaseInfo mapReleaseInfo(IgdbReleaseDate d) {
        return new ReleaseInfo(
                Instant.ofEpochSecond(d.getDate())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate(),
                mapStatus(d.getStatus())
        );
    }

    default String mapStatus(IgdbReleaseStatus status) {
        if (status == null || status.getName() == null) {
            return "UNKNOWN";
        }

        return status.getName()
                .toUpperCase()
                .replace(" ", "_");
    }

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

    @Named("mapToLocalDate")
    static LocalDate mapToLocalDate(Long epoch) {
        return epoch != null
                ? Instant.ofEpochSecond(epoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                : null;
    }
}