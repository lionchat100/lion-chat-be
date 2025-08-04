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
        var createRoomResponse = 채팅방을_생성한다(user1AccessToken, user2Id, spec);
        Long chatRoomId = createRoomResponse.jsonPath().getLong("id");

        // WebSocket 연결
        StompSession user1Session = STOMP_연결을_하고_세션을_반환한다(port, user1AccessToken);

        // 4. 사용자1이 메시지 전송
        String messageContent = "안녕하세요! 채팅 테스트입니다.";
        채팅_메시지를_전송한다(user1Session, chatRoomId, messageContent);

        // 6. [검증 1] 사용자2의 채팅방 목록 API를 반복적으로 호출하여,
        // 마지막 메시지가 업데이트될 때까지 최대 10초간 기다린다.
        api_문서_타이틀("get_my_chat_rooms", spec);
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var chatRoomListResponse = 내_채팅방_목록을_조회한다(user2AccessToken, spec);

            // 검증 로직을 이 안에 넣는다.
            채팅방_목록_응답을_검증한다(chatRoomListResponse, messageContent);
        });

        // 7. [검증 2] 사용자1의 채팅 메시지 내역 API를 호출하여,
        // 보낸 메시지가 조회될 때까지 최대 10초간 기다린다.
        api_문서_타이틀("get_chat_messages", spec);
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var chatMessagesResponse = 채팅_메시지_내역을_조회한다(user1AccessToken, chatRoomId, spec);
            JsonPath messageHistory = chatMessagesResponse.jsonPath();

            assertThat(messageHistory.getList("$")).hasSize(1);
            assertThat(messageHistory.getString("[0].content")).isEqualTo(messageContent);
            assertThat(messageHistory.getString("[0].senderName")).isEqualTo(user1Name);
            assertThat(messageHistory.getLong("[0].senderId")).isEqualTo(user1Id);
        });

        // 세션 연결 해제
        if (user1Session != null && user1Session.isConnected()) {
            user1Session.disconnect();
        }
    }

}