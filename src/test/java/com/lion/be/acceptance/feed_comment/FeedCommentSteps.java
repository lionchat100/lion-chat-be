package com.lion.be.acceptance.feed_comment;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
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

    public static Map<String, Object> feedCommentSaveRequest_생성(String content) {
        return Map.of("content", content);
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

    public static ExtractableResponse<Response> 피드의_모든_댓글을_조회한다(
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
                .when()
                .get("/api/feeds/{feedId}/comments", feedId)
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

    public static void 피드_댓글_전체_조회_응답을_검증한다(ExtractableResponse<Response> response) {
        int expectedCommentCount = 4;

        List<Map<String, Object>> content = response.jsonPath().getList("content");

        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK),

                () -> assertThat(content)
                        .isNotNull()
                        .hasSize(expectedCommentCount),

                () -> {
                    Map<String, Object> firstComment = content.get(0);

                    assertThat(firstComment).containsKeys("id", "feedId", "feedCommentUserResponse", "content",
                            "createdAt", "updatedAt");
                    assertThat(firstComment.get("content")).isEqualTo("댓글1"); // 생성 순서에 따라 검증

                    assertThat(firstComment.get("feedCommentUserResponse")).isInstanceOf(Map.class);

                    Map<String, Object> userResponse = (Map<String, Object>) firstComment.get(
                            "feedCommentUserResponse");
                    assertThat(userResponse).containsKeys("userId", "name", "imageUrl");
                },

                () -> {
                    assertThat(response.jsonPath().getMap("pageable")).isNotNull();
                    assertThat(response.jsonPath().getBoolean("last")).isNotNull();
                    assertThat(response.jsonPath().getInt("numberOfElements")).isEqualTo(expectedCommentCount);
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
