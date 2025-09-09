package com.matkon.gamelog.data.movies;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MovieDto
{
    private Long id;
    private Long tmdbId;
    private String title;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private LocalDate releaseDatePL;
    private int runtime;
    private String status;
    private String poster;
    private List<String> genres;
    private List<String> vodProviders;
    private LocalDateTime createdAt;

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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getOverview()
    {
        return overview;
    }

    public void setOverview(String overview)
    {
        this.overview = overview;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public int getRuntime()
    {
        return runtime;
    }

    public void setRuntime(int runtime)
    {
        this.runtime = runtime;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPoster()
    {
        return poster;
    }

    public void setPoster(String poster)
    {
        this.poster = poster;
    }

    public List<String> getGenres()
    {
        return genres;
    }

    public void setGenres(List<String> genres)
    {
        this.genres = genres;
    }

    public List<String> getVodProviders()
    {
        return vodProviders;
    }

    public void setVodProviders(List<String> vodProviders)
    {
        this.vodProviders = vodProviders;
    }

    public LocalDate getReleaseDatePL()
    {
        return releaseDatePL;
    }

    public void setReleaseDatePL(LocalDate releaseDatePL)
    {
        this.releaseDatePL = releaseDatePL;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getOriginalTitle()
    {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle)
    {
        this.originalTitle = originalTitle;
    }
}