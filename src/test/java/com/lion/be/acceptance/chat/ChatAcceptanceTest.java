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

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;

@DisplayName("채팅 관련 기능 인수테스트")
public class ChatAcceptanceTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    private void safeWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting", e);
        }
    }

    @Test
    @DisplayName("사용자 간에 채팅방을 생성하고, 메시지를 주고받은 후, 채팅 목록과 메시지 내역을 조회한다.")
    void fullChatFlowTest() throws Exception {
        // Given: 이 테스트에 필요한 사용자들을 생성하고 온보딩합니다.
        var user1LoginResponse = 원준이_로그인한다(spec);
        String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사_온보딩_요청(), user1AccessToken, spec);
        User user1 = userRepository.fetchByEmail(사용자_원준_로그인_요청().get("email").toString())
                .orElseThrow(() -> new IllegalStateException("테스트 사용자를 찾을 수 없습니다."));

        var user2LoginResponse = 비회원이_로그인한다(spec);
        String user2AccessToken = user2LoginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), user2AccessToken, spec);
        User user2 = userRepository.fetchByEmail(비회원_로그인_요청().get("email").toString())
                .orElseThrow(() -> new IllegalStateException("테스트 사용자를 찾을 수 없습니다."));

        // When & Then
        // 1. 채팅방 생성
        api_문서_타이틀("create_chat_room", spec);
        var createRoomResponse = 채팅방을_생성한다(user1AccessToken, user2.getId(), spec);
        Long chatRoomId = createRoomResponse.jsonPath().getLong("id");

        // 2. WebSocket 연결
        StompSession user1Session = STOMP_연결을_하고_세션을_반환한다(port, user1AccessToken);

        // 3. 메시지 전송
        String messageContent = "안녕하세요! 채팅 테스트입니다.";
        채팅_메시지를_전송한다(user1Session, chatRoomId, messageContent);

        // 4. 채팅방 목록 조회 및 검증 (비동기 처리 대기)
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var chatRoomListResponse = 내_채팅방_목록을_조회한다(user2AccessToken, spec);
            채팅방_목록_응답을_검증한다(chatRoomListResponse, messageContent);
        });
        api_문서_타이틀("get_my_chat_rooms", spec);
        내_채팅방_목록을_조회한다(user2AccessToken, spec); // 문서화를 위한 추가 호출

        // 5. 메시지 내역 조회 및 검증 (비동기 처리 대기)
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var chatMessagesResponse = 채팅_메시지_내역을_조회한다(user1AccessToken, chatRoomId, spec);
            JsonPath messageHistory = chatMessagesResponse.jsonPath();

            assertThat(messageHistory.getList("$")).hasSize(1);
            assertThat(messageHistory.getString("[0].content")).isEqualTo(messageContent);
            assertThat(messageHistory.getString("[0].senderName")).isEqualTo(user1.getName());
            assertThat(messageHistory.getLong("[0].senderId")).isEqualTo(user1.getId());
        });
        api_문서_타이틀("get_chat_messages", spec);
        채팅_메시지_내역을_조회한다(user1AccessToken, chatRoomId, spec); // 문서화를 위한 추가 호출

        // 6. 세션 종료
        if (user1Session != null && user1Session.isConnected()) {
            user1Session.disconnect();
        }
    }

    @Test
    @DisplayName("두 사용자가 동시에 메시지를 보내도 데이터 정합성이 유지된다.")
    void concurrencyMessageSendTest() throws Exception {
        // Given: 이 테스트에 필요한 사용자들을 생성하고 온보딩합니다.
        var user1LoginResponse = 원준이_로그인한다(spec);
        String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사_온보딩_요청(), user1AccessToken, spec);

        var user2LoginResponse = 비회원이_로그인한다(spec);
        String user2AccessToken = user2LoginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), user2AccessToken, spec);
        User user2 = userRepository.fetchByEmail(비회원_로그인_요청().get("email").toString())
                .orElseThrow(() -> new IllegalStateException("테스트 사용자를 찾을 수 없습니다."));

        // 채팅방 생성
        var createRoomResponse = 채팅방을_생성한다(user1AccessToken, user2.getId(), spec);
        Long chatRoomId = createRoomResponse.jsonPath().getLong("id");

        // WebSocket 연결
        StompSession user1Session = STOMP_연결을_하고_세션을_반환한다(port, user1AccessToken);
        StompSession user2Session = STOMP_연결을_하고_세션을_반환한다(port, user2AccessToken);

        // When: 두 사용자가 동시에 메시지를 전송합니다.
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        String user1Message = "사용자1의 동시성 테스트 메시지";
        String user2Message = "사용자2의 동시성 테스트 메시지";

        executorService.submit(() -> {
            try {
                latch.countDown();
                latch.await();
                채팅_메시지를_전송한다(user1Session, chatRoomId, user1Message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executorService.submit(() -> {
            try {
                latch.countDown();
                latch.await();
                채팅_메시지를_전송한다(user2Session, chatRoomId, user2Message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executorService.shutdown();

        // Then: 모든 메시지가 정상적으로 저장되고 조회됩니다.
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var chatMessagesResponse = 채팅_메시지_내역을_조회한다(user1AccessToken, chatRoomId, spec);
            JsonPath messageHistory = chatMessagesResponse.jsonPath();

            assertThat(messageHistory.getList("$").size()).isEqualTo(2);
            assertThat(messageHistory.getList("content", String.class)).contains(user1Message, user2Message);
        });

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
    void getMyChatRooms_returnsChatRoomList_whenChatRoomsExist() throws Exception {
        // Given: 이 테스트에 필요한 사용자들을 생성하고 채팅방과 메시지를 만듭니다.
        var user1LoginResponse = 원준이_로그인한다(spec);
        String user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사_온보딩_요청(), user1AccessToken, spec);
        User user1 = userRepository.fetchByEmail(사용자_원준_로그인_요청().get("email").toString()).get();

        var user2LoginResponse = 비회원이_로그인한다(spec);
        String user2AccessToken = user2LoginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), user2AccessToken, spec);
        User user2 = userRepository.fetchByEmail(비회원_로그인_요청().get("email").toString()).get();

        var createRoomResponse = 채팅방을_생성한다(user1AccessToken, user2.getId(), spec);
        Long chatRoomId = createRoomResponse.jsonPath().getLong("id");

        String messageContent = "Test message for chat room list";
        StompSession user1Session = STOMP_연결을_하고_세션을_반환한다(port, user1AccessToken);
        채팅_메시지를_전송한다(user1Session, chatRoomId, messageContent);
        safeWait(500); // 메시지 처리 대기
        user1Session.disconnect();

        // When: 다른 사용자가 자신의 채팅방 목록을 조회합니다.
        api_문서_타이틀("get_my_chat_rooms", spec);
        var chatRoomListResponse = 내_채팅방_목록을_조회한다(user2AccessToken, spec);

        // Then: 응답을 검증합니다.
        JsonPath chatRoomListPath = chatRoomListResponse.jsonPath();
        assertThat(chatRoomListPath.getList("$")).isNotNull();
        assertThat(chatRoomListPath.getLong("[0].roomId")).isEqualTo(chatRoomId);
        assertThat(chatRoomListPath.getString("[0].userImageUrl")).isNotEmpty();
        assertThat(chatRoomListPath.getString("[0].opponentName")).isEqualTo(user1.getName());
        assertThat(chatRoomListPath.getString("[0].lastChat")).isEqualTo(messageContent);
        assertThat(chatRoomListPath.getBoolean("[0].isRead")).isNotNull();
    }

    @Test
    @DisplayName("사용자에게 채팅방이 없을 때, 빈 채팅방 리스트를 반환한다.")
    void getMyChatRooms_returnsEmptyChatRoomList_whenNoChatRoomsExist() {
        // Given: 이 테스트에 필요한 사용자를 생성하고 온보딩합니다.
        var userLoginResponse = 비회원이_로그인한다(spec);
        String userAccessToken = userLoginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), userAccessToken, spec);

        // When: 채팅방 생성 없이 목록을 조회합니다.
        api_문서_타이틀("get_my_chat_rooms_empty", spec);
        var chatRoomListResponse = 내_채팅방_목록을_조회한다(userAccessToken, spec);

        // Then: 빈 목록이 반환됩니다.
        JsonPath jsonPath = chatRoomListResponse.jsonPath();
        List<?> chatRooms = jsonPath.getList("$");
        assertThat(chatRooms).isEmpty();
    }
}