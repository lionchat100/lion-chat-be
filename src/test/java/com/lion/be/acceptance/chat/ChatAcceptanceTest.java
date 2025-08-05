package com.lion.be.acceptance.chat;

import static com.lion.be.acceptance.auth.AuthSteps.비회원이_로그인한다;
import static com.lion.be.acceptance.auth.AuthSteps.원준이_로그인한다;
import static com.lion.be.acceptance.chat.ChatSteps.STOMP_연결을_하고_세션을_반환한다;
import static com.lion.be.acceptance.chat.ChatSteps.내_채팅방_목록을_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅_메시지_내역을_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅_메시지를_전송한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방_목록_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방을_생성한다;
import static com.lion.be.acceptance.user.UserSteps.온보딩을_완료한다;
import static com.lion.be.acceptance.util.AuthFixture.비회원_로그인_요청;
import static com.lion.be.acceptance.util.AuthFixture.사용자_원준_로그인_요청;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사2_온보딩_요청;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사_온보딩_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import io.restassured.path.json.JsonPath;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;

@DisplayName("채팅 관련 기능 인수테스트")
public class ChatAcceptanceTest extends AcceptanceTest {

    // 구현체 대신 인터페이스를 주입받는 것이 좋습니다.
    @Autowired
    private UserRepository userRepository;

    private String user1AccessToken;
    private String user2AccessToken;
    private Long user1Id;
    private Long user2Id;
    private String user1Name;
    private String user2Name;

    private void safeWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting", e);
        }
    }

    @BeforeEach
    void chatSetup() {
        var user1LoginResponse = 원준이_로그인한다(spec);
        user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");

        User user1 = userRepository.fetchByEmail(사용자_원준_로그인_요청().get("email").toString())
                .orElseThrow(() -> new IllegalStateException("테스트 사용자를 찾을 수 없습니다: " + 사용자_원준_로그인_요청().get("email")));
        user1Id = user1.getId();
        user1Name = user1.getName(); // DB에서 직접 이름을 가져와 일관성 유지
        // 온보딩 완료 (이미 완료되었을 수 있지만, 테스트의 명확성을 위해 호출)
        온보딩을_완료한다(회원_멋사_온보딩_요청(), user1AccessToken, spec);

        var user2LoginResponse = 비회원이_로그인한다(spec);
        user2AccessToken = user2LoginResponse.jsonPath().getString("accessToken");

        User user2 = userRepository.fetchByEmail(비회원_로그인_요청().get("email").toString())
                .orElseThrow(() -> new IllegalStateException("테스트 사용자를 찾을 수 없습니다: " + 비회원_로그인_요청().get("email")));
        user2Id = user2.getId();
        user2Name = user2.getName(); // DB에서 직접 이름을 가져와 일관성 유지
        // 온보딩 완료
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), user2AccessToken, spec);
    }

    @Test
    @DisplayName("사용자 간에 채팅방을 생성하고, 메시지를 주고받은 후, 채팅 목록과 메시지 내역을 조회한다.")
    void fullChatFlowTest() throws Exception {
        // 1. 채팅방 생성 (동일)
        api_문서_타이틀("create_chat_room", spec);
        var createRoomResponse = 채팅방을_생성한다(user1AccessToken, user2Id, spec);
        Long chatRoomId = createRoomResponse.jsonPath().getLong("roomId");

        // WebSocket 연결
        StompSession user1Session = STOMP_연결을_하고_세션을_반환한다(port, user1AccessToken);

        // 4. 사용자1이 메시지 전송
        String messageContent = "안녕하세요! 채팅 테스트입니다.";
        채팅_메시지를_전송한다(user1Session, chatRoomId, messageContent);

        // 6. [검증 1] 사용자2의 채팅방 목록 API를 반복적으로 호출하여,
        // 마지막 메시지가 업데이트될 때까지 최대 10초간 기다린다.
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var chatRoomListResponse = 내_채팅방_목록을_조회한다(user2AccessToken, spec);
            채팅방_목록_응답을_검증한다(chatRoomListResponse, messageContent);
        });

        api_문서_타이틀("get_my_chat_rooms", spec);
        내_채팅방_목록을_조회한다(user2AccessToken, spec);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var chatMessagesResponse = 채팅_메시지_내역을_조회한다(user1AccessToken, chatRoomId, spec);
            JsonPath messageHistory = chatMessagesResponse.jsonPath();

            assertThat(messageHistory.getList("$")).hasSize(1);
            assertThat(messageHistory.getString("[0].content")).isEqualTo(messageContent);
            assertThat(messageHistory.getString("[0].senderName")).isEqualTo(user1Name);
            assertThat(messageHistory.getLong("[0].senderId")).isEqualTo(user1Id);
        });

        api_문서_타이틀("get_chat_messages", spec);
        채팅_메시지_내역을_조회한다(user1AccessToken, chatRoomId, spec);

        // 세션 연결 해제
        if (user1Session != null && user1Session.isConnected()) {
            user1Session.disconnect();
        }
    }

    @Test
    @DisplayName("두 사용자가 동시에 메시지를 보내도 데이터 정합성이 유지된다.")
    void concurrencyMessageSendTest() throws Exception {
        // 1. 테스트 준비: 채팅방 생성
        var createRoomResponse = 채팅방을_생성한다(user1AccessToken, user2Id, spec);
        Long chatRoomId = createRoomResponse.jsonPath().getLong("roomId");

        // WebSocket 연결
        StompSession user1Session = STOMP_연결을_하고_세션을_반환한다(port, user1AccessToken);
        StompSession user2Session = STOMP_연결을_하고_세션을_반환한다(port, user2AccessToken);

        // 2. 동시 요청 준비
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount); // 2개의 스레드를 가진 스레드 풀 생성
        CountDownLatch latch = new CountDownLatch(threadCount); // 모든 스레드가 동시에 시작하도록 돕는 도구

        String user1Message = "사용자1의 동시성 테스트 메시지";
        String user2Message = "사용자2의 동시성 테스트 메시지";

        // 3. 각 스레드가 수행할 작업 정의
        // 사용자 1의 메시지 전송 작업
        executorService.submit(() -> {
            try {
                latch.countDown(); // "준비 완료" 신호
                latch.await();     // 다른 스레드가 준비될 때까지 대기
                채팅_메시지를_전송한다(user1Session, chatRoomId, user1Message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 사용자 2의 메시지 전송 작업
        executorService.submit(() -> {
            try {
                latch.countDown(); // "준비 완료" 신호
                latch.await();     // 다른 스레드가 준비될 때까지 대기
                채팅_메시지를_전송한다(user2Session, chatRoomId, user2Message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 스레드 풀 종료
        executorService.shutdown();

        // 4. Awaitility를 사용하여 최종 결과 검증
        // [검증 1] 채팅 메시지 내역에 두 개의 메시지가 모두 저장되었는지 확인
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var chatMessagesResponse = 채팅_메시지_내역을_조회한다(user1AccessToken, chatRoomId, spec);
            JsonPath messageHistory = chatMessagesResponse.jsonPath();

            assertThat(messageHistory.getList("$").size()).isEqualTo(2);
            assertThat(messageHistory.getList("content", String.class)).contains(user1Message, user2Message);
        });

        // [검증 2] 채팅방 목록의 마지막 메시지가 두 메시지 중 하나로 올바르게 업데이트 되었는지 확인
        // (어떤 메시지가 마지막이 될지는 실행 시점에 따라 다르므로, 둘 중 하나이면 성공으로 간주)
        var chatRoomListResponse = 내_채팅방_목록을_조회한다(user1AccessToken, spec);
        String lastChat = chatRoomListResponse.jsonPath().getString("[0].lastChat");
        assertThat(lastChat).isIn(user1Message, user2Message);

        // 테스트 종료 후 세션 연결 해제
        if (user1Session != null && user1Session.isConnected()) {
            user1Session.disconnect();
        }
        if (user2Session != null && user2Session.isConnected()) {
            user2Session.disconnect();
        }
    }

    @Test
    @DisplayName("사용자에게 채팅방이 있을 때, 채팅방 리스트를 반환한다.")
    void getMyChatRooms_returnsChatRoomList_whenChatRoomsExist() {
        // Given
        api_문서_타이틀("get_my_chat_rooms", spec);

        var createRoomResponse = 채팅방을_생성한다(user1AccessToken, user2Id, spec);
        Long chatRoomId = createRoomResponse.jsonPath().getLong("id");

        String messageContent = "Test message for chat room list";
        StompSession user1Session;
        try {
            user1Session = STOMP_연결을_하고_세션을_반환한다(port, user1AccessToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to establish STOMP connection", e);
        }
        try {
            채팅_메시지를_전송한다(user1Session, chatRoomId, messageContent);
            safeWait(500);
        } finally {
            if (user1Session != null && user1Session.isConnected()) {
                user1Session.disconnect();
            }
        }

        // When
        var chatRoomListResponse = 내_채팅방_목록을_조회한다(user2AccessToken, spec);

        // Then
        JsonPath chatRoomListPath = chatRoomListResponse.jsonPath();
        assertThat(chatRoomListPath.getList("$")).isNotNull();
        assertThat(chatRoomListPath.getLong("[0].roomId")).isEqualTo(chatRoomId);
        assertThat(chatRoomListPath.getString("[0].userImageUrl")).isNotEmpty();
        assertThat(chatRoomListPath.getString("[0].opponentName")).isEqualTo(user1Name);
        assertThat(chatRoomListPath.getString("[0].lastChat")).isEqualTo(messageContent);
        assertThat(chatRoomListPath.getBoolean("[0].isRead")).isNotNull();
    }

    @Test
    @DisplayName("사용자에게 채팅방이 없을 때, 빈 채팅방 리스트를 반환한다.")
    void getMyChatRooms_returnsEmptyChatRoomList_whenNoChatRoomsExist() {
        // Given
        api_문서_타이틀("get_my_chat_rooms_empty", spec);

        // When
        var chatRoomListResponse = 내_채팅방_목록을_조회한다(user2AccessToken, spec);

        // Then
        JsonPath jsonPath = chatRoomListResponse.jsonPath();
        List<?> chatRooms = jsonPath.getList("$");

        assertThat(chatRooms).hasSize(0);
    }
}