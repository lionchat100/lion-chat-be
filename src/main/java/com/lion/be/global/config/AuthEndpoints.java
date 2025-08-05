package com.lion.be.global.config;

public class AuthEndpoints {

    private AuthEndpoints() {
    }

    public static final String[] PERMIT_ALL_PATTERNS = {
            // 정적 리소스
            "/",
            "/index.html",
            "/css/**",
            "/images/**",
            "/uploads/**",
            "/js/**",
            "/h2-console/**",
            "/docs/**",
            "/static/**",
            // 소셜 로그인 관련
            "/oauth2/**",
            "/login",
            // 토큰 관련 API
            "/api/auth/token",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/api/test/login",
            "/api/docs",
            "/api/test.html",
            "/ws/**",
            "/health"
    };

}
