package com.matkon.gamelog.data.tvseries;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Season
{
    @Id
    @GeneratedValue
    private Long id;
    private Long tmdbId;
    private int seasonNumber;
    private String name;

    private int totalEpisodes;
    private int watchedCount;

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

    public Long getTmdbId()
    {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId)
    {
        this.tmdbId = tmdbId;
    }

    public int getSeasonNumber()
    {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber)
    {
        this.seasonNumber = seasonNumber;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getTotalEpisodes()
    {
        return totalEpisodes;
    }

    public void setTotalEpisodes(int totalEpisodes)
    {
        this.totalEpisodes = totalEpisodes;
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
}

/*
*     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate air_date;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Episode> episodes;

    private String name;

    private String overview;

    private String poster_path;

    private int season_number;*/