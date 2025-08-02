package com.lion.be.acceptance.auth;

import static com.lion.be.acceptance.user.UserSteps.상태코드를_검증한다;
import static org.assertj.core.api.Assertions.assertThat;

import com.lion.be.acceptance.util.AuthFixture;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AuthSteps {

    public static ExtractableResponse<Response> 비회원이_로그인한다(RequestSpecification spec) {
        return 로그인한다(AuthFixture.비회원_로그인_요청(), spec);
    }

    public static ExtractableResponse<Response> 원준이_로그인한다(RequestSpecification spec) {
        return 로그인한다(AuthFixture.사용자_원준_로그인_요청(), spec);
    }

    public static ExtractableResponse<Response> 로그인한다(Map<String, Object> loginRequest, RequestSpecification spec) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .log().all()
                .body(loginRequest)
                .when().post("/api/test/login")
                .then().log().all()
                .extract();
    }

    public static void 토큰과_상태코드_200을_응답하는지_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("accessToken")).isNotNull()
        );
    }

    public static ExtractableResponse<Response> 로그아웃_한다(String accessToken, RequestSpecification spec) {
        return RestAssured
                .given().log().all()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .when().post("/api/auth/logout")
                .then().log().all()
                .extract();
    }

    public static void 상태코드가_204임을_검증한다(ExtractableResponse<Response> response) {
        상태코드를_검증한다(response, HttpStatus.NO_CONTENT);
    }

}
