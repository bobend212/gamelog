package com.matkon.gamelog.data.game.sync;

import java.util.List;

public class GameSyncResultDto
{
    private int totalChecked;
    private int updatedCount;
    private List<GameChangeDetail> changes;

    public GameSyncResultDto(int totalChecked, int updatedCount, List<GameChangeDetail> changes)
    {
        this.totalChecked = totalChecked;
        this.updatedCount = updatedCount;
        this.changes = changes;
    }

    public int getTotalChecked()
    {
        return totalChecked;
    }

    public void setTotalChecked(int totalChecked)
    {
        this.totalChecked = totalChecked;
    }

    public int getUpdatedCount()
    {
        return updatedCount;
    }

    public void setUpdatedCount(int updatedCount)
    {
        this.updatedCount = updatedCount;
    }

    public List<GameChangeDetail> getChanges()
    {
        return changes;
    }

    public void setChanges(List<GameChangeDetail> changes)
    {
        this.changes = changes;
    }
}
