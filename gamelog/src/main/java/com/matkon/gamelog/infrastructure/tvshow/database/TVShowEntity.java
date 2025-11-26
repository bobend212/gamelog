package com.matkon.gamelog.infrastructure.tvshow.database;

import com.matkon.gamelog.domain.tvshow.model.TrackingType;
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
public class TVShowEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long tmdbId;
    private String name;
    private LocalDate firstAirDate;
    private int numberOfEpisodes;
    private int numberOfSeasons;
    private String posterPath;
    private LocalDate lastAirDate;
    private String status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private TrackingType trackingType;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeasonEntity> seasons = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tvseries_vod_providers", joinColumns = @JoinColumn(name = "tvseries_id"))
    @Column(name = "provider_name")
    private List<String> vodProviders = new ArrayList<>();

    public void addSeason(SeasonEntity seasonEntity) {
        seasons.add(seasonEntity);
        seasonEntity.setSeries(this);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}