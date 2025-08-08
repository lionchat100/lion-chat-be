package com.lion.be.acceptance.feed_comment;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class FeedCommentSteps {

    public static ExtractableResponse<Response> 회원_id를_가져온다(RequestSpecification spec, String accessToken) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when().post("/api/feeds/")
                .then().log().all()
                .extract();
    }

    public static Map<String, Object> feedCommentSaveRequest_생성() {
        return Map.of("content", "이것은 댓글 내용입니다.");
    }

    public static ExtractableResponse<Response> 피드의_댓글을_작성한다(
            Map<String, Object> feedCommentSaveRequest,
            Long feedId,
            String accessToken,
            RequestSpecification spec
    ) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .body(feedCommentSaveRequest)
                .when()
                .post("/api/feeds/{feedId}/comments", feedId)
                .then()
                .log().all()
                .extract();
    }

    public static void 피드_댓글_작성_응답을_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK),
                () -> assertThat(response.jsonPath().getString("commentId"))
                        .isNotEmpty()
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
