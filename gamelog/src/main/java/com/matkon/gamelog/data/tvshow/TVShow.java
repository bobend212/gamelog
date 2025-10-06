package com.matkon.gamelog.data.tvshow;

import com.matkon.gamelog.data.tvshow.season.Season;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TVShow {
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

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private TrackingType trackingType;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Season> seasons = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER) // or LAZY
    @CollectionTable(name = "tvseries_vod_providers", joinColumns = @JoinColumn(name = "tvseries_id"))
    @Column(name = "provider_name")
    private List<String> vodProviders = new ArrayList<>();

    public void addSeason(Season season) {
        seasons.add(season);
        season.setSeries(this);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}