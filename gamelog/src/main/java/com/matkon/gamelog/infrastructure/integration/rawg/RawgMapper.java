package com.matkon.gamelog.infrastructure.integration.rawg;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameDetails;
import com.matkon.gamelog.infrastructure.integration.rawg.dto.RawgGameDetailsDto;
import com.matkon.gamelog.infrastructure.integration.rawg.dto.RawgGameInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface RawgMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "rawgId")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "background_image", target = "imageUrl")
    @Mapping(source = "released", target = "releaseDate")
    Game mapRawgGameInfoDtoToGame(RawgGameInfoDto rawgGameInfoDto);

    @Mapping(source = "updated", target = "updatedRawg")
    @Mapping(source = "website", target = "websiteUrl")
    @Mapping(source = "metacritic_url", target = "metacriticUrl")
    @Mapping(source = "background_image_additional", target = "additionalImageUrl")
    @Mapping(source = "platforms", target = "platforms")
    GameDetails mapRawgGameDetailsDtoToGameDetails(RawgGameDetailsDto rawgGameDetailsDto);
}
