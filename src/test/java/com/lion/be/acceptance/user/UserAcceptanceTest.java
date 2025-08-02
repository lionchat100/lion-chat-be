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
                .body(UserFixture.회원_멋사2_온보딩_요청())
                .when()
                .patch("/api/user/onboarding")
                .then()
                .log().all()  // 응답 로그
                .extract();

            // 일단 상태코드만 확인
            온보딩_완료_응답을_검증한다(onboardingResponse);
        }

    }

    @Nested
    @DisplayName("사용자 카드 조회 인수테스트")
    class UserCardQueryTest {

        @DisplayName("온보딩 완료한 회원이 매칭 카드를 조회하면, 상태코드 200과 카드 목록을 반환한다.")
        @Test
        void when_onboarded_user_get_matching_cards_then_response_200() {
            // given
            api_문서_타이틀("getMatchingCards_success", spec);

            // 첫 번째 사용자 (카드를 조회할 사용자)
            var user1LoginResponse = 비회원이_로그인한다(spec);
            String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), user1AccessToken, spec);

            // 두 번째 사용자 (매칭될 카드)
            var user2LoginResponse = 원준이_로그인한다(spec);
            String user2AccessToken = user2LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사_온보딩_요청(), user2AccessToken, spec);

            // when
            var response = 매칭_카드를_조회한다(user1AccessToken, spec);

            // then
            매칭_카드_조회_응답을_검증한다(response);
        }

        @DisplayName("필터 조건을 적용해서 매칭 카드를 조회하면, 필터링된 결과를 반환한다.")
        @Test
        void when_get_matching_cards_with_filter_then_response_filtered_results() {
            // given
            var user1LoginResponse = 비회원이_로그인한다(spec);
            String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), user1AccessToken, spec);

            var user2LoginResponse = 원준이_로그인한다(spec);
            String user2AccessToken = user2LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사_온보딩_요청(), user2AccessToken, spec);

            // when
            var response = 필터_조건으로_매칭_카드를_조회한다(user1AccessToken, spec);

            // then
            상태코드가_200이다(response);
        }


        @DisplayName("페이지네이션을 적용해서 매칭 카드를 조회하면, 페이징된 결과를 반환한다.")
        @Test
        void when_get_matching_cards_with_pagination_then_response_paged_results() {
            // given
            var user1LoginResponse = 비회원이_로그인한다(spec);
            String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), user1AccessToken, spec);

            // 여러 사용자 생성해서 페이징 효과 확인
            var user2LoginResponse = 원준이_로그인한다(spec);
            String user2AccessToken = user2LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사_온보딩_요청(), user2AccessToken, spec);

            // when
            var response = 페이지네이션으로_매칭_카드를_조회한다(user1AccessToken, spec);

            // then
            상태코드가_200이다(response);
        }
    }

}
