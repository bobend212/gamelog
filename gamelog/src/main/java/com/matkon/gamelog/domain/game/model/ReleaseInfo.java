package com.matkon.gamelog.domain.game.model;

import java.time.LocalDate;

public record ReleaseInfo(
        LocalDate date,
        String status
) {}
