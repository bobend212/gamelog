package com.matkon.gamelog.infrastructure.integration.igdb.dto;

import lombok.Getter;

@Getter
public class TwitchTokenResponse {

    private String access_token;
    private Long expires_in;
    private String token_type;
}
