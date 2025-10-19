package com.matkon.gamelog.api.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
class GameWishlistResponse {
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private boolean tba;
    private Long daysToRelease;
    private boolean released;
    private String imageUrl;
}
