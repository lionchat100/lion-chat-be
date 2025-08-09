package com.lion.be.acceptance.feed;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedSteps {

    public static ExtractableResponse<Response> 피드를_작성한다(String accessToken, RequestSpecification spec, String title, String content) {
        Map<String, String> feedWriteRequest = new HashMap<>();
        feedWriteRequest.put("title", title);
        feedWriteRequest.put("content", content);

        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .body(feedWriteRequest)
                .log().all()
                .when()
                .post("/api/feeds")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 최신_피드를_조회한다(String accessToken, RequestSpecification spec) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .get("/api/feeds")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 피드_하나를_조회한다(String accessToken, RequestSpecification spec, Long feedId) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .get("/api/feeds/{feedId}",feedId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 인기_피드를_조회한다(String accessToken, RequestSpecification spec) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .get("/api/feeds/hot")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 피드를_삭제한다(String accessToken, RequestSpecification spec, Long feedId) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .delete("/api/feeds/{feedId}", feedId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 피드를_수정한다(String accessToken, RequestSpecification spec, Long feedId, String title, String content) {
        Map<String, String> feedUpdateRequest = new HashMap<>();
        feedUpdateRequest.put("title", title);
        feedUpdateRequest.put("content", content);

        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .body(feedUpdateRequest)
                .log().all()
                .when()
                .put("/api/feeds/{feedId}", feedId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 피드에_좋아요를_누른다(
            String accessToken,
            RequestSpecification spec,
            Long feedId
    ) {
        return RestAssured
                .given()
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .post("/api/feeds/{feedId}/like", feedId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 피드의_좋아요를_취소한다(
            String accessToken,
            RequestSpecification spec,
            Long feedId
    ) {
        return RestAssured
                .given()
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .delete("/api/feeds/{feedId}/like", feedId)
                .then()
                .log().all()
                .extract();
    }

    public static void 피드_좋아요_정보를_검증한다(
            ExtractableResponse<Response> response,
            Long targetFeedId,
            int expectedLikeCount,
            boolean expectedIsLiked
    ) {
        // 단건 조회 응답 구조에 맞게 검증 로직 수정
        Map<String, Object> feed = response.jsonPath().getMap("$"); // 응답 전체가 FeedDto 객체

        Assertions.assertAll(
                () -> 상태코드_200이다(response),
                () -> assertThat(feed.get("id")).isEqualTo(targetFeedId.intValue()), // JSON Path는 숫자를 Integer로 반환할 수 있음
                () -> assertThat(feed.get("likeCount")).isEqualTo(expectedLikeCount),
                () -> assertThat(feed.get("isLiked")).isEqualTo(expectedIsLiked)
        );
    }

    public static void 상태코드_200이다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(200);
    }

    public static void 상태코드_404이다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(404);
    }

    public static void 상태코드_401이다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode())
                .isEqualTo(401);
    }

    public static void 피드의_동등성을_검증한다(ExtractableResponse<Response> response, Long id, String title, String content) {
        assertThat(response.jsonPath().getLong("id"))
                .isEqualTo(id);
        assertThat(response.jsonPath().getString("title"))
                .isEqualTo(title);
        assertThat(response.jsonPath().getString("content"))
                .isEqualTo(content);
    }

    public static void 피드_전체_조회_응답을_검증한다(ExtractableResponse<Response> response, int expectedCount, List<String> expectedTitles, List<String> expectedContents) {

        List<Map<String, Map<String,Object>>> content = response.jsonPath().getList("content");

        Assertions.assertAll(
                () -> 상태코드_200이다(response),

                () -> assertThat(content)
                        .isNotNull()
                        .hasSize(expectedCount),

                () -> {
                    for(int i=expectedCount-1; i>=0; i--){
                        Map<String, Map<String,Object>> feedResponse = content.get(i);
                        Map<String, Object> feed = feedResponse.get("feed");
                        assertThat(feed).containsKeys("id", "title", "content", "likeCount", "createdAt");
                        int listIndex = expectedCount - 1 - i; // 역순으로 접근
                        assertThat(feed.get("title")).isEqualTo(expectedTitles.get(listIndex));
                        assertThat(feed.get("content")).isEqualTo(expectedContents.get(listIndex));

                        Map<String, Object> user = feedResponse.get("writer");
                        assertThat(user).containsKeys("name", "id", "profileImageUrl");
                    }
                },

                () -> {
                    assertThat(response.jsonPath().getMap("pageable")).isNotNull();
                    assertThat(response.jsonPath().getBoolean("last")).isNotNull();
                    assertThat(response.jsonPath().getInt("numberOfElements")).isEqualTo(expectedCount);
                }
        );
    }

    public static void 피드_삭제_후_조회_응답을_검증한다(
            ExtractableResponse<Response> response,
            int expectedSize,
            List<String> expectedRemainingTitles,
            List<String> expectedRemainingContents,
            String deletedTitle,
            String deletedContent
    ) {
        List<Map<String, Map<String,Object>>> content = response.jsonPath().getList("content");

        Assertions.assertAll(
                () -> 상태코드_200이다(response),

                () -> assertThat(content)
                        .isNotNull()
                        .hasSize(expectedSize),

                // 남아있는 댓글의 내용이 예상과 일치하는지 검증
                () -> {
                    for(int i=expectedSize-1; i>=0; i--){
                        Map<String, Map<String,Object>> feedResponse = content.get(i);
                        Map<String, Object> feed = feedResponse.get("feed");
                        int listIndex = expectedSize - 1 - i; // 역순으로 접근
                        assertThat(feed.get("title")).isEqualTo(expectedRemainingTitles.get(listIndex));
                        assertThat(feed.get("content")).isEqualTo(expectedRemainingContents.get(listIndex));
                    }
                },

                // 응답 목록에 삭제된 댓글의 내용이 없는지 검증
                () -> {
                    assertThat(
                            content.stream()
                            .filter(feedResponse -> {
                                Map<String, Object> feed = feedResponse.get("feed");
                                return feed.get("title").equals(deletedTitle) || feed.get("content").equals(deletedContent);
                            }).findFirst().isEmpty()).isTrue();
                }
        );
    }
}
