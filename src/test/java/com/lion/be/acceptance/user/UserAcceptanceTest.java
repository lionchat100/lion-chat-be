package com.lion.be.acceptance.user;

import static com.lion.be.acceptance.auth.AuthSteps.*;
import static com.lion.be.acceptance.user.UserSteps.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;

import io.restassured.RestAssured;

@DisplayName("회원 관련 기능 인수테스트")
class UserAcceptanceTest extends AcceptanceTest {

    @Nested
    @DisplayName("온보딩 인수테스트")
    class OnboardingTest {

        @DisplayName("신규 회원이 온보딩을 완료하면, 상태코드 200과 완료 메시지를 반환한다.")
        @Test
        void when_new_user_complete_onboarding_then_response_200() {
            api_문서_타이틀("onboarding_complete_success",spec);
            // given
            var loginResponse = 비회원이_로그인한다(spec);
            String accessToken = loginResponse.jsonPath().getString("accessToken");

            // when
            var onboardingResponse = RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .spec(spec)
                    .auth().oauth2(accessToken)
                    .log().all()
                    .body(UserFixture.회원_멋사2_온보딩_요청()) // "멋사대학교"를 사용
                    .when()
                    .patch("/api/users/onboarding")
                    .then()
                    .log().all()
                    .extract();

            // then
            온보딩_완료_응답을_검증한다(onboardingResponse);
        }

        @DisplayName("가 가입된 사용자가 온보딩 라벨을 조회하면, 상태코드 200과 라벨 목록을 반환한다.")
        @Test
        void when_authenticated_user_get_onboarding_labels_then_response_200(){
			 api_문서_타이틀("fetch_labels_success",spec);
            var loginResponse = 비회원이_로그인한다(spec);
            String accessToken = loginResponse.jsonPath().getString("accessToken");

            // when
            var labelsResponse = 온보딩_라벨을_조회한다(spec, accessToken);

            // then
            상태코드가_200이다(labelsResponse);
            온보딩_라벨_조회_응답을_검증한다(labelsResponse);
        }

        @DisplayName("인증되지 않은 사용자가 온보딩 라벨을 조회하면, 상태코드 401을 반환한다.")
        @Test
        void when_unauthenticated_user_get_onboarding_labels_then_response_401() {
            // when
            var labelsResponse = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .log().all()
                .when()
                .get("/api/users/onboarding/labels")
                .then()
                .log().all()
                .extract();

            // then
            상태코드가_401이다(labelsResponse);
        }
    }
}
