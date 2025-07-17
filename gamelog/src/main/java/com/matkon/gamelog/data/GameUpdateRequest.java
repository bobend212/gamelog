package com.matkon.gamelog.data;

import java.time.LocalDate;

public class GameUpdateRequest
{
    private String playedOn;
    private GameStatus status;
    private Double rating;
    private String notes;
    private LocalDate completedAt;

    public GameUpdateRequest() {}

    public GameUpdateRequest(String playedOn, GameStatus status, Double rating, String notes, LocalDate completedAt)
    {
        this.playedOn = playedOn;
        this.status = status;
        this.rating = rating;
        this.notes = notes;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public String getPlayedOn() {return playedOn;}

    public void setPlayedOn(String playedOn) {this.playedOn = playedOn;}

    public GameStatus getStatus() {return status;}

    public void setStatus(GameStatus status) {this.status = status;}

    public Double getRating() {return rating;}

    public void setRating(Double rating) {this.rating = rating;}

    public String getNotes() {return notes;}

    public void setNotes(String notes) {this.notes = notes;}

    public LocalDate getCompletedAt() {return completedAt;}

    public void setCompletedAt(LocalDate completedAt) {this.completedAt = completedAt;}
}
