package com.matkon.gamelog.data;

public class GameSaveResult
{
    private Game game;
    private boolean alreadyExists;
    private String message;

    public GameSaveResult(Game game, boolean alreadyExists, String message)
    {
        this.game = game;
        this.alreadyExists = alreadyExists;
        this.message = message;
    }

    public Game getGame() {return game;}

    public void setGame(Game game) {this.game = game;}

    public boolean isAlreadyExists() {return alreadyExists;}

    public void setAlreadyExists(boolean alreadyExists) {this.alreadyExists = alreadyExists;}

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}
}

