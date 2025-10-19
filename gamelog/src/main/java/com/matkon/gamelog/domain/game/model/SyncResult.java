package com.matkon.gamelog.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class SyncResult {
    private int itemsProcessed;
    private int itemsUpdated;
    private List<FieldDifference> fieldDifferences;
}
