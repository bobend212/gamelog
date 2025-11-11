package com.matkon.gamelog.infrastructure.integration.rawg;

import com.matkon.gamelog.common.exception.ItemNotFoundException;
import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.infrastructure.integration.rawg.dto.RawgGameInfoDto;
import com.matkon.gamelog.infrastructure.integration.rawg.dto.RawgSearchResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class RawgInfoAdapter implements GameInfoPort {

    private final RestClient restClient;
    private final RawgMapper rawgMapper;

    public RawgInfoAdapter(@Qualifier("rawgRestClient") RestClient restClient, RawgMapper rawgMapper) {
        this.restClient = restClient;
        this.rawgMapper = rawgMapper;
    }

    @Override
    public List<Game> searchGames(String query) {
        RawgSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("search", query)
                        .queryParam("page_size", 8)
                        .build())
                .retrieve()
                .body(RawgSearchResponse.class);

        if (response == null || response.getResults().isEmpty()) {
            throw new ItemNotFoundException("No games found matching the query: '%s'".formatted(query));
        }

        return response.getResults().stream()
                .map(rawgMapper::mapRawgGameInfoDtoToGame)
                .toList();
    }

    @Override
    public Game getGameDetails(Long rawgId) {
        RawgGameInfoDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games/" + rawgId)
                        .build())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, res) -> {
                    throw new ItemNotFoundException("Game with following ID '%s' does not exist in the API".formatted(rawgId));
                })
                .body(RawgGameInfoDto.class);

        if (response == null) {
            return null;
        }

        return rawgMapper.mapRawgGameInfoDtoToGame(response);
    }
}