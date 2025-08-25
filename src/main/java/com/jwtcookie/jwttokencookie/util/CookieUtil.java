package com.jwtcookie.jwttokencookie.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
	
    @Value("${JWT_ACCESS_COOKIE_NAME}")
    private String accessTokenCookieName;
    
    public HttpCookie createAccessTokenCookie(String accessToken, long duration) {
        return ResponseCookie.from(accessTokenCookieName, accessToken)
                .maxAge(duration)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .build();
    }
    
    public HttpCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(accessTokenCookieName, "").maxAge(0).httpOnly(true).path("/").build();
    }
}
