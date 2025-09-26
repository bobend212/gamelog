package com.matkon.gamelog.data.movies;

import java.time.LocalDate;
import java.util.List;

public class MovieListDto
{
    private Long id;
    private String title;
    private String originalTitle;
    private LocalDate releaseDate;
    private String status;
    private String poster;
    private List<String> genres;
    private List<String> vodProviders;

    public static MovieListDto fromEntity(Movie movie)
    {
        MovieListDto dto = new MovieListDto();
        dto.id = movie.getId();
        dto.title = movie.getTitle();
        dto.originalTitle = movie.getOriginalTitle();
        dto.releaseDate = movie.getReleaseDate();
        dto.status = movie.getStatus();
        dto.poster = movie.getPoster();
        dto.genres = movie.getGenres();
        dto.vodProviders = movie.getVodProviders();

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

    public String getOriginalTitle()
    {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle)
    {
        this.originalTitle = originalTitle;
    }

    public LocalDate getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate)
    {
        this.releaseDate = releaseDate;
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
}
