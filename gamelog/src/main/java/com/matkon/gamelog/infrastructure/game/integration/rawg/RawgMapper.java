package com.matkon.gamelog.infrastructure.game.integration.rawg;

import com.matkon.gamelog.domain.game.model.Game;
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

}
