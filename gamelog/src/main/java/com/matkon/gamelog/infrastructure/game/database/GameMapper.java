package com.matkon.gamelog.infrastructure.game.database;

import com.matkon.gamelog.domain.game.model.Game;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {

    Game mapGameEntityToGame(GameEntity gameEntity);

    GameEntity mapGameToGameEntity(Game game);
}
