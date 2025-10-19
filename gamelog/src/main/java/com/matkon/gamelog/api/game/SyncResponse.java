package com.matkon.gamelog.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyncResponse {
    private int itemsProcessed;
    private int itemsUpdated;
    private List<FieldChange> fieldChanges;
}