package com.matkon.gamelog.data.tvshow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TVShowSaveResultDto {
    private Long id;
    private boolean alreadyExists;
    private String message;
}