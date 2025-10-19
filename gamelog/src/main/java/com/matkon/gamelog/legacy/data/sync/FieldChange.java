package com.matkon.gamelog.legacy.data.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldChange {
    private String fieldName;
    private String oldValue;
    private String newValue;
}