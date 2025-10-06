-- GameLog Test Database Schema
-- SQL script for Docker container initialization during tests

-- Create sequences (required by JPA @GeneratedValue)
CREATE SEQUENCE seasons_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE series_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create games table
CREATE TABLE games (
    id BIGSERIAL PRIMARY KEY,
    rawg_id BIGINT UNIQUE,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'BACKLOG',
    rating DOUBLE PRECISION,
    notes TEXT,
    platform VARCHAR(100),
    favourite BOOLEAN NOT NULL DEFAULT false,
    release_date DATE,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at DATE,
    CONSTRAINT games_status_check CHECK (status IN ('WISHLIST', 'BACKLOG', 'PLAYING', 'COMPLETED', 'DROPPED', 'ONLINE'))
);

-- Create movies table
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    tmdb_id BIGINT,
    title VARCHAR(255),
    original_title VARCHAR(255),
    status VARCHAR(50),
    release_date DATE,
    poster VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create movies_genres table
CREATE TABLE movies_genres (
    movie_id BIGINT NOT NULL,
    genre_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Create movies_vod_providers table
CREATE TABLE movies_vod_providers (
    movie_id BIGINT NOT NULL,
    provider_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Create series table (with correct column name for TVShow entity)
CREATE TABLE series (
    id BIGSERIAL PRIMARY KEY,
    tmdb_id BIGINT,
    name VARCHAR(255),
    status VARCHAR(50),
    tracking_type VARCHAR(20),
    number_of_seasons INTEGER NOT NULL DEFAULT 0,
    number_of_episodes INTEGER NOT NULL DEFAULT 0,
    first_air_date DATE,
    last_air_date DATE,
    poster_path VARCHAR(500),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT series_tracking_type_check CHECK (tracking_type IN ('WATCHING', 'UP_TO_DATE', 'COMPLETED', 'ON_HOLD', 'DROPPED', 'WISHLIST'))
);

-- Create seasons table
CREATE TABLE seasons (
    id BIGSERIAL PRIMARY KEY,
    series_id BIGINT NOT NULL,
    season_number INTEGER NOT NULL,
    name VARCHAR(255),
    episode_count INTEGER NOT NULL DEFAULT 0,
    watched_count INTEGER NOT NULL DEFAULT 0,
    rating DOUBLE PRECISION,
    air_date DATE,
    FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE
);

-- Create tvseries_vod_providers table
CREATE TABLE tvseries_vod_providers (
    tvseries_id BIGINT NOT NULL,
    provider_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (tvseries_id) REFERENCES series(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_games_status ON games(status);
CREATE INDEX idx_games_rawg_id ON games(rawg_id);
CREATE INDEX idx_games_title ON games(title);
CREATE INDEX idx_games_updated_at ON games(updated_at);
CREATE INDEX idx_movies_tmdb_id ON movies(tmdb_id);
CREATE INDEX idx_series_tmdb_id ON series(tmdb_id);
CREATE INDEX idx_seasons_series_id ON seasons(series_id);

-- No sample data in schema initialization
-- Data will be inserted in test setup methods
