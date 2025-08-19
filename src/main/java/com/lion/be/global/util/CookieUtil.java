package com.lion.be.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private CookieUtil() {
    }

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge, String domain) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
//                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .domain(domain)
//                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String name, String domain) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
//                .secure(true)
                .path("/")
                .maxAge(0)
                .domain(domain)
//                .sameSite("Lax")
                .sameSite("")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findFirst();
        }
        return Optional.empty();
    }

}
