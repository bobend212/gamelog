package com.matkon.gamelog.services;

import com.matkon.gamelog.data.games.Game;
import com.matkon.gamelog.data.games.GameStatus;
import com.matkon.gamelog.data.games.ReleaseFilter;
import com.matkon.gamelog.data.games.WishlistGameForTableDto;
import com.matkon.gamelog.repos.GamesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

@ExtendWith(MockitoExtension.class)
public class GamesServiceTest {

    @Mock
    private GamesRepository gamesRepository;

    @InjectMocks
    private GamesService gameService;

    @Test
    void testGetWishlistGames_withSearchTerm_callsRepositoryWithSearchTerm() {
        int page = 0;
        int size = 5;
        String searchTerm = "lego";

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Game> expectedPage = new PageImpl<>(List.of(new Game()));

        when(gamesRepository.findWishlistGames(GameStatus.WISHLIST, searchTerm, expectedPageable))
                .thenReturn(expectedPage);

        Page<Game> result = gameService.getWishlistGames(page, size, searchTerm);

        assertSame(expectedPage, result);
        verify(gamesRepository).findWishlistGames(GameStatus.WISHLIST, searchTerm, expectedPageable);
    }

    @Test
    void testGetWishlistGames_withoutSearchTerm_callsRepositoryWithNullSearchTerm() {
        int page = 1;
        int size = 10;
        String searchTerm = null;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Game> expectedPage = new PageImpl<>(List.of(new Game(), new Game()));

        when(gamesRepository.findWishlistGames(GameStatus.WISHLIST, null, expectedPageable))
                .thenReturn(expectedPage);

        Page<Game> result = gameService.getWishlistGames(page, size, searchTerm);

        assertSame(expectedPage, result);
        verify(gamesRepository).findWishlistGames(GameStatus.WISHLIST, null, expectedPageable);
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
        ReleaseFilter filter = ReleaseFilter.RELEASED_ONLY;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Game> dummyPage = createDummyGamesPage();

        when(gamesRepository.findByStatusAndReleaseDateLessThanEqual(eq(GameStatus.WISHLIST), any(LocalDate.class), eq(expectedPageable)))
                .thenReturn(dummyPage);

        Page<WishlistGameForTableDto> result = gameService.getWishlistGamesDashboard(page, size, sort, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gamesRepository).findByStatusAndReleaseDateLessThanEqual(eq(GameStatus.WISHLIST), any(LocalDate.class), eq(expectedPageable));
    }

    @Test
    void testGetWishlistGamesDashboard_WithNotReleasedOnlyFilter_SortsAscending() {
        int page = 1;
        int size = 10;
        String sort = "title,asc";
        ReleaseFilter filter = ReleaseFilter.NOT_RELEASED_ONLY;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "title"));
        Page<Game> dummyPage = createDummyGamesPage();

        when(gamesRepository.findByStatusAndReleaseDateAfter(eq(GameStatus.WISHLIST), any(LocalDate.class), eq(expectedPageable)))
                .thenReturn(dummyPage);

        Page<WishlistGameForTableDto> result = gameService.getWishlistGamesDashboard(page, size, sort, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gamesRepository).findByStatusAndReleaseDateAfter(eq(GameStatus.WISHLIST), any(LocalDate.class), eq(expectedPageable));
    }

    @Test
    void testGetWishlistGamesDashboard_WithTbaFilter() {
        int page = 2;
        int size = 7;
        String sort = "releaseDate";
        ReleaseFilter filter = ReleaseFilter.TBA;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "releaseDate")); // default ASC
        Page<Game> dummyPage = createDummyGamesPage();

        when(gamesRepository.findByStatusAndReleaseDateIsNull(eq(GameStatus.WISHLIST), eq(expectedPageable)))
                .thenReturn(dummyPage);

        Page<WishlistGameForTableDto> result = gameService.getWishlistGamesDashboard(page, size, sort, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gamesRepository).findByStatusAndReleaseDateIsNull(eq(GameStatus.WISHLIST), eq(expectedPageable));
    }

    @Test
    void testGetWishlistGamesDashboard_WithDefaultFilter() {
        int page = 3;
        int size = 6;
        String sort = "title,desc";
        ReleaseFilter filter = ReleaseFilter.ALL; // default case in your switch

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
        Page<Game> dummyPage = createDummyGamesPage();

        when(gamesRepository.findByStatus(eq(GameStatus.WISHLIST), eq(expectedPageable)))
                .thenReturn(dummyPage);

        Page<WishlistGameForTableDto> result = gameService.getWishlistGamesDashboard(page, size, sort, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(gamesRepository).findByStatus(eq(GameStatus.WISHLIST), eq(expectedPageable));
    }
}