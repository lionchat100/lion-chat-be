package com.lion.be.acceptance.notification;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lion.be.acceptance.feed.FeedSteps.상태코드_200이다;
import static org.assertj.core.api.Assertions.assertThat;

public class NotificationSteps {
    public static ExtractableResponse<Response> 알림을_조회한다(String accessToken, RequestSpecification spec) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .get("/api/notifications")
                .then()
                .log().all()
                .extract();
    }

    public static void 알림_전체_조회_응답을_검증한다(ExtractableResponse<Response> response, int expectedCount) {
        List<Map<String, Object>> content = response.jsonPath().getList("content");

        Assertions.assertAll(
                () -> 상태코드_200이다(response),

                () -> assertThat(content)
                        .isNotNull()
                        .hasSize(expectedCount)

        );
    }




}
