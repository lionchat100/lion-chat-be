package com.lion.be.acceptance.chat;

import static com.lion.be.acceptance.auth.AuthSteps.비회원이_로그인한다;
import static com.lion.be.acceptance.auth.AuthSteps.원준이_로그인한다;
import static com.lion.be.acceptance.chat.ChatSteps.STOMP_연결을_하고_세션을_반환한다;
import static com.lion.be.acceptance.chat.ChatSteps.내_채팅방_목록을_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.수신된_메시지를_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅_메시지_내역을_조회한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅_메시지를_전송한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방_목록_응답을_검증한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방을_구독한다;
import static com.lion.be.acceptance.chat.ChatSteps.채팅방을_생성한다;
import static com.lion.be.acceptance.user.UserSteps.온보딩을_완료한다;
import static com.lion.be.acceptance.util.AuthFixture.비회원_로그인_요청;
import static com.lion.be.acceptance.util.AuthFixture.사용자_원준_로그인_요청;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사2_온보딩_요청;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사_온보딩_요청;
import static org.assertj.core.api.Assertions.assertThat;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.chat.domain.dto.ChatMessageDto;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import io.restassured.path.json.JsonPath;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;

@DisplayName("채팅 관련 기능 인수테스트")
public class ChatAcceptanceTest extends AcceptanceTest {

    private void safeWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting", e);
        }
    }

    // 구현체 대신 인터페이스를 주입받는 것이 좋습니다.
    @Autowired
    private UserRepository userRepository;

    private String user1AccessToken;
    private String user2AccessToken;
    private Long user1Id;
    private Long user2Id;
    private String user1Email;
    private String user1Name;
    private String user2Name;

    @BeforeEach
    void chatSetup() {
        var user1LoginResponse = 원준이_로그인한다(spec);
        user1AccessToken = user1LoginResponse.jsonPath().getString("accessToken");

        User user1 = userRepository.fetchByEmail(사용자_원준_로그인_요청().get("email").toString())
                .orElseThrow(() -> new IllegalStateException("테스트 사용자를 찾을 수 없습니다: " + 사용자_원준_로그인_요청().get("email")));
        user1Id = user1.getId();
        user1Name = user1.getName();
        user1Email = user1.getEmail(); // DB에서 직접 이름을 가져와 일관성 유지
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
    @DisplayName("사용자 간에 채팅방을 생성하고, 메시지를 주고받는다.")
    void fullChatFlowTest() throws Exception {
        // 1. 채팅방 생성 (사용자1 -> 사용자2)
        api_문서_타이틀("create_chat_room", spec);
        var createRoomResponse = 채팅방을_생성한다(user1AccessToken, user2Id, spec);
        Long chatRoomId = createRoomResponse.jsonPath().getLong("id");

        // WebSocket 연결 및 구독을 위한 준비
        StompSession user1Session = null;
        StompSession user2Session = null;
        // 사용자2가 메시지를 수신할 큐
        BlockingQueue<ChatMessageDto> messageQueue = new LinkedBlockingDeque<>();

        try {
            // 2. 두 사용자 모두 WebSocket에 연결
            try {
                user1Session = STOMP_연결을_하고_세션을_반환한다(port, user1AccessToken);
                user2Session = STOMP_연결을_하고_세션을_반환한다(port, user2AccessToken);
            } catch (Exception e) {
                throw new RuntimeException("Failed to establish STOMP connection", e);
            }

            // 3. 사용자2가 채팅방 토픽을 구독
            채팅방을_구독한다(user2Session, chatRoomId, messageQueue);

            // 잠시 대기하여 구독이 완료될 시간을 줌 (네트워크 지연 등 고려)
            safeWait(500);

            // 4. 사용자1이 메시지 전송
            String messageContent = "안녕하세요! 채팅 테스트입니다.";
            채팅_메시지를_전송한다(user1Session, chatRoomId, messageContent);

            // 5. 사용자2가 메시지를 정상적으로 수신했는지 검증
            수신된_메시지를_검증한다(messageQueue, user1Email, messageContent);

            // 6. 사용자2가 자신의 채팅방 목록을 조회했을 때, 마지막 메시지가 잘 반영되었는지 확인
            api_문서_타이틀("get_my_chat_rooms", spec);
            var chatRoomListResponse = 내_채팅방_목록을_조회한다(user2AccessToken, spec);
            채팅방_목록_응답을_검증한다(chatRoomListResponse, messageContent);

            // 7. 사용자1이 채팅 메시지 내역을 조회했을 때, 보낸 메시지가 포함되어 있는지 확인
            api_문서_타이틀("get_chat_messages", spec);
            var chatMessagesResponse = 채팅_메시지_내역을_조회한다(user1AccessToken, chatRoomId, spec);
            JsonPath messageHistory = chatMessagesResponse.jsonPath();
            assertThat(messageHistory.getList("$")).hasSizeGreaterThan(0);

            JsonPath chatRoomListPath = chatRoomListResponse.jsonPath();
            assertThat(chatRoomListPath.getLong("[0].roomId")).isEqualTo(chatRoomId);
            assertThat(chatRoomListPath.getString("[0].userImageUrl")).isNotEmpty();
            assertThat(chatRoomListPath.getString("[0].opponentName")).isEqualTo(user1Name);
            assertThat(chatRoomListPath.getString("[0].lastChat")).isEqualTo(messageContent);
            assertThat(chatRoomListPath.getBoolean("[0].isRead")).isNotNull();

        } finally {
            // 8. 테스트 종료 후 세션 연결 해제
            if (user1Session != null && user1Session.isConnected()) {
                user1Session.disconnect();
            }
            if (user2Session != null && user2Session.isConnected()) {
                user2Session.disconnect();
            }
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