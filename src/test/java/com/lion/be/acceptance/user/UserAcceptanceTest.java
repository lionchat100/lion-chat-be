package com.lion.be.acceptance.user;

import static com.lion.be.acceptance.auth.AuthSteps.비회원이_로그인한다;
import static com.lion.be.acceptance.user.UserSteps.*;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;

@DisplayName("회원 관련 기능 인수테스트")
class UserAcceptanceTest extends AcceptanceTest {

    @Nested
    @DisplayName("회원 가입 인수테스트")
    class SaveUser {

        @DisplayName("최초 로그인(회원 가입)이 성공하면, 상태코드 200을 반환한다.")
        @Test
        void when_first_login_then_response_200() {
            // docs
            api_문서_타이틀("firstLogin_success", spec);

            // when
            var response = 비회원이_로그인한다(spec);

            // then
            상태코드가_200이다(response);
        }

    }

    @Nested
    @DisplayName("온보딩 인수테스트")
    class OnboardingTest {

        @DisplayName("신규 회원이 온보딩을 완료하면, 상태코드 200과 완료 메시지를 반환한다.")
        @Test
        void when_new_user_complete_onboarding_then_response_200() {
            // given
            var loginResponse = 비회원이_로그인한다(spec);
            String accessToken = loginResponse.jsonPath().getString("accessToken");

            // when
            var onboardingResponse = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()  // 요청 로그
                .body(UserFixture.비회원_온보딩_요청())
                .when()
                .patch("/api/user/onboarding")
                .then()
                .log().all()  // 응답 로그
                .extract();

            // 일단 상태코드만 확인
            온보딩_완료_응답을_검증한다(onboardingResponse);
        }

    }

}
