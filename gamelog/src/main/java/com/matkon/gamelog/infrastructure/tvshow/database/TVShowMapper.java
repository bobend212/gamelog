package com.matkon.gamelog.infrastructure.tvshow.database;

import com.matkon.gamelog.domain.tvshow.model.Season;
import com.matkon.gamelog.domain.tvshow.model.TVShow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TVShowMapper {

    TVShow mapTVShowEntityToTVShow(TVShowEntity tvShowEntity);

    @Mapping(target = "seasons", ignore = true)
    TVShowEntity mapTVShowToTVShowEntity(TVShow tvShow);

    @Mapping(target = "series", ignore = true)
    Season mapSeasonEntityToSeason(SeasonEntity seasonEntity);

    SeasonEntity mapSeasonToSeasonEntity(Season season);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seasons", ignore = true)
    @Mapping(target = "vodProviders", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDomain(TVShow domain, @MappingTarget TVShowEntity entity);
}