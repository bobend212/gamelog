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
public class ChangeDetail {
    private Long mediaId;
    private String mediaName;
    private List<FieldChange> fieldChanges;
}
