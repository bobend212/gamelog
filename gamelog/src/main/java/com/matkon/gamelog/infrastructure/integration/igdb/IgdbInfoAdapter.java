package com.matkon.gamelog.infrastructure.integration.igdb;

import com.matkon.gamelog.domain.game.model.Game;
import com.matkon.gamelog.domain.game.model.GameDetails;
import com.matkon.gamelog.domain.game.ports.out.GameInfoPort;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbGame;
import com.matkon.gamelog.infrastructure.integration.igdb.dto.IgdbGameDetails;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
@Primary
public class IgdbInfoAdapter implements GameInfoPort {

    private final RestClient restClient;
    private final IgdbMapper mapper;

    public IgdbInfoAdapter(@Qualifier("igdbRestClient") RestClient restClient, IgdbMapper mapper) {
        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Override
    public List<Game> searchGames(String title) {
        String query = """
                search "%s";
                fields id, name, first_release_date, cover.image_id;
                where version_parent = null;
                limit 8;
                """.formatted(title);

        IgdbGame[] response = restClient.post()
                .uri("/games")
                .body(query)
                .retrieve()
                .body(IgdbGame[].class);

        if (response == null) {
            return List.of();
        }

        return Arrays.stream(response)
                .map(mapper::magIgdbGameResponseToGame)
                .toList();
    }

    @Override
    public Game getGame(Long externalId) {
        String query = """
                fields id, name, release_dates.date, release_dates.status.name, release_dates.release_region , cover.image_id;
                where id = %d;
                """.formatted(externalId);

        IgdbGame[] response = restClient.post()
                .uri("/games")
                .body(query)
                .retrieve()
                .body(IgdbGame[].class);

        if (response == null || response.length == 0) {
            return null;
        }

        return mapper.magIgdbGameResponseToGame(response[0]);
    }

    @Override
    public GameDetails getGameDetails(Long externalId) {
        String query = """
                fields storyline,
                       summary,
                       updated_at,
                       url,
                       screenshots.image_id,
                       release_dates.date,
                       release_dates.platform.name,
                       release_dates.status.name;
                where id = %d;
                """.formatted(externalId);

        IgdbGameDetails[] response = restClient.post()
                .uri("/games")
                .body(query)
                .retrieve()
                .body(IgdbGameDetails[].class);

        if (response == null || response.length == 0) {
            return null;
        }

        return mapper.mapIgdbGameDetailsDtoToGameDetails(response[0]);
    }

    public Game getGameById(Long igdbId) {

        String query = """
                fields id, name, first_release_date, cover.image_id;
                where id = %d;
                """.formatted(igdbId);

        IgdbGame[] response = restClient.post()
                .uri("/games")
                .body(query)
                .retrieve()
                .body(IgdbGame[].class);

        if (response == null || response.length == 0) {
            return null;
        }

        return mapper.mapIgdbGameResponseToGameForMatcher(response[0]);
    }
}