package com.matkon.gamelog.data.tvseries;

public class TVSeriesSaveResultDto {
    private Long id;
    private boolean alreadyExists;
    private String message;

    public TVSeriesSaveResultDto(Long id, boolean alreadyExists, String message) {
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
