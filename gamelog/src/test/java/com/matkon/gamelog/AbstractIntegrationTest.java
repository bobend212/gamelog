package com.matkon.gamelog;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public abstract class AbstractIntegrationTest {

    public static final String RESPONSE_FILES_PATH = "src/test/resources/__files/response/";
    public static final String GAMES_API_URL = "/api/games";

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    @Container
    static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("thelibrarydb")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("docker-init/init-test-db.sql")
                    .withReuse(true);

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

    @DynamicPropertySource
    static void wiremockProperties(DynamicPropertyRegistry registry) {
        registry.add("rawg.api.url", () -> "http://localhost:" + wireMockServer.port() + "/api");
    }

    protected String readJsonFile(String path, String fileName) {
        try {
            return Files.readString(Paths.get(path + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Can not read a file: " + fileName, e);
        }
    }
}