package com.lion.be.acceptance.user;

import static com.lion.be.acceptance.auth.AuthSteps.*;
import static com.lion.be.acceptance.image.ImageSteps.*;
import static com.lion.be.acceptance.user.UserSteps.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("회원 관련 기능 인수테스트")
class UserAcceptanceTest extends AcceptanceTest {

    @Nested
    @DisplayName("온보딩 인수테스트")
    class OnboardingTest {

		@DisplayName("신규 회원이 온보딩을 완료하면, 상태코드 200과 완료 메시지를 반환한다.")
		@Test
		void when_new_user_complete_onboarding_then_response_200() throws IOException {
			api_문서_타이틀("onboarding_complete_success",spec);

			// given - 1. 로그인
			var loginResponse = 토킷이_로그인한다(spec);
			String accessToken = loginResponse.jsonPath().getString("accessToken");

			// given - 2. 이미지 업로드
			List<Long> imageIds = new ArrayList<>();
			ExtractableResponse<Response> imageResponse1 = 이미지를_업로드한다(accessToken, spec);
			imageIds.add(imageResponse1.jsonPath().getLong("imageId"));

			ExtractableResponse<Response> imageResponse2 = 이미지를_업로드한다(accessToken, spec);
			imageIds.add(imageResponse2.jsonPath().getLong("imageId"));

			// when - 3. 온보딩 완료
			var onboardingResponse = RestAssured
				.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.spec(spec)
				.auth().oauth2(accessToken)
				.log().all()
				.body(UserFixture.토킷_온보딩_요청(imageIds))
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

	@Nested
	@DisplayName("닉네임 중복 체크 인수테스트")
	class NicknameDuplicationCheckTest {

		@DisplayName("사용 가능한 닉네임을 체크하면, 상태코드 200과 true를 반환한다.")
		@Test
		void when_check_available_nickname_then_response_200_with_true() {
			api_문서_타이틀("check_nickname_available", spec);

			// given
			var loginResponse = 비회원이_로그인한다(spec);
			String accessToken = loginResponse.jsonPath().getString("accessToken");
			String availableNickname = "사용가능한닉네임123";

			// when
			var response = 닉네임_중복을_체크한다(spec, accessToken, availableNickname);

			// then
			상태코드가_200이다(response);
			닉네임_사용가능_응답을_검증한다(response);
		}

		@DisplayName("이미 사용 중인 닉네임을 체크하면, 상태코드 200과 false를 반환한다.")
		@Test
		void when_check_duplicate_nickname_then_response_200_with_false() throws IOException {
			api_문서_타이틀("check_nickname_duplicate", spec);

			토킷_완전_온보딩();
			// given - 사용자 2 로그인
			var user1LoginResponse = 비회원이_로그인한다(spec);
			String user1Token = user1LoginResponse.jsonPath().getString("accessToken");

			// when - 사용자 1의 실제 닉네임으로 중복 체크
			String actualNickname = "토킷개발자";

			var response = 닉네임_중복을_체크한다(spec, user1Token, actualNickname);

			// then
			상태코드가_200이다(response);
			닉네임_중복_응답을_검증한다(response);
		}

		@DisplayName("인증되지 않은 사용자가 닉네임을 체크하면, 상태코드 401을 반환한다.")
		@Test
		void when_unauthenticated_user_check_nickname_then_response_401() {
			// when
			var response = RestAssured
				.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.spec(spec)
				.log().all()
				.when()
				.get("/api/users/check-nickname/{nickname}", "테스트닉네임")
				.then()
				.log().all()
				.extract();

			// then
			상태코드가_401이다(response);
		}
	}

	@Nested
	@DisplayName("프로필 수정 인수테스트")
	class ProfileUpdateTest {

		@DisplayName("사용자가 프로필을 수정하면, 상태코드 200과 완료 메시지를 반환한다.")
		@Test
		void when_user_update_profile_then_response_200() throws IOException {
			api_문서_타이틀("profile_update_success", spec);

			// given
			String accessToken = 토킷_완전_온보딩();

			// given
			ExtractableResponse<Response> imageResponse = 이미지를_업로드한다(accessToken, spec);
			이미지_업로드_성공을_검증한다(imageResponse);
			Long newImageId = imageResponse.jsonPath().getLong("imageId");

			// given
			Map<String, Object> updateRequest = Map.of(
				"bio", "수정된 자기소개입니다",
				"imageId", newImageId,
				"preferenceType", "PREFERENCE_FOCUSED"
			);

			// when
			var updateResponse = 본인_프로필을_수정한다(updateRequest, accessToken, spec);

			// then
			상태코드가_200이다(updateResponse);
			프로필_수정_완료_응답을_검증한다(updateResponse);
		}

		@DisplayName("이미지 없이 bio와 preferenceType만 수정하면, 상태코드 200을 반환한다.")
		@Test
		void when_user_update_profile_without_image_then_response_200() throws IOException {
			api_문서_타이틀("profile_update_without_image", spec);

			// given - 온보딩 완료된 사용자
			String accessToken = 토킷_완전_온보딩();

			// given - 이미지 제외한 수정 요청
			Map<String, Object> updateRequest = Map.of(
				"bio", "이미지 없이 수정된 bio",
				"preferenceType", "PREFERENCE_FOCUSED"
			);

			// when
			var updateResponse = 본인_프로필을_수정한다(updateRequest, accessToken, spec);

			// then - 수정 성공 검증
			상태코드가_200이다(updateResponse);
			프로필_수정_완료_응답을_검증한다(updateResponse);
		}
	}
}

