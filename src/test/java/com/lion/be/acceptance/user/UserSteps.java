package com.lion.be.acceptance.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.lion.be.acceptance.util.UserFixture;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class UserSteps {

    public static void 원준_회원가입() {
        로그인한다(UserFixture.사용자_원준_회원가입_요청(), new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
    }

    public static ExtractableResponse<Response> 회원_id를_가져온다(RequestSpecification spec, String accessToken) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when().get("/api/users/id")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 온보딩을_완료한다(
        Map<String, Object> onboardingRequest,
        String accessToken,
        RequestSpecification spec
    ) {
        return RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .spec(spec)
            .auth().oauth2(accessToken)
            .log().all()
            .body(onboardingRequest)
            .when()
            .patch("/api/user/onboarding")
            .then()
            .log().all()
            .extract();
    }

    public static void 온보딩_완료_응답을_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
            () -> 상태코드를_검증한다(response, HttpStatus.OK),
            () -> assertThat(response.jsonPath().getString("message"))
                .isEqualTo("온보딩이 완료되었습니다."),
            () -> assertThat(response.jsonPath().getString("status"))
                .isEqualTo("COMPLETED")
        );
    }

    public static ExtractableResponse<Response> 로그인한다(Map<String, Object> userSaveRequest,
                                                      RequestSpecification spec) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .log().all()
                .body(userSaveRequest)
                .when()
                .post("/api/test/login")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원탈퇴한다(String accessToken,
                                                       RequestSpecification spec) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .delete("/api/members/delete")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 매칭_카드를_조회한다(
        String accessToken,
        RequestSpecification spec
    ) {
        return RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .spec(spec)
            .auth().oauth2(accessToken)
            .log().all()
            .when()
            .get("/api/user/card")
            .then()
            .log().all()
            .extract();
    }

    // 필터 조건으로 매칭 카드 조회
    public static ExtractableResponse<Response> 필터_조건으로_매칭_카드를_조회한다(
        String accessToken,
        RequestSpecification spec
    ) {
        return RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .spec(spec)
            .auth().oauth2(accessToken)
            .queryParam("preferredGender", "MEN")
            .queryParam("preferredMbti", "ENFP")
            .queryParam("preferredUniversity", "멋사대학교")
            .log().all()
            .when()
            .get("/api/user/card")
            .then()
            .log().all()
            .extract();
    }

    // 페이지네이션으로 매칭 카드 조회
    public static ExtractableResponse<Response> 페이지네이션으로_매칭_카드를_조회한다(
        String accessToken,
        RequestSpecification spec
    ) {
        return RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .spec(spec)
            .auth().oauth2(accessToken)
            .queryParam("page", 0)
            .queryParam("size", 5)
            .log().all()
            .when()
            .get("/api/user/card")
            .then()
            .log().all()
            .extract();
    }

    // 원준이 로그인 (UserFixture에 이미 있는 데이터 활용)
    public static ExtractableResponse<Response> 원준이_로그인한다(RequestSpecification spec) {
        return RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .spec(spec)
            .log().all()
            .body(UserFixture.사용자_원준_회원가입_요청())
            .when()
            .post("/api/test/login")
            .then()
            .log().all()
            .extract();
    }

    // 매칭 카드 조회 응답 검증
    public static void 매칭_카드_조회_응답을_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
            () -> 상태코드를_검증한다(response, HttpStatus.OK),
            () -> assertThat(response.jsonPath().getList("$")).isNotNull(),
            () -> {
                // 카드가 있다면 첫 번째 카드의 필수 필드들 검증
                if (!response.jsonPath().getList("$").isEmpty()) {
                    assertThat(response.jsonPath().getString("[0].userId")).isNotNull();
                    assertThat(response.jsonPath().getString("[0].name")).isNotNull();
                    assertThat(response.jsonPath().getString("[0].university")).isNotNull();
                    assertThat(response.jsonPath().getString("[0].position")).isNotNull();
                    assertThat(response.jsonPath().getString("[0].mbti")).isNotNull();
                    assertThat(response.jsonPath().getString("[0].gender")).isNotNull();
                    assertThat(response.jsonPath().getList("[0].imageUrls")).isNotNull();
                }
            }
        );
    }


    public static void 상태코드가_200이다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK)
        );
    }

    public static void 상태코드가_204이다(
            ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.NO_CONTENT)
        );
    }

    public static void 상태코드가_400이다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
            () -> 상태코드를_검증한다(response, HttpStatus.BAD_REQUEST)
        );
    }

    public static void 상태코드가_404이다(
            ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.NOT_FOUND)
        );
    }


    public static AbstractIntegerAssert<?> 상태코드를_검증한다(ExtractableResponse<Response> response,
                                                      HttpStatus expectedHttpStatus) {
        return assertThat(response.statusCode()).isEqualTo(expectedHttpStatus.value());
    }

    public static AbstractStringAssert<?> 오류코드를_검증한다(ExtractableResponse<Response> response, String code) {
        return assertThat(response.jsonPath().getString("code")).isEqualTo(code);
    }

}
