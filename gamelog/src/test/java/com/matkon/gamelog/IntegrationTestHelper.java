package com.matkon.gamelog;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class IntegrationTestHelper {

    public static final String RESPONSE_FILES_PATH = "src/test/resources/__files/response/";
    public static final String GAMES_API_URL = "/api/games";
    public static final String MOVIES_API_URL = "/api/movies";

    public static WireMockServer wireMockServer;

    public static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
    }

    public static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    public static void wiremockProperties(DynamicPropertyRegistry registry) {
        registry.add("rawg.api.url", () -> "http://localhost:" + wireMockServer.port() + "/api");
        registry.add("tmdb.api.url", () -> "http://localhost:" + wireMockServer.port());
    }

    public static String readJsonFile(String path, String fileName) {
        try {
            return Files.readString(Paths.get(path + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Can not read a file: " + fileName, e);
        }
    }
}