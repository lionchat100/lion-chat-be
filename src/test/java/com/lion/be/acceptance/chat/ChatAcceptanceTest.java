package com.lion.be.acceptance.chat;

import static com.lion.be.acceptance.chat.ChatSteps._1대1_채팅방을_생성_또는_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.기존_채팅방_조회_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.메시지_목록_조회_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.메시지_전송_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.자신의_채팅방_목록을_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방_목록_조회_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방_생성_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방에_메시지를_전송한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방의_메시지_목록을_조회한다;
import static com.lion.be.acceptance.user.UserSteps.회원_id를_가져온다;

import com.lion.be.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("채팅 관련 인수 테스트")
public class ChatAcceptanceTest extends AcceptanceTest {

    // 토큰은 AcceptanceTest의 것을 그대로 사용
    String 사용자1_토큰;
    String 사용자2_토큰;

    // ID는 하드코딩 대신 동적으로 조회해서 사용
    Long 사용자1_ID;
    Long 사용자2_ID;

    @BeforeEach
    void before() {
        // 1. AcceptanceTest가 생성해준 토큰을 가져옵니다.
        사용자1_토큰 = 회원_원준_액세스토큰;
        사용자2_토큰 = 비회원_엑세스토큰;

        // 2. 각 토큰을 이용해 사용자의 실제 ID를 API로 조회합니다. (가장 중요!)
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
            // 사용자1과 사용자2 간의 채팅방을 미리 하나 생성
            var firstResponse = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 사용자2_ID, spec);
            Long firstChatRoomId = firstResponse.jsonPath().getLong("chatRoomId");

            // when
            // 이번엔 사용자2가 사용자1에게 다시 채팅을 요청
            var secondResponse = _1대1_채팅방을_생성_또는_조회한다(사용자2_토큰, 사용자1_ID, spec);

            // then
            // 두 번째 요청에서도 첫 번째와 동일한 채팅방 ID를 반환하는지 검증
            기존_채팅방_조회_응답을_검증한다(secondResponse, firstChatRoomId);
        }

        @DisplayName("자신이 속한 채팅방 목록을 최신 메시지 순으로 조회한다.")
        @Test
        void when_fetch_my_chat_rooms_then_response_200() {
            // given
            api_문서_타이틀("fetch_my_chat_rooms_success", spec);

            // data.sql에 있는 ID 6번 '김백엔드' 유저와 채팅방 생성
            // **참고**: 이 테스트는 동적 유저(사용자1)와 정적 유저(김백엔드) 간의 상호작용을 검증합니다.
            Long 김백엔드_ID = 6L;

            // 1. 사용자1(동적)과 사용자2(동적)의 채팅방 생성 및 메시지 전송
            var chatRoomResponse1 = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 사용자2_ID, spec);
            Long chatRoomId1 = chatRoomResponse1.jsonPath().getLong("chatRoomId");
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId1, "동적 사용자2님, 안녕하세요?", spec);

            // 2. 사용자1(동적)과 김백엔드(정적, ID:6)의 채팅방 생성 및 메시지 전송
            var chatRoomResponse2 = _1대1_채팅방을_생성_또는_조회한다(사용자1_토큰, 김백엔드_ID, spec);
            Long chatRoomId2 = chatRoomResponse2.jsonPath().getLong("chatRoomId");
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId2, "김백엔드님, 안녕하세요.", spec);

            // when
            // 사용자1의 채팅방 목록을 조회
            var response = 자신의_채팅방_목록을_조회한다(사용자1_토큰, spec);

            // then
            // 채팅방이 2개이고, 가장 최근에 메시지를 보낸 '김백엔드'와의 채팅방이 먼저 오는지 검증
            채팅방_목록_조회_응답을_검증한다(response, 2, "김백엔드");
        }

    }

    @Nested
    @DisplayName("채팅 메시지 전송 및 조회 테스트")
    class ChatMessageTest {

        private Long chatRoomId;

        @BeforeEach
        void before() {
            // 모든 메시지 테스트 전에 사용자1과 사용자2의 채팅방을 미리 생성
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

        @DisplayName("채팅방의 메시지 목록을 페이지네이션으로 조회한다.")
        @Test
        void when_fetch_messages_then_response_200() throws InterruptedException {
            // given
            api_문서_타이틀("fetch_chat_messages_success", spec);
            // 여러 개의 메시지를 순차적으로 전송
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "첫 번째 메시지", spec);
            Thread.sleep(10); // 메시지 순서 보장을 위한 약간의 딜레이
            채팅방에_메시지를_전송한다(사용자2_토큰, chatRoomId, "아, 네. 안녕하세요.", spec);
            Thread.sleep(10);
            채팅방에_메시지를_전송한다(사용자1_토큰, chatRoomId, "반갑습니다.", spec);

            // when
            var response = 채팅방의_메시지_목록을_조회한다(사용자1_토큰, chatRoomId, spec);

            // then
            // 메시지가 3개이고, 마지막에 보낸 "반갑습니다."가 가장 먼저 조회되는지 검증
            메시지_목록_조회_응답을_검증한다(response, 3, "반갑습니다.");
        }

    }

}