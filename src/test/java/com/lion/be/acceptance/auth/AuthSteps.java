package com.lion.be.acceptance.auth;

import static com.lion.be.acceptance.user.UserSteps.상태코드를_검증한다;
import static org.assertj.core.api.Assertions.assertThat;

import com.lion.be.acceptance.util.AuthFixture;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AuthSteps {

    public static ExtractableResponse<Response> 원준이_로그인한다(RequestSpecification spec) {
        return 로그인한다(AuthFixture.사용자_원준_로그인_요청(), spec);
    }

	public static ExtractableResponse<Response> 토킷이_로그인한다(RequestSpecification spec) {
		return 로그인한다(AuthFixture.사용자_토킷_로그인_요청(), spec);
	}

	public static ExtractableResponse<Response> 어드민이_로그인한다(RequestSpecification spec) {
		return 로그인한다(AuthFixture.어드민_멋사_로그인_요청(), spec);
	}

    public static ExtractableResponse<Response> 비회원이_로그인한다(RequestSpecification spec) {
        return 로그인한다(AuthFixture.비회원_로그인_요청(), spec);
    }

    public static ExtractableResponse<Response> 로그인한다(Map<String, Object> loginRequest, RequestSpecification spec) {
        return RestAssured
                .given().spec(spec).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(loginRequest)
                .when().post("/api/test/login")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 인증_코드로_토큰을_요청한다(String code, RequestSpecification spec) {
        Map<String, String> request = Map.of("code", code);

        return RestAssured
                .given().spec(spec).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/auth/token")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 토큰_재발급을_요청한다(String refreshToken, RequestSpecification spec) {
        return RestAssured
                .given().spec(spec).log().all()
                .cookie("refresh_token", refreshToken)
                .when().post("/api/auth/refresh")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 로그아웃_한다(String refreshToken, RequestSpecification spec) {
        return RestAssured
                .given().spec(spec).log().all()
                .cookie("refresh_token", refreshToken)
                .when().post("/api/auth/logout")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 내_정보를_조회한다(String accessToken, RequestSpecification spec) {
        return RestAssured
                .given().spec(spec).log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/users/me")
                .then().log().all()
                .extract();
    }

    public static void 토큰과_상태코드_200을_응답하는지_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("accessToken")).isNotNull()
        );
    }

    public static void 토큰_발급과_쿠키_생성을_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK),
                () -> assertThat(response.jsonPath().getString("accessToken")).isNotNull(),
                () -> assertThat(response.cookie("refresh_token")).isNotNull()
        );
    }

    public static void 새로운_액세스_토큰이_발급되었는지_검증한다(ExtractableResponse<Response> response, String oldAccessToken) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK),
                () -> assertThat(response.jsonPath().getString("accessToken")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("accessToken")).isNotEqualTo(oldAccessToken)
        );
    }

    public static void 로그아웃이_성공했는지_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.NO_CONTENT),
                () -> assertThat(response.cookie("refresh_token")).isEqualTo("")
        );
    }

    public static void 내_정보가_정상적으로_조회되는지_검증한다(ExtractableResponse<Response> response, String email, String name, boolean isOnboardingCompleted) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK),
                () -> assertThat(response.jsonPath().getString("email")).isEqualTo(email),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(name),
				() -> assertThat(response.jsonPath().getBoolean("isOnboardingCompleted")).isEqualTo(isOnboardingCompleted)
        );
    }

    public static void 상태코드가_204임을_검증한다(ExtractableResponse<Response> response) {
        상태코드를_검증한다(response, HttpStatus.NO_CONTENT);
    }

    public static void 상태코드가_401임을_검증한다(ExtractableResponse<Response> response) {
        상태코드를_검증한다(response, HttpStatus.UNAUTHORIZED);
    }

}
