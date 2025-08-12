package com.lion.be.acceptance.feed_comment;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class FeedCommentSteps {

    public static Map<String, Object> feedCommentSaveRequest_생성(String content) {
        return Map.of("content", content);
    }

    public static Map<String, Object> feedCommentUpdateRequest_수정(String newContent) {
        return Map.of("content", newContent);
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

    public static ExtractableResponse<Response> 피드의_댓글을_수정한다(
            Map<String, Object> feedCommentSaveRequest,
            Long commentId,
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
                .patch("/api/feeds/comments/{commentId}", commentId)
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

    public static ExtractableResponse<Response> 문서_없이_피드의_모든_댓글을_조회한다(
            Long feedId,
            String accessToken) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .get("/api/feeds/{feedId}/comments", feedId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 피드의_댓글을_삭제한다(
            Long commentId,
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
                .delete("/api/feeds/comments/{commentId}", commentId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 피드의_댓글을_하나를_조회한다(
            Long commentId,
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
                .get("/api/feeds/comments/{commentId}", commentId)
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

    public static void 피드_댓글_수정_응답을_검증한다(ExtractableResponse<Response> response) {
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
                            "createdAt", "updatedAt", "likeCount", "isLiked");
                    assertThat(firstComment.get("content")).isEqualTo("댓글1");

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

    public static void 피드_댓글_삭제_후_조회_응답을_검증한다(
            ExtractableResponse<Response> response,
            int expectedSize,
            String expectedRemainingContent,
            String deletedContent
    ) {
        List<Map<String, Object>> comments = response.jsonPath().getList("content");

        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK),

                () -> assertThat(comments)
                        .isNotNull()
                        .hasSize(expectedSize),

                // 남아있는 댓글의 내용이 예상과 일치하는지 검증
                () -> assertThat(comments.get(0).get("content"))
                        .isEqualTo(expectedRemainingContent),

                // 응답 목록에 삭제된 댓글의 내용이 없는지 검증
                () -> assertThat(comments.stream()
                        .map(comment -> comment.get("content").toString())
                        .collect(Collectors.toList()))
                        .doesNotContain(deletedContent)
        );
    }

    public static void 피드_삭제_후_댓글_조회_응답을_검증한다(
            ExtractableResponse<Response> response
    ) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.NOT_FOUND)
        );
    }

    public static ExtractableResponse<Response> 피드_댓글에_좋아요를_누른다(
            Long commentId,
            String accessToken,
            RequestSpecification spec
    ) {
        return RestAssured
                .given()
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .post("/api/feeds/comments/{commentId}/like", commentId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 피드_댓글의_좋아요를_취소한다(
            Long commentId,
            String accessToken,
            RequestSpecification spec
    ) {
        return RestAssured
                .given()
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .delete("/api/feeds/comments/{commentId}/like", commentId)
                .then()
                .log().all()
                .extract();
    }

    public static void 피드_댓글_좋아요_정보를_검증한다(
            ExtractableResponse<Response> response,
            Long targetCommentId,
            int expectedLikeCount,
            boolean expectedIsLiked
    ) {
        List<Map<String, Object>> comments = response.jsonPath().getList("content");

        // 검증하려는 특정 댓글을 찾습니다.
        Map<String, Object> targetComment = comments.stream()
                .filter(comment -> Long.valueOf(comment.get("id").toString()).equals(targetCommentId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Target comment not found. ID: " + targetCommentId));

        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK),
                () -> assertThat((Integer) targetComment.get("likeCount")).isEqualTo(expectedLikeCount),
                () -> assertThat((Boolean) targetComment.get("isLiked")).isEqualTo(expectedIsLiked)
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

    public static void 상태코드가_429이다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.TOO_MANY_REQUESTS)
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
