package com.matkon.gamelog.data;

import java.time.LocalDate;

public class GameUpdateRequest
{
    private String platform;
    private GameStatus status;
    private Double rating;
    private String notes;
    private LocalDate completedAt;
    private boolean favourite;

    public GameUpdateRequest() {}

    public GameUpdateRequest(String platform, GameStatus status, Double rating, String notes, LocalDate completedAt, boolean favourite)
    {
        this.platform = platform;
        this.status = status;
        this.rating = rating;
        this.notes = notes;
        this.completedAt = completedAt;
        this.favourite = favourite;
    }

    // Getters and Setters
    public String getPlatform() {return platform;}

    public void setPlatform(String platform) {this.platform = platform;}

    public GameStatus getStatus() {return status;}

    public void setStatus(GameStatus status) {this.status = status;}

    public Double getRating() {return rating;}

    public void setRating(Double rating) {this.rating = rating;}

    public String getNotes() {return notes;}

    public void setNotes(String notes) {this.notes = notes;}

    public LocalDate getCompletedAt() {return completedAt;}

    public void setCompletedAt(LocalDate completedAt) {this.completedAt = completedAt;}

    public boolean getFavourite() {return favourite;}
}
