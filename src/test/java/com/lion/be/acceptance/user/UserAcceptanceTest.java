package com.lion.be.acceptance.user;

import static com.lion.be.acceptance.auth.AuthSteps.*;
import static com.lion.be.acceptance.user.UserSteps.*;
import static com.lion.be.acceptance.user.UserSteps.원준이_로그인한다;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;
import com.lion.be.user.domain.entity.University;
import com.lion.be.user.repository.UniversityRepository;
import com.lion.be.user.service.UniversityReadService;

import io.restassured.RestAssured;

@DisplayName("회원 관련 기능 인수테스트")
@ContextConfiguration(classes = UserAcceptanceTest.TestConfig.class)
class UserAcceptanceTest extends AcceptanceTest {

    @Autowired
    private UniversityRepository universityRepository;
    //mockbean 이제 사용안해서 우회하기위한 방법 더 좋은 방식있으면 알려주시면 감사합니다.
    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary  // 실제 Bean을 Override
        public UniversityRepository universityRepository() {
            return mock(UniversityRepository.class);
        }

        @Bean
        @Primary
        public UniversityReadService universityReadService() {
            return mock(UniversityReadService.class);
        }
    }
    @Autowired
    private UniversityReadService universityReadService;

    @BeforeEach
    void setUp() {
        // Mock 초기화
        reset(universityRepository);

        // Mock 데이터 설정
        University 멋사대학교 = new University(1L, "멋사대학교", "https://example.com/likelion.jpg");
        University 연세대학교 = new University(2L, "연세대학교", "https://example.com/yonsei.jpg");

        // Mock 동작 정의
        when(universityRepository.fetchByName("멋사대학교")).thenReturn(Optional.of(멋사대학교));
        when(universityRepository.fetchByName("연세대학교")).thenReturn(Optional.of(연세대학교));
        when(universityRepository.fetchByName("존재하지않는대학")).thenReturn(Optional.empty());
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
                    .patch("/api/users/onboarding")
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

        @DisplayName("제외 목록을 적용해서 매칭 카드를 조회하면, 제외된 사용자는 결과에 포함되지 않는다.")
        @Test
        void when_get_matching_cards_with_exclusion_then_excluded_users_not_in_results() {
            // given
            api_문서_타이틀("getMatchingCards_with_exclusion", spec);

            // 카드를 조회할 사용자
            var user1LoginResponse = 비회원이_로그인한다(spec);
            String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), user1AccessToken, spec);

            // 매칭될 사용자들
            var user2LoginResponse = 원준이_로그인한다(spec);
            String user2AccessToken = user2LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사_온보딩_요청(), user2AccessToken, spec);

            // 첫 번째 조회로 user2의 ID 획득
            var firstResponse = 매칭_카드를_조회한다(user1AccessToken, spec);
            String user2Id = firstResponse.jsonPath().getString("[0].userId");

            // when - user2를 제외하고 조회
            var response = 제외_목록으로_매칭_카드를_조회한다(user1AccessToken, spec, user2Id);

            // then
            상태코드가_200이다(response);
            // 제외된 사용자가 결과에 없는지 검증
            var userIds = response.jsonPath().getList("userId", String.class);
            assertThat(userIds).doesNotContain(user2Id);
        }

        @DisplayName("사이즈 파라미터를 적용해서 매칭 카드를 조회하면, 지정된 개수만큼 결과를 반환한다.")
        @Test
        void when_get_matching_cards_with_size_then_return_limited_results() {
            // given
            var user1LoginResponse = 비회원이_로그인한다(spec);
            String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), user1AccessToken, spec);

            // when
            var response = 사이즈_제한으로_매칭_카드를_조회한다(user1AccessToken, spec, 5);

            // then
            상태코드가_200이다(response);
            assertThat(response.jsonPath().getList("$").size()).isLessThanOrEqualTo(5);
        }

        @DisplayName("필터 조건과 제외 목록을 함께 적용해서 매칭 카드를 조회하면, 두 조건이 모두 적용된 결과를 반환한다.")
        @Test
        void when_get_matching_cards_with_filter_and_exclusion_then_both_conditions_applied() {
            // given
            var user1LoginResponse = 비회원이_로그인한다(spec);
            String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), user1AccessToken, spec);

            // when
            var response = 필터_및_제외_목록으로_매칭_카드를_조회한다(user1AccessToken, spec);

            // then
            상태코드가_200이다(response);
        }

        @DisplayName("온보딩하지 않은 회원이 매칭 카드를 조회하면, 상태코드 400을 반환한다.")
        @Test
        void when_non_onboarded_user_get_matching_cards_then_response_400() {
            // given
            var loginResponse = 비회원이_로그인한다(spec);
            String accessToken = loginResponse.jsonPath().getString("accessToken");
            // 온보딩하지 않음

            // when
            var response = 매칭_카드를_조회한다(accessToken, spec);

            // then
            상태코드가_400이다(response);
        }

    }

}
