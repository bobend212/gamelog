package com.matkon.gamelog.domain.game.model;

import java.util.List;

public record PlatformReleaseDates(
        String platform,
        List<ReleaseInfo> releases
) {}
