package com.matkon.gamelog.data.sync;

import java.util.List;

public class ChangeDetail {
    private Long mediaId;
    private String mediaName;
    private List<FieldChange> fieldChanges;

    public ChangeDetail(Long mediaId, String mediaName, List<FieldChange> fieldChanges) {
        this.mediaId = mediaId;
        this.mediaName = mediaName;
        this.fieldChanges = fieldChanges;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public List<FieldChange> getFieldChanges() {
        return fieldChanges;
    }

    public void setFieldChanges(List<FieldChange> fieldChanges) {
        this.fieldChanges = fieldChanges;
    }
}
