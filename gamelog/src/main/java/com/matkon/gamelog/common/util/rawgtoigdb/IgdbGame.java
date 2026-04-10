package com.matkon.gamelog.common.util.rawgtoigdb;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class IgdbGame {

    private Long id;
    private String name;
    private LocalDate releaseDate;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
}
