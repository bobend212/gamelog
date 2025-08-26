package com.matkon.gamelog.data.tvseries;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "seasons")
public class Season
{
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int season_number;
    private LocalDate air_date;
    private int episode_count;

    private int watchedCount;
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private TVSeries series;

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

    public TVSeries getSeries()
    {
        return series;
    }

    public void setSeries(TVSeries series)
    {
        this.series = series;
    }

    public Double getRating()
    {
        return rating;
    }

    public void setRating(Double rating)
    {
        this.rating = rating;
    }
}