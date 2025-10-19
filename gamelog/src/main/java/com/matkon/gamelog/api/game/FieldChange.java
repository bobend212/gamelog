package com.matkon.gamelog.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldChange {
    private String title;
    private String fieldName;
    private String oldValue;
    private String newValue;
}