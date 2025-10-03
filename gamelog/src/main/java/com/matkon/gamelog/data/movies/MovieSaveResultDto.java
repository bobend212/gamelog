package com.matkon.gamelog.data.movies;

public class MovieSaveResultDto {
    private Long id;
    private boolean alreadyExists;
    private String message;

    public MovieSaveResultDto(Long id, boolean alreadyExists, String message) {
        this.id = id;
        this.alreadyExists = alreadyExists;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAlreadyExists() {
        return alreadyExists;
    }

    public void setAlreadyExists(boolean alreadyExists) {
        this.alreadyExists = alreadyExists;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
