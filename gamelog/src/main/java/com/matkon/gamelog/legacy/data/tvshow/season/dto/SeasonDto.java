package com.matkon.gamelog.legacy.data.tvshow.season.dto;

import com.matkon.gamelog.legacy.data.tvshow.season.Season;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeasonDto {
    private Long id;
    private String name;
    private int season_number;
    private LocalDate air_date;
    private int episode_count;
    private int watchedCount;
    private Double rating;

    public static SeasonDto fromEntity(Season season) {
        SeasonDto dto = new SeasonDto();
        dto.id = season.getId();
        dto.name = season.getName();
        dto.season_number = season.getSeason_number();
        dto.air_date = season.getAir_date();
        dto.episode_count = season.getEpisode_count();
        dto.watchedCount = season.getWatchedCount();
        dto.rating = season.getRating();
        return dto;
    }

    public static Season toEntity(SeasonDto dto) {
        Season season = new Season();
        season.setId(dto.getId());
        season.setName(dto.getName());
        season.setSeason_number(dto.getSeason_number());
        season.setAir_date(dto.getAir_date());
        season.setEpisode_count(dto.getEpisode_count());
        season.setWatchedCount(dto.getWatchedCount());
        season.setRating(dto.getRating());
        return season;
    }
}