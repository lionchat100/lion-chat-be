package com.lion.be.acceptance.auth;

import static com.lion.be.acceptance.auth.AuthSteps.*;
import static com.lion.be.acceptance.util.UserFixture.회원_원준;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.auth.controller.dto.AuthToken;
import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.auth.service.AuthCodeService;
import com.lion.be.auth.service.RefreshTokenService;
import com.lion.be.global.util.JwtTokenProvider;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.service.UserReadService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@DisplayName("인증 관련 기능 인수테스트")
class AuthAcceptanceTest extends AcceptanceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthCodeService authCodeService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserReadService userReadService;

    private User 원준;
    private Authentication 원준_인증객체;

    @BeforeEach
    void setAuthAcceptanceTest() {
        원준 = userReadService.fetchByEmail(회원_원준.getEmail());
        원준_인증객체 = getAuthentication(원준.getId(), 원준.getEmail(), Role.USER.getKey());
    }

    // 인증 객체를 만들기 위한 헬퍼 메서드
    private Authentication getAuthentication(Long userId, String email, String role) {
        UserPrincipal userPrincipal = new UserPrincipal(
                userId,
                email,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                "",
                userPrincipal.getAuthorities()
        );
    }

    @Nested
    @DisplayName("실제 인증 흐름 테스트 (TokenController)")
    class RealAuthFlow {

        @DisplayName("유효한 인증 코드로 토큰 교환 요청 시, 토큰과 200 상태코드를 반환한다.")
        @Test
        void when_exchangeCode_then_returnTokenAnd200() {
            api_문서_타이틀("token_exchange_success", spec);

            // given: 테스트용 토큰 생성 및 AuthCode 발급
            String accessToken = jwtTokenProvider.generateAccessToken(원준_인증객체);
            String refreshToken = jwtTokenProvider.generateRefreshToken(원준_인증객체);
            AuthToken authToken = new AuthToken(accessToken, refreshToken);
            String code = authCodeService.generateTokens(authToken);

            // when
            var response = 인증_코드로_토큰을_요청한다(code, spec);

            // then
            토큰_발급과_쿠키_생성을_검증한다(response);
        }

        @DisplayName("유효한 리프레시 토큰으로 재발급 요청 시, 새로운 액세스 토큰과 200 상태코드를 반환한다.")
        @Test
        void when_refreshToken_then_returnNewTokenAnd200() {
            api_문서_타이틀("token_refresh_success", spec);

            // given
            String oldAccessToken = jwtTokenProvider.generateAccessToken(원준_인증객체);
            String refreshToken = jwtTokenProvider.generateRefreshToken(원준_인증객체);
            refreshTokenService.saveToken(원준.getEmail(), refreshToken);

            // when
            var response = 토큰_재발급을_요청한다(refreshToken, spec);

            // then
            새로운_액세스_토큰이_발급되었는지_검증한다(response, oldAccessToken);
        }

        @DisplayName("로그아웃 요청이 성공하면, 204코드를 반환하고 쿠키를 삭제한다.")
        @Test
        void when_logout_then_return_204AndClearCookie() {
            api_문서_타이틀("logout_success", spec);

            // given
            String refreshToken = jwtTokenProvider.generateRefreshToken(원준_인증객체);
            refreshTokenService.saveToken(원준.getEmail(), refreshToken);

            // when
            var response = 로그아웃_한다(refreshToken, spec);

            // then
            로그아웃이_성공했는지_검증한다(response);
        }
    }

    @Nested
    @DisplayName("인가 흐름 테스트 (AuthController)")
    class AuthorizationFlow {

        @DisplayName("유효한 액세스 토큰으로 내 정보를 요청하면, 유저 정보와 200코드를 반환한다.")
        @Test
        void when_fetchMyInfo_then_returnUserInfoAnd200() {
            api_문서_타이틀("fetch_my_info_success", spec);

            // given
            String accessToken = jwtTokenProvider.generateAccessToken(원준_인증객체);

            // when
            var response = 내_정보를_조회한다(accessToken, spec);

            // then
            내_정보가_정상적으로_조회되는지_검증한다(response, 원준.getEmail(), 원준.getName());
        }

        @DisplayName("토큰 없이 내 정보를 요청하면, 401 코드를 반환한다.")
        @Test
        void given_noToken_when_fetchMyInfo_then_return401() {
            api_문서_타이틀("fetch_my_info_fail_no_token", spec);

            // when
            var response = RestAssured
                    .given().spec(spec).log().all()
                    .when().get("/api/users/me")
                    .then().log().all()
                    .extract();

            // then
            상태코드가_401임을_검증한다(response);
        }
    }


    @Nested
    @DisplayName("테스트용 로그인 인수테스트 (기존)")
    class LoginWithTestEndpoint {

        @DisplayName("회원이 테스트 로그인 요청시 성공하면 토큰을 반환한다.")
        @Test
        void when_login_then_return_200AndToken() {
            api_문서_타이틀("test_login_success", spec);

            // when
            var response = 원준이_로그인한다(spec);

            // then
            토큰과_상태코드_200을_응답하는지_검증한다(response);
        }

    }
}