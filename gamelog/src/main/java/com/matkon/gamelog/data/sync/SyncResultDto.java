package com.matkon.gamelog.data.sync;

import java.util.List;

public class SyncResultDto
{
    private int totalChecked;
    private int updatedCount;
    private List<ChangeDetail> changes;

    public SyncResultDto(int totalChecked, int updatedCount, List<ChangeDetail> changes)
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

    public List<ChangeDetail> getChanges()
    {
        return changes;
    }

    public void setChanges(List<ChangeDetail> changes)
    {
        this.changes = changes;
    }
}
