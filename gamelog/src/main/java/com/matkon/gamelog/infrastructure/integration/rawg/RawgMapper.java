package com.matkon.gamelog.infrastructure.integration.rawg;

import com.matkon.gamelog.domain.game.model.Game;
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

}
