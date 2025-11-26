package com.matkon.gamelog.domain.tvshow.model;

public enum TrackingType {
    WATCHING,    // Actively watching released episodes/seasons
    UP_TO_DATE,  // Watched all currently released content, waiting for new future episodes
    COMPLETED,   // Finished entire show, ended
    ON_HOLD,     // Paused but plan to resume later
    DROPPED,     // Stopped watching, no plan to continue
    WISHLIST     // Planning to watch in the future
}