package com.matkon.gamelog.data.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyncResultDto {
    private int totalChecked;
    private int updatedCount;
    private List<ChangeDetail> changes;
}