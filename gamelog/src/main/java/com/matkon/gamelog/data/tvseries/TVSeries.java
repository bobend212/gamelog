package com.matkon.gamelog.data.tvseries;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TVSeries
{
    @Id
    @GeneratedValue
    private Long id;
    private Long tmdbId;
    private String name;

    @Enumerated(EnumType.STRING)
    private MyStatus myStatus;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons = new ArrayList<>();

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

    public MyStatus getMyStatus()
    {
        return myStatus;
    }

    public void setMyStatus(MyStatus myStatus)
    {
        this.myStatus = myStatus;
    }

    public List<Season> getSeasons()
    {
        return seasons;
    }

    public void setSeasons(List<Season> seasons)
    {
        this.seasons = seasons;
    }

    public void addSeason(Season season)
    {
        seasons.add(season);
        season.setSeries(this);
    }

}

/*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tmdbId;

    private String name;

    private String original_name;

    private String overview;

    private String poster_path;

    private boolean in_production;

    private LocalDate first_air_date;

    private LocalDate last_air_date;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Episode last_episode_to_air;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Episode next_episode_to_air;

    private List<String> networks;

    private int number_of_episodes;

    private int number_of_seasons;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Season> seasons;

    private String status;

    @Column(name = "my_status")
    @Enumerated(EnumType.STRING)
    private TVSeriesStatus myStatus; // WATCHING, WISHLIST, DROPPED, FINISHED
*/