package com.matkon.gamelog.data.games.sync;

import java.util.List;

public class GameChangeDetail
{
    private Long gameId;
    private String gameName;
    private List<FieldChange> fieldChanges;

    public GameChangeDetail(Long gameId, String gameName, List<FieldChange> fieldChanges)
    {
        this.gameId = gameId;
        this.gameName = gameName;
        this.fieldChanges = fieldChanges;
    }

    public Long getGameId()
    {
        return gameId;
    }

    public void setGameId(Long gameId)
    {
        this.gameId = gameId;
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }

    public List<FieldChange> getFieldChanges()
    {
        return fieldChanges;
    }

    public void setFieldChanges(List<FieldChange> fieldChanges)
    {
        this.fieldChanges = fieldChanges;
    }
}
