package com.authservice.controller.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieUtil {

    private static final String accessTokenCookieName = "access_token";
    private static final String refreshTokenCookieName = "refresh_token";

    private static final Long accessTokenLifeTime = Duration.ofHours(1).getSeconds();
    private static final Long refreshTokenLifeTime = Duration.ofDays(7).getSeconds();


    public HttpCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(accessTokenCookieName, token)
                .maxAge(accessTokenLifeTime)
                .httpOnly(true)
                .path("/")
                .build();
    }

    public HttpCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(refreshTokenCookieName, token)
                .maxAge(refreshTokenLifeTime)
                .httpOnly(true)
                .path("/")
                .build();
    }

    public HttpCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(accessTokenCookieName, "").maxAge(0).httpOnly(true).path("/").build();
    }

}