package com.matkon.gamelog.data.tvseries;

import java.time.LocalDate;

public class SeasonDto
{
    private Long id;
    private String name;
    private int season_number;
    private LocalDate air_date;
    private int episode_count;
    private int watchedCount;

    public static SeasonDto fromEntity(Season season)
    {
        SeasonDto dto = new SeasonDto();
        dto.id = season.getId();
        dto.name = season.getName();
        dto.season_number = season.getSeason_number();
        dto.air_date = season.getAir_date();
        dto.episode_count = season.getEpisode_count();
        dto.watchedCount = season.getWatchedCount();
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getSeason_number()
    {
        return season_number;
    }

    public void setSeason_number(int season_number)
    {
        this.season_number = season_number;
    }

    public LocalDate getAir_date()
    {
        return air_date;
    }

    public void setAir_date(LocalDate air_date)
    {
        this.air_date = air_date;
    }

    public int getEpisode_count()
    {
        return episode_count;
    }

    public void setEpisode_count(int episode_count)
    {
        this.episode_count = episode_count;
    }

    public int getWatchedCount()
    {
        return watchedCount;
    }

    public void setWatchedCount(int watchedCount)
    {
        this.watchedCount = watchedCount;
    }
}
