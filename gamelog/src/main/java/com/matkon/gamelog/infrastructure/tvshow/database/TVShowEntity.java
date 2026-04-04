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
import jakarta.persistence.OrderBy;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tv_shows")
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

    @OneToMany(mappedBy = "tvShow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seasonNumber ASC")
    private Set<SeasonEntity> seasons = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tv_show_vod_providers", joinColumns = @JoinColumn(name = "tv_show_id"))
    @Column(name = "provider")
    private Set<String> vodProviders = new HashSet<>();

    public void addSeason(SeasonEntity seasonEntity) {
        seasons.add(seasonEntity);
        seasonEntity.setTvShow(this);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}