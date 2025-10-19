package com.matkon.gamelog.legacy.data.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieSaveResultDto {
    private Long id;
    private boolean alreadyExists;
    private String message;
}
