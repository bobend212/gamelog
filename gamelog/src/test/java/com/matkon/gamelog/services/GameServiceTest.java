package com.matkon.gamelog.services;

import com.matkon.gamelog.data.game.Game;
import com.matkon.gamelog.data.game.GameReleaseFilter;
import com.matkon.gamelog.data.game.GameStatus;
import com.matkon.gamelog.data.game.dto.GameForWishlistDto;
import com.matkon.gamelog.repos.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RawgClientService rawgClientService;

    private GameService gameService;

    @BeforeEach
    void setup() {
        gameService = new GameService(gameRepository, rawgClientService);
    }

    @Test
    void testGetWishlistGames_withSearchTerm_callsRepositoryWithSearchTerm() {
        int page = 0;
        int size = 5;
        String searchTerm = "lego";

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Game> expectedPage = new PageImpl<>(List.of(new Game()));

        when(gameRepository.findWishlistGames(GameStatus.WISHLIST, searchTerm, expectedPageable))
                .thenReturn(expectedPage);

        Page<Game> result = gameService.getWishlistGames(page, size, searchTerm);

        assertSame(expectedPage, result);
        verify(gameRepository).findWishlistGames(GameStatus.WISHLIST, searchTerm, expectedPageable);
    }

    @Test
    void testGetWishlistGames_withoutSearchTerm_callsRepositoryWithNullSearchTerm() {
        int page = 1;
        int size = 10;
        String searchTerm = null;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Game> expectedPage = new PageImpl<>(List.of(new Game(), new Game()));

        when(gameRepository.findWishlistGames(GameStatus.WISHLIST, null, expectedPageable))
                .thenReturn(expectedPage);

        Page<Game> result = gameService.getWishlistGames(page, size, searchTerm);

        assertSame(expectedPage, result);
        verify(gameRepository).findWishlistGames(GameStatus.WISHLIST, null, expectedPageable);
    }

    private Page<Game> createDummyGamesPage() {
        Game game = new Game();
        game.setId(1L);
        game.setTitle("Test Game");
        game.setStatus(GameStatus.WISHLIST);
        game.setReleaseDate(LocalDate.of(2023, 1, 1));
        game.setUpdatedAt(LocalDateTime.now());

        return new PageImpl<>(List.of(game));
    }

    @Test
    void testGetWishlistGamesDashboard_WithReleasedOnlyFilter_SortsDescending() {
        int page = 0;
        int size = 5;
        String sort = "updatedAt,desc";
        GameReleaseFilter filter = GameReleaseFilter.RELEASED_ONLY;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Game> dummyPage = createDummyGamesPage();

        when(gameRepository.findByStatusAndReleaseDateLessThanEqual(eq(GameStatus.WISHLIST), any(LocalDate.class), eq(expectedPageable)))
                .thenReturn(dummyPage);

        Page<GameForWishlistDto> result = gameService.getWishlistGamesDashboard(page, size, sort, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gameRepository).findByStatusAndReleaseDateLessThanEqual(eq(GameStatus.WISHLIST), any(LocalDate.class), eq(expectedPageable));
    }

    @Test
    void testGetWishlistGamesDashboard_WithNotReleasedOnlyFilter_SortsAscending() {
        int page = 1;
        int size = 10;
        String sort = "title,asc";
        GameReleaseFilter filter = GameReleaseFilter.NOT_RELEASED_ONLY;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "title"));
        Page<Game> dummyPage = createDummyGamesPage();

        when(gameRepository.findByStatusAndReleaseDateAfter(eq(GameStatus.WISHLIST), any(LocalDate.class), eq(expectedPageable)))
                .thenReturn(dummyPage);

        Page<GameForWishlistDto> result = gameService.getWishlistGamesDashboard(page, size, sort, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gameRepository).findByStatusAndReleaseDateAfter(eq(GameStatus.WISHLIST), any(LocalDate.class), eq(expectedPageable));
    }

    @Test
    void testGetWishlistGamesDashboard_WithTbaFilter() {
        int page = 2;
        int size = 7;
        String sort = "releaseDate";
        GameReleaseFilter filter = GameReleaseFilter.TBA;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "releaseDate"));
        Page<Game> dummyPage = createDummyGamesPage();

        when(gameRepository.findByStatusAndReleaseDateIsNull(eq(GameStatus.WISHLIST), eq(expectedPageable)))
                .thenReturn(dummyPage);

        Page<GameForWishlistDto> result = gameService.getWishlistGamesDashboard(page, size, sort, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gameRepository).findByStatusAndReleaseDateIsNull(eq(GameStatus.WISHLIST), eq(expectedPageable));
    }

    @Test
    void testGetWishlistGamesDashboard_WithDefaultFilter() {
        int page = 3;
        int size = 6;
        String sort = "title,desc";
        GameReleaseFilter filter = GameReleaseFilter.ALL;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
        Page<Game> dummyPage = createDummyGamesPage();

        when(gameRepository.findByStatus(eq(GameStatus.WISHLIST), eq(expectedPageable)))
                .thenReturn(dummyPage);

        Page<GameForWishlistDto> result = gameService.getWishlistGamesDashboard(page, size, sort, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gameRepository).findByStatus(eq(GameStatus.WISHLIST), eq(expectedPageable));
    }
}