package com.lion.be.acceptance.chat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatSteps {

    public static ExtractableResponse<Response> _1대1_채팅방을_생성_또는_조회한다(
            String accessToken, Long receiverId, RequestSpecification spec) {
        Map<String, Long> requestBody = Map.of("receiverId", receiverId);
        return RestAssured
                .given().log().all()
                .spec(spec)
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .post("/api/chatrooms/init")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 자신의_채팅방_목록을_조회한다(
            String accessToken, RequestSpecification spec) {
        return RestAssured
                .given().log().all()
                .spec(spec)
                .auth().oauth2(accessToken)
                .when()
                .get("/api/chatrooms")
                .then().log().all()
                .extract();
    }

    // ✅ 수정된 부분: TestChatController 호출
    public static ExtractableResponse<Response> 채팅방에_메시지를_전송한다(
            String accessToken, Long chatRoomId, String content, RequestSpecification spec) {
        Map<String, Object> requestBody = Map.of("chatRoomId", chatRoomId, "content", content);
        return RestAssured
                .given().log().all()
                .spec(spec)
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                // 새로 만든 테스트 전용 API 경로로 수정
                .post("/api/test/chat/messages")
                .then().log().all()
                .extract();
    }

    // ✅ 수정된 부분: ChatController의 실제 경로와 파라미터 방식에 맞게 수정
    public static ExtractableResponse<Response> 채팅방의_메시지_목록을_조회한다(
            String accessToken, Long chatRoomId, RequestSpecification spec) {
        return RestAssured
                .given().log().all()
                .spec(spec)
                .auth().oauth2(accessToken)
                // PathVariable이 아닌 RequestParam으로 roomId 전달
                .queryParam("roomId", chatRoomId)
                // lastId는 기본값이 0이므로 명시적으로 보내지 않아도 테스트 가능
                .when()
                // 실제 Controller 경로인 "/api/chatrooms/chats/messages"로 수정
                .get("/api/chatrooms/chats/messages")
                .then().log().all()
                .extract();
    }


    /* --- 응답 검증 메서드 --- */

    public static void 채팅방_생성_응답을_검증한다(ExtractableResponse<Response> response) {
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("chatRoomId")).isNotNull().isPositive()
        );
    }

    public static void 기존_채팅방_조회_응답을_검증한다(ExtractableResponse<Response> response, Long expectedChatRoomId) {
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getLong("chatRoomId")).isEqualTo(expectedChatRoomId)
        );
    }

    // ✅ 수정된 부분: DTO 필드명 일치
    public static void 채팅방_목록_조회_응답을_검증한다(ExtractableResponse<Response> response, int expectedSize, String expectedFirstName) {
        List<Map<String, Object>> chatRooms = response.jsonPath().getList("$");
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(chatRooms).hasSize(expectedSize),
                () -> assertThat(chatRooms.get(0).get("name").toString()).isEqualTo(expectedFirstName),
                () -> assertThat(chatRooms.get(0).get("chatRoomId")).isNotNull(),
                () -> assertThat(chatRooms.get(0)).containsKey("lastContent")
        );
    }

    public static void 메시지_전송_응답을_검증한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 메시지_목록_조회_응답을_검증한다(ExtractableResponse<Response> response, int expectedSize, String firstMessageContent) {
        List<Map<String, Object>> messages = response.jsonPath().getList("content");

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(messages).hasSize(expectedSize),
                () -> assertThat(messages.get(0).get("content")).isEqualTo(firstMessageContent),
                () -> assertThat(messages.get(0).get("messageId")).isNotNull(),
                () -> assertThat(messages.get(0).get("senderName")).isNotNull(),
                () -> assertThat(response.jsonPath().getBoolean("isEnd")).isTrue()
        );
    }
}