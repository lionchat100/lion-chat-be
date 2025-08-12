package com.lion.be.acceptance.user;

import static com.lion.be.acceptance.auth.AuthSteps.로그인한다;
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

    public static void 비회원_회원가입() {
        로그인한다(UserFixture.비회원_회원가입_요청(), new RequestSpecBuilder().build()).jsonPath().getString("accessToken");
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
            .patch("/api/users/onboarding")
            .then()
            .log().all()
            .extract();
    }

    public static void 온보딩_완료_응답을_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
            () -> 상태코드를_검증한다(response, HttpStatus.OK),
            () -> assertThat(response.jsonPath().getString("message"))
                .isEqualTo("온보딩이 완료되었습니다."),
			() -> assertThat(response.jsonPath().getBoolean("isOnboardingCompleted"))  // 필드명과 타입 수정
				.isTrue()
        );
    }

    public static ExtractableResponse<Response> 온보딩_라벨을_조회한다(RequestSpecification spec, String accessToken) {
        return RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .spec(spec)
            .auth().oauth2(accessToken)
            .log().all()
            .when()
            .get("/api/users/onboarding/labels")
            .then()
            .log().all()
            .extract();
    }

    public static void 온보딩_라벨_조회_응답을_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
            () -> 상태코드를_검증한다(response, HttpStatus.OK),
            () -> assertThat(response.jsonPath().getList("genders")).isNotEmpty(),
            () -> assertThat(response.jsonPath().getList("universities")).isNotEmpty(),
            () -> assertThat(response.jsonPath().getList("positions")).isNotEmpty(),
            () -> assertThat(response.jsonPath().getList("mbtis")).isNotEmpty(),
            () -> assertThat(response.jsonPath().getString("genders[0].code")).isNotNull(),
            () -> assertThat(response.jsonPath().getString("genders[0].name")).isNotNull(),
            () -> assertThat(response.jsonPath().getString("universities[0].code")).isNotNull(),
            () -> assertThat(response.jsonPath().getString("universities[0].name")).isNotNull(),
            () -> assertThat(response.jsonPath().getString("positions[0].code")).isNotNull(),
            () -> assertThat(response.jsonPath().getString("positions[0].name")).isNotNull(),
            () -> assertThat(response.jsonPath().getString("mbtis[0].code")).isNotNull(),
            () -> assertThat(response.jsonPath().getString("mbtis[0].name")).isNotNull()
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

    public static void 상태코드가_401이다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.UNAUTHORIZED)
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
