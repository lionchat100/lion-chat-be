package com.lion.be.acceptance.chat;

import static com.lion.be.acceptance.user.UserSteps.상태코드를_검증한다;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lion.be.chat.domain.dto.ChatMessageDto;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.OpponentUserRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class ChatSteps {

    // STOMP 클라이언트 설정을 위한 ObjectMapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    // REST API Step: 상대방과 채팅방 생성/입장 요청
    public static ExtractableResponse<Response> 채팅방을_생성한다(String accessToken, Long opponentId, RequestSpecification spec) {
        OpponentUserRequest request = new OpponentUserRequest();
        request.setId(opponentId);

        return RestAssured
                .given().spec(spec).log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/chats")
                .then().log().all()
                .extract();
    }

    // REST API Step: 자신의 채팅방 목록 조회
    public static ExtractableResponse<Response> 내_채팅방_목록을_조회한다(String accessToken, RequestSpecification spec) {
        return RestAssured
                .given().spec(spec).log().all()
                .auth().oauth2(accessToken)
                .when().get("/api/chats")
                .then().log().all()
                .extract();
    }
    
    // REST API Step: 특정 채팅방의 메시지 내역 조회
    public static ExtractableResponse<Response> 채팅_메시지_내역을_조회한다(String accessToken, Long roomId, RequestSpecification spec) {
        return RestAssured
            .given().spec(spec).log().all()
            .auth().oauth2(accessToken)
            .when().get("/api/chatmessages/{roomId}", roomId)
            .then().log().all()
            .extract();
    }

    // WebSocket Step: STOMP 클라이언트 생성 및 연결 (수정된 부분)
    public static StompSession STOMP_연결을_하고_세션을_반환한다(int port, String accessToken) throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(
                new SockJsClient(Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient())))
        );

        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(messageConverter);

        // STOMP 프로토콜의 CONNECT 프레임에 포함될 헤더 (JWT 인증용)
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // WebSocket 최초 연결 시 사용될 핸드셰이크 헤더 (여기서는 특별한 값 불필요)
        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        String url = String.format("ws://localhost:%d/ws", port);

        // 모호성을 없애기 위해 두 번째 인자로 null 대신 handshakeHeaders 객체를 전달
        return stompClient.connectAsync(url, handshakeHeaders, connectHeaders, new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);
    }

    // WebSocket Step: 특정 채팅방 토픽 구독
    public static StompSession.Subscription 채팅방을_구독한다(StompSession session, Long chatRoomId, BlockingQueue<ChatMessageDto> messageQueue) {
        return session.subscribe("/topic/chatroom" + chatRoomId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                // 수신할 메시지의 타입을 지정
                return ChatMessageDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // 메시지 수신 시 큐에 추가
                messageQueue.add((ChatMessageDto) payload);
            }
        });
    }

    // WebSocket Step: 채팅 메시지 전송
    public static void 채팅_메시지를_전송한다(StompSession session, Long chatRoomId, String content) {
        ChatMessageRequest request = new ChatMessageRequest();
        request.setChatRoomId(chatRoomId);
        request.setContent(content);
        request.setDate(LocalDateTime.now());

        session.send("/app/chat.sendMessage", request);
    }
    
    // Assertion Step: 수신된 메시지 검증
    public static void 수신된_메시지를_검증한다(BlockingQueue<ChatMessageDto> messageQueue, String expectedSenderName, String expectedContent) throws InterruptedException {
        // 큐에서 메시지를 꺼낼 때까지 5초간 대기
        ChatMessageDto receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS);

        Assertions.assertAll(
                () -> assertThat(receivedMessage).isNotNull(),
                () -> assertThat(receivedMessage.getSenderName()).isEqualTo(expectedSenderName),
                () -> assertThat(receivedMessage.getContent()).isEqualTo(expectedContent)
        );
    }

    // Assertion Step: 채팅방 목록 응답 검증
    public static void 채팅방_목록_응답을_검증한다(ExtractableResponse<Response> response, String lastChatContent) {
        Assertions.assertAll(
                () -> 상태코드를_검증한다(response, HttpStatus.OK),
                () -> assertThat(response.jsonPath().getList("$")).isNotEmpty(),
                () -> assertThat(response.jsonPath().getString("[0].lastChat")).isEqualTo(lastChatContent)
        );
    }
}