package com.matkon.gamelog.infrastructure.tvshow.database;

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
public class SeasonEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int seasonNumber;
    private LocalDate airDate;
    private int episodeCount;

    private int watchedCount;
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private TVShowEntity series;
}