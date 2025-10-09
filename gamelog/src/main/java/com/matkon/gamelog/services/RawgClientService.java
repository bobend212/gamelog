package com.matkon.gamelog.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matkon.gamelog.data.game.Game;
import com.matkon.gamelog.data.game.dto.GameSearchResultDto;
import com.matkon.gamelog.data.game.dto.rawg.RawgSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RawgClientService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String rawgApiUrl;
    private final String rawgApiKey;

    public RawgClientService(WebClient.Builder webClientBuilder,
                             ObjectMapper objectMapper,
                             @Value("${rawg.api.url}") String rawgApiUrl,
                             @Value("${rawg.api.key}") String rawgApiKey) {
        this.webClient = webClientBuilder.baseUrl(rawgApiUrl).build();
        this.objectMapper = objectMapper;
        this.rawgApiUrl = rawgApiUrl;
        this.rawgApiKey = rawgApiKey;
    }

    public Game getGameDetails(Long rawgId) {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games/" + rawgId)
                        .queryParam("key", rawgApiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response == null) return null;

        return parseGameFromRawg(response, rawgId);
    }

    public List<GameSearchResultDto> searchGames(String query) {
        RawgSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("key", rawgApiKey)
                        .queryParam("search", query)
                        .queryParam("page_size", 8)
                        .build())
                .retrieve()
                .bodyToMono(RawgSearchResponse.class)
                .block();

        if (response == null || response.getResults() == null) {
            return List.of();
        }

        return response.getResults().stream()
                .map(game -> new GameSearchResultDto(
                        game.getId(),
                        game.getName(),
                        game.getBackground_image(),
                        parseDate(game.getReleased())
                ))
                .toList();
    }

    private LocalDate parseDate(String date) {
        return (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
    }

    private Game parseGameFromRawg(String response, Long rawgId) {
        try {
            JsonNode gameNode = objectMapper.readTree(response);
            Game game = createGameFromRAWGResponse(gameNode);
            if (game != null) {
                game.setRawgId(rawgId);
            }
            return game;
        } catch (Exception e) {
            System.err.println("Error parsing game response: " + e.getMessage());
            return null;
        }
    }

    private Game createGameFromRAWGResponse(JsonNode gameNode) {
        try {
            Game game = new Game();

            if (gameNode.has("id")) {
                game.setRawgId(gameNode.get("id").asLong());
            }

            if (gameNode.has("name")) {
                game.setTitle(gameNode.get("name").asText());
            } else {
                game.setTitle("<no_title>");
            }

            if (gameNode.has("released") && !gameNode.get("released").isNull()) {
                String rawDate = gameNode.get("released").asText();

                try {
                    // RAWG typically uses "yyyy-MM-dd"
                    LocalDate releaseDate = LocalDate.parse(rawDate, DateTimeFormatter.ISO_LOCAL_DATE);
                    game.setReleaseDate(releaseDate);
                } catch (Exception e) {
                    System.err.println("Error parsing releaseDate: " + rawDate);
                }
            }

            if (gameNode.has("background_image") && !gameNode.get("background_image").isNull()) {
                game.setImageUrl(gameNode.get("background_image").asText());
            }

            return game;
        } catch (Exception e) {
            System.err.println("Error creating game from node: " + e.getMessage());
            return null;
        }
    }
}
