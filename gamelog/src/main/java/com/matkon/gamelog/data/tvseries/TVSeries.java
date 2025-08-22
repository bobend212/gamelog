package com.matkon.gamelog.data.tvseries;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
public class TVSeries
{
    @Id
    @GeneratedValue
    private Long id;

    private Long tmdbId;
    private String name;
    private LocalDate first_air_date;
    private int number_of_episodes;
    private int number_of_seasons;
    private String poster_path;
    private LocalDate last_air_date;
    private String status;

    @Enumerated(EnumType.STRING)
    private TrackingType trackingType;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons = new ArrayList<>();

    public void addSeason(Season season)
    {
        seasons.add(season);
        season.setSeries(this);
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

    public List<Season> getSeasons()
    {
        return seasons;
    }

    public void setSeasons(List<Season> seasons)
    {
        this.seasons = seasons;
    }
}