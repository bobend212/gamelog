package com.matkon.gamelog.data.game.dto;

import com.matkon.gamelog.data.game.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameForWishlistDto {
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private boolean tba;
    private Long daysToRelease;
    private boolean isReleased;
    private String imageUrl;

    public static GameForWishlistDto fromEntity(Game game) {
        GameForWishlistDto dto = new GameForWishlistDto();
        dto.id = game.getId();
        dto.title = game.getTitle();
        dto.releaseDate = game.getReleaseDate();

        dto.tba = (game.getReleaseDate() == null);

        if (dto.releaseDate != null) {
            LocalDate today = LocalDate.now();
            if (dto.releaseDate.isAfter(today)) {
                dto.daysToRelease = ChronoUnit.DAYS.between(today, dto.releaseDate);
            } else {
                dto.daysToRelease = null;
            }
        } else {
            dto.daysToRelease = null;
        }

        dto.isReleased = dto.releaseDate != null && !dto.releaseDate.isAfter(LocalDate.now());
        dto.imageUrl = game.getImageUrl();

        return dto;
    }
}
