package com.lion.be.global.config;

public class AuthEndpoints {

    private AuthEndpoints() {
    }

    // Spring Security Filter Chain을 아예 거치지 않을 경로 (정적 리소스)
    public static final String[] STATIC_RESOURCES_PATTERNS = {
            "/css/**",
            "/images/**",
            "/uploads/**",
            "/js/**",
            "/h2-console/**",
            "/static/**"
    };

    // Filter Chain은 거치지만, 인증 없이 접근을 허용할 경로
    public static final String[] PERMIT_ALL_PATTERNS = {
            // 정적 리소스 (루트 페이지 등)
            "/",
            "/index.html",
            "/api/test.html",
            "/docs/**",
            // 소셜 로그인 관련
            "/oauth2/**", // <-- 여기가 핵심! ignoring()에서 이쪽으로 옮겨야 합니다.
            "/login",
            // 토큰 관련 API
            "/api/auth/token",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/api/test/login",
            "/api/docs",
            "/api/health",
            // WebSocket
            "/ws/**",
    };

}
