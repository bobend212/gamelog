package com.matkon.gamelog.data.tvshow.season;

import com.matkon.gamelog.data.tvshow.TVShow;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "seasons")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Season {
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
    private TVShow series;
}