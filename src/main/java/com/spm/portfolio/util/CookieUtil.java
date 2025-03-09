package com.spm.portfolio.util;
import org.springframework.http.ResponseCookie;


import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
public class CookieUtil {

    private static final String COOKIE_NAME = "jwt_token";
    private static final long COOKIE_EXPIRATION = 3600; // 1 hour

    // Set JWT in HTTP-Only Cookie
    /*public void setJwtCookie(ServerHttpResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)  // Prevent JavaScript access
                .secure(true)     // Set true in HTTPS environments
                .path("/")        // Available across the whole site
                .maxAge(COOKIE_EXPIRATION)
                .sameSite("Strict") // CSRF protection
                .build();

        response.addCookie(cookie);
    }

    // Retrieve JWT from Cookie
    public Optional<String> getJwtFromCookie(ServerHttpRequest request) {
        return request.getCookies().getFirst(COOKIE_NAME) != null ?
                Optional.of(request.getCookies().getFirst(COOKIE_NAME).getValue()) : Optional.empty();
    }

    // Clear JWT Cookie (Logout)
    public void clearJwtCookie(ServerHttpResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();

        response.addCookie(cookie);
    }*/
}
