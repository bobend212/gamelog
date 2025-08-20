package com.matkon.gamelog.data.tvseries;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TVSeriesDto
{
    private Long id;
    private Long tmdbId;
    private String name;
    private LocalDate first_air_date;
    private boolean in_production;
    private int number_of_episodes;
    private int number_of_seasons;
    private String poster_path;
    private LocalDate last_air_date;
    private String status;
    private TrackingType trackingType;
    private List<SeasonDto> seasons = new ArrayList<>();

    public static TVSeriesDto fromEntity(TVSeries tvSeries)
    {
        TVSeriesDto dto = new TVSeriesDto();
        dto.id = tvSeries.getId();
        dto.tmdbId = tvSeries.getTmdbId();
        dto.name = tvSeries.getName();
        dto.first_air_date = tvSeries.getFirst_air_date();
        dto.in_production = tvSeries.isIn_production();
        dto.number_of_episodes = tvSeries.getNumber_of_episodes();
        dto.number_of_seasons = tvSeries.getNumber_of_seasons();
        dto.poster_path = tvSeries.getPoster_path();
        dto.last_air_date = tvSeries.getLast_air_date();
        dto.status = tvSeries.getStatus();
        dto.trackingType = tvSeries.getTrackingType();

        if (tvSeries.getSeasons() != null) {
            for (Season season : tvSeries.getSeasons()) {
                dto.seasons.add(SeasonDto.fromEntity(season));
            }
        }

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

    public Long getTmdbId()
    {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId)
    {
        this.tmdbId = tmdbId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocalDate getFirst_air_date()
    {
        return first_air_date;
    }

    public void setFirst_air_date(LocalDate first_air_date)
    {
        this.first_air_date = first_air_date;
    }

    public boolean isIn_production()
    {
        return in_production;
    }

    public void setIn_production(boolean in_production)
    {
        this.in_production = in_production;
    }

    public int getNumber_of_episodes()
    {
        return number_of_episodes;
    }

    public void setNumber_of_episodes(int number_of_episodes)
    {
        this.number_of_episodes = number_of_episodes;
    }

    public int getNumber_of_seasons()
    {
        return number_of_seasons;
    }

    public void setNumber_of_seasons(int number_of_seasons)
    {
        this.number_of_seasons = number_of_seasons;
    }

    public String getPoster_path()
    {
        return poster_path;
    }

    public void setPoster_path(String poster_path)
    {
        this.poster_path = poster_path;
    }

    public LocalDate getLast_air_date()
    {
        return last_air_date;
    }

    public void setLast_air_date(LocalDate last_air_date)
    {
        this.last_air_date = last_air_date;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public TrackingType getTrackingType()
    {
        return trackingType;
    }

    public void setTrackingType(TrackingType trackingType)
    {
        this.trackingType = trackingType;
    }

    public List<SeasonDto> getSeasons()
    {
        return seasons;
    }

    public void setSeasons(List<SeasonDto> seasons)
    {
        this.seasons = seasons;
    }
}
