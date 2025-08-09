package com.lion.be.acceptance.chat;

import static com.lion.be.acceptance.auth.AuthSteps.비회원이_로그인한다;
import static com.lion.be.acceptance.user.UserSteps.상태코드가_200이다;
import static com.lion.be.acceptance.user.UserSteps.온보딩을_완료한다;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("채팅 관련 기능 인수테스트")
class ChatAcceptTest extends AcceptanceTest {

    // todo 테스트용 로그인에 문제가 있어 테스트가 통과되지 않습니다. 추후 수정이 필요합니다.
    @Nested
    @DisplayName("메시지 전송 인수테스트")
    class ChatMessageTest {

        @DisplayName("온보딩 완료한 회원이 채팅 메시지를 전송하면, 상태코드 200을 반환한다.")
        @Test
        void when_onboarded_user_sends_chat_message_then_response_200() {
            // given
            api_문서_타이틀("send-chat-message-success", spec);

            // 1. 메시지를 보낼 사용자 로그인 및 온보딩
            var userLoginResponse = 비회원이_로그인한다(spec);
            String accessToken = userLoginResponse.jsonPath().getString("accessToken");
            온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), accessToken, spec);

            // 2. 메시지 전송 요청 본문 생성 (senderName과 senderId를 제거)
            Long chatRoomId = 1L;
            String senderName = "cinnamein";
            Long senderId = 2L;
            String content = "안녕하세요! 메시지 전송 테스트입니다.";
            ChatMessageRequest chatMessageRequest = new ChatMessageRequest(
                    chatRoomId,
                    senderName,
                    senderId,
                    content
            );

            // when
            var response = RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .spec(spec)
                    .auth().oauth2(accessToken)
                    .log().all()
                    .body(chatMessageRequest)
                    .when()
                    .post("/api/chat/messages")
                    .then()
                    .log().all()
                    .extract();

            // then
            상태코드가_200이다(response);
        }
    }
}