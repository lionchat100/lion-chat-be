package com.lion.be.acceptance.chat;

import static com.lion.be.acceptance.chat.ChatSteps._1대1_채팅방을_생성_또는_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.기존_채팅방_조회_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.메시지_목록_조회_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.메시지_전송_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.상태코드가_200이다;
import static com.lion.be.acceptance.chat.ChatSteps.상태코드가_429이다;
import static com.lion.be.acceptance.chat.ChatSteps.자신의_채팅방_목록을_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방_목록_조회_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방_생성_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방에_메시지를_전송한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방의_메시지_목록을_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방의_초기_메시지_목록을_조회한다;
import static com.lion.be.acceptance.user.UserSteps.회원_id를_가져온다;

import com.lion.be.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 관련 인수 테스트")
public class ChatAcceptanceTest extends AcceptanceTest {

    String 사용자1_토큰;
    String 사용자2_토큰;

    Long 사용자1_ID;
    Long 사용자2_ID;

    @BeforeEach
    void before() {
        사용자1_토큰 = 회원_원준_액세스토큰;
        사용자2_토큰 = 비회원_엑세스토큰;

        사용자1_ID = 회원_id를_가져온다(spec, 사용자1_토큰).jsonPath().getLong("id");
        사용자2_ID = 회원_id를_가져온다(spec, 사용자2_토큰).jsonPath().getLong("id");
    }

    @Nested
    @DisplayName("채팅방 생성 및 조회 테스트")
    class ChatRoomTest {

        @DisplayName("두 사용자 간에 첫 채팅 시 새로운 채팅방이 생성된다.")
        @Test
        void when_create_chat_room_first_time_then_response_200() {
            // given
            api_문서_타이틀("create_chat_room_success", spec);

            // when
            // 사용자1이 사용자2에게 채팅을 요청
            var response = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 사용자2_ID, spec);

            // then
            채팅방_생성_응답을_검증한다(response);
        }

        @DisplayName("이미 채팅방이 존재할 경우, 기존 채팅방 ID를 반환한다.")
        @Test
        void when_chat_room_already_exists_then_return_existing_room() {
            // given
            api_문서_타이틀("find_existing_chat_room_success", spec);
            var firstResponse = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 사용자2_ID, spec);
            Long firstChatRoomId = firstResponse.jsonPath().getLong("chatRoomId");

            // when
            var secondResponse = _1대1_채팅방을_생성_또는_조회한다(사용자2_토큰, 사용자1_ID, spec);

            // then
            기존_채팅방_조회_응답을_검증한다(secondResponse, firstChatRoomId);
        }

        @DisplayName("자신이 속한 채팅방 목록을 최신 메시지 순으로 조회한다.")
        @Test
        void when_fetch_my_chat_rooms_then_response_200() {
            // given
            api_문서_타이틀("fetch_my_chat_rooms_success", spec);

            // data.sql에 있는 ID 6번 '김백엔드' 유저와 채팅방 생성
            Long 김백엔드_ID = 8L;

            var chatRoomResponse1 = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 사용자2_ID, spec);
            Long chatRoomId1 = chatRoomResponse1.jsonPath().getLong("chatRoomId");
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId1, "동적 사용자2님, 안녕하세요?", spec);

            var chatRoomResponse2 = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 김백엔드_ID, spec);
            Long chatRoomId2 = chatRoomResponse2.jsonPath().getLong("chatRoomId");
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId2, "김백엔드님, 안녕하세요.", spec);

            // when
            var response = 자신의_채팅방_목록을_조회한다(사용자1_토큰, spec);

            // then
            채팅방_목록_조회_응답을_검증한다(response, 2, "백엔드전문가");
        }

    }

    @Nested
    @DisplayName("채팅 메시지 전송 및 조회 테스트")
    class ChatMessageTest {

        private Long chatRoomId;

        @BeforeEach
        void before() {
            var response = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 사용자2_ID, spec);
            chatRoomId = response.jsonPath().getLong("chatRoomId");
        }

        @DisplayName("채팅방에 메시지를 성공적으로 전송한다.")
        @Test
        void when_send_message_then_response_200() {
            // given
            api_문서_타이틀("send_chat_message_success", spec);

            // when
            var response = 채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "안녕하세요, 첫 메시지입니다.", spec);

            // then
            메시지_전송_응답을_검증한다(response);
        }

        @DisplayName("채팅방의 초기 메시지 목록을 페이지네이션으로 조회한다.")
        @Test
        void when_fetch_init_messages_then_response_200() throws InterruptedException {
            // given
            api_문서_타이틀("fetch_init_chat_messages_success", spec);
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "첫 번째 메시지", spec);
            Thread.sleep(10);
            채팅방에_메시지를_전송한다(사용자2_토큰, chatRoomId, "아, 네. 안녕하세요.", spec);
            Thread.sleep(10);
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "반갑습니다.", spec);

            // when
            var response = 채팅방의_초기_메시지_목록을_조회한다(사용자1_토큰, chatRoomId, null, spec);

            // then
            메시지_목록_조회_응답을_검증한다(response, 3, "반갑습니다.");
        }

        @DisplayName("채팅방의 메시지 목록을 페이지네이션으로 조회한다.")
        @Test
        void when_fetch_messages_then_response_200() throws InterruptedException {
            // given
            api_문서_타이틀("fetch_chat_messages_success", spec);
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "첫 번째 메시지", spec);
            Thread.sleep(10);
            채팅방에_메시지를_전송한다(사용자2_토큰, chatRoomId, "아, 네. 안녕하세요.", spec);
            Thread.sleep(10);
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "반갑습니다.", spec);
            String lastId = null;

            // when
            var response = 채팅방의_메시지_목록을_조회한다(사용자1_토큰, chatRoomId, lastId, spec);

            // then
            메시지_목록_조회_응답을_검증한다(response, 3, "반갑습니다.");
        }

    }

    @Nested
    @DisplayName("채팅 메시지 전송 속도 제한 테스트")
    class RateLimitingTest {

        private Long chatRoomId;

        @BeforeEach
        void before() {
            // 모든 테스트 전에 채팅방을 하나 생성합니다.
            var response = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 사용자2_ID, spec);
            chatRoomId = response.jsonPath().getLong("chatRoomId");
        }

        @DisplayName("1초에 2개를 초과하여 메시지를 보내면 429 에러가 발생한다")
        @Test
        void when_send_message_too_fast_then_throw_429() throws InterruptedException {
            // given
            api_문서_타이틀("chat_rate_limit_sustained_fail", spec);

            // when & then
            // 1. 첫 번째, 두 번째 요청은 1초 안에 보내도 성공한다.
            var firstResponse = 채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "첫 번째 메시지", spec);
            상태코드가_200이다(firstResponse);

            var secondResponse = 채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "두 번째 메시지", spec);
            상태코드가_200이다(secondResponse);

            // 2. 1초가 지나기 전 세 번째 요청은 실패한다.
            var thirdResponse = 채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "세 번째 메시지", spec);
            상태코드가_429이다(thirdResponse);

            // 3. 1초가 지난 후의 요청은 다시 성공한다.
            Thread.sleep(1100); // 1.1초 대기
            var fourthResponse = 채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "네 번째 메시지", spec);
            상태코드가_200이다(fourthResponse);
        }

        @DisplayName("10초에 10개를 초과하여 메시지를 보내면 429 에러가 발생한다")
        @Test
        void when_send_more_than_10_messages_in_10_seconds_then_throw_429() throws InterruptedException {
            // given
            api_문서_타이틀("chat_rate_limit_burst_fail", spec);

            // when & then
            // 1. 10개의 요청을 단기 제한(1초에 2개)에 걸리지 않도록 0.6초 간격으로 보내 모두 성공시킨다.
            for (int i = 0; i < 10; i++) {
                var response = 채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "메시지 " + (i + 1), spec);
                상태코드가_200이다(response);
                Thread.sleep(600); // 단기 제한(2/sec)을 피하기 위해 0.5초 이상 대기
            }

            // 2. 11번째 요청은 10초 제한에 걸려 실패한다.
            var eleventhResponse = 채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "열한 번째 메시지", spec);
            상태코드가_429이다(eleventhResponse);
        }

    }

}