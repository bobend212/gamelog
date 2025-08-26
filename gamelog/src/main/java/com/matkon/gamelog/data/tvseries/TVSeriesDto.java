package com.matkon.gamelog.data.tvseries;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TVSeriesDto
{
    private Long id;
    private Long tmdbId;
    private String name;
    private LocalDate first_air_date;
    private int number_of_episodes;
    private int number_of_seasons;
    private String poster_path;
    private LocalDate last_air_date;
    private String status;
    private TrackingType trackingType;
    private List<SeasonDto> seasons = new ArrayList<>();
    private int totalWatchedEpisodes;
    private int percentageProgress;
    private Double ratingOverall;
    private LocalDateTime updatedAt;

    public static TVSeriesDto fromEntity(TVSeries tvSeries)
    {
        TVSeriesDto dto = new TVSeriesDto();
        dto.id = tvSeries.getId();
        dto.tmdbId = tvSeries.getTmdbId();
        dto.name = tvSeries.getName();
        dto.first_air_date = tvSeries.getFirst_air_date();
        dto.number_of_episodes = tvSeries.getNumber_of_episodes();
        dto.number_of_seasons = tvSeries.getNumber_of_seasons();
        dto.poster_path = tvSeries.getPoster_path();
        dto.last_air_date = tvSeries.getLast_air_date();
        dto.status = tvSeries.getStatus();
        dto.trackingType = tvSeries.getTrackingType();
        dto.updatedAt = tvSeries.getUpdatedAt();

        int totalWatched = 0;
        if (tvSeries.getSeasons() != null) {
            for (Season season : tvSeries.getSeasons()) {
                dto.seasons.add(SeasonDto.fromEntity(season));
                totalWatched += season.getWatchedCount();
            }
        }
        dto.totalWatchedEpisodes = totalWatched;

        if (dto.number_of_episodes > 0) {
            dto.percentageProgress = (int) Math.round((totalWatched * 100.0) / dto.number_of_episodes);
        } else {
            dto.percentageProgress = 0;
        }

        if (tvSeries.getSeasons() != null && !tvSeries.getSeasons().isEmpty()) {
            double avgRating = tvSeries.getSeasons().stream()
                    .filter(s -> s.getRating() != null)
                    .mapToDouble(Season::getRating)
                    .average()
                    .orElse(0.0);
            double roundedAvg = Math.round(avgRating * 10) / 10.0;  // rounds to 1 decimal place
            dto.setRatingOverall(roundedAvg);
        } else {
            dto.setRatingOverall(null);
        }

        return dto;
    }

    public static TVSeries toEntity(TVSeriesDto dto)
    {
        TVSeries entity = new TVSeries();
        entity.setId(dto.getId());
        entity.setTmdbId(dto.getTmdbId());
        entity.setName(dto.getName());
        entity.setFirst_air_date(dto.getFirst_air_date());
        entity.setNumber_of_episodes(dto.getNumber_of_episodes());
        entity.setNumber_of_seasons(dto.getNumber_of_seasons());
        entity.setPoster_path(dto.getPoster_path());
        entity.setLast_air_date(dto.getLast_air_date());
        entity.setStatus(dto.getStatus());
        entity.setTrackingType(dto.getTrackingType());
        entity.setUpdatedAt(dto.getUpdatedAt());

        if (dto.getSeasons() != null) {
            List<Season> seasonEntities = new ArrayList<>();
            for (SeasonDto obj : dto.getSeasons()) {
                if (obj != null) {
                    Season season = SeasonDto.toEntity(obj);
                    season.setSeries(entity);
                    seasonEntities.add(season);
                }
            }
            entity.setSeasons(seasonEntities);
        }

        return entity;
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

    public List<SeasonDto> getSeasons()
    {
        return seasons;
    }

    public void setSeasons(List<SeasonDto> seasons)
    {
        this.seasons = seasons;
    }

    public int getTotalWatchedEpisodes()
    {
        return totalWatchedEpisodes;
    }

    public void setTotalWatchedEpisodes(int totalWatchedEpisodes)
    {
        this.totalWatchedEpisodes = totalWatchedEpisodes;
    }

    public int getPercentageProgress()
    {
        return percentageProgress;
    }

    public void setPercentageProgress(int percentageProgress)
    {
        this.percentageProgress = percentageProgress;
    }

    public Double getRatingOverall()
    {
        return ratingOverall;
    }

    public void setRatingOverall(Double ratingOverall)
    {
        this.ratingOverall = ratingOverall;
    }

    public LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
