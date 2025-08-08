package com.lion.be.acceptance.feed;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

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
}
