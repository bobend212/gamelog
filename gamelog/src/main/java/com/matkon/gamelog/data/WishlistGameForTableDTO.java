package com.matkon.gamelog.data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class WishlistGameForTableDTO
{
    private Long id;
    private String title;
    private LocalDate releaseDate;
    private boolean tba;
    private Long daysToRelease;
    private boolean isReleased;

    public static WishlistGameForTableDTO fromEntity(Game game)
    {
        WishlistGameForTableDTO dto = new WishlistGameForTableDTO();
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

        return dto;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public LocalDate getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public boolean isTba()
    {
        return tba;
    }

    public void setTba(boolean tba)
    {
        this.tba = tba;
    }

    public Long getDaysToRelease()
    {
        return daysToRelease;
    }

    public void setDaysToRelease(Long daysToRelease)
    {
        this.daysToRelease = daysToRelease;
    }

    public boolean isReleased()
    {
        return isReleased;
    }

    public void setReleased(boolean released)
    {
        isReleased = released;
    }
}
