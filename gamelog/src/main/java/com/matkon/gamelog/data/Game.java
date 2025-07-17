package com.matkon.gamelog.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
public class Game
{
    // CUSTOM DATA
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.BACKLOG;

    private Double rating;

    private String notes;

    private String playedOn;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDate completedAt;


    // RAWG API DATA
    @Column(unique = true)
    private Long rawgId; // External API ID

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "image_url")
    private String imageUrl;

    // ---

    @PrePersist
    protected void onCreate()
    {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate()
    {
        updatedAt = LocalDateTime.now();
    }

    public Game() {}

    public Game(String title)
    {
        this.title = title;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public GameStatus getStatus() {return status;}

    public void setStatus(GameStatus status) {this.status = status;}

    public LocalDate getCompletedAt() {return completedAt;}

    public void setCompletedAt(LocalDate completedAt) {this.completedAt = completedAt;}

    public Double getRating() {return rating;}

    public void setRating(Double rating) {this.rating = rating;}

    public String getNotes() {return notes;}

    public void setNotes(String notes) {this.notes = notes;}

    public String getPlayedOn() {return playedOn;}

    public void setPlayedOn(String playedOn) {this.playedOn = playedOn;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public Long getRawgId() {return rawgId;}

    public void setRawgId(Long rawgId) {this.rawgId = rawgId;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getReleaseDate() {return releaseDate;}

    public void setReleaseDate(String releaseDate) {this.releaseDate = releaseDate;}

    public String getImageUrl() {return imageUrl;}

    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
}