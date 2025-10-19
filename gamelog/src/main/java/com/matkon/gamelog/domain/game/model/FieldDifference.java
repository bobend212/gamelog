package com.matkon.gamelog.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class FieldDifference {

    private String title;
    private String fieldName;
    private String oldValue;
    private String newValue;
}
