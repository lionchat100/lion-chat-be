package com.lion.be.acceptance.userlikes;

import static com.lion.be.acceptance.usercard.UserCardSteps.김백엔드_로그인;
import static com.lion.be.acceptance.usercard.UserCardSteps.김프론트_로그인;
import static com.lion.be.acceptance.usercard.UserCardSteps.이리액트_로그인;
import static com.lion.be.acceptance.usercard.UserCardSteps.카드_리스트_조회_성공을_검증한다;
import static com.lion.be.acceptance.usercard.UserCardSteps.카드리스트를_조회한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.사용자_카드_정보_완성도를_검증한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.인증없이_좋아요_목록을_조회한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.인증없이_좋아요를_누른다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요_목록_조회_성공을_검증한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요_목록의_카드_정보를_검증한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요_상태가_false임을_검증한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요_상태가_true임을_검증한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요_성공을_검증한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요를_누른다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요한_사용자_목록을_조회한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요한_사용자가_포함됨을_검증한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.notification.controller.dto.NotificationResponse;
import com.lion.be.notification.domain.NotificationType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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

@DisplayName("유저 좋아요 시스템 인수테스트")
class UserLikesAcceptanceTest extends AcceptanceTest {

    @Nested
    @DisplayName("좋아요 토글 기능")
    class LikeToggleTest {

        @DisplayName("사용자가 다른 사용자에게 좋아요를 누르면 좋아요가 생성된다")
        @Test
        void when_user_likes_another_user_then_like_is_created() {
            api_문서_타이틀("create_like", spec);

            // given - 김프론트가 로그인
            String accessToken = 김프론트_로그인();
            Long targetUserId = 2L; // 이리액트

            // when - 좋아요 누르기
            ExtractableResponse<Response> response = 좋아요를_누른다(spec, accessToken, targetUserId);

            // then
            좋아요_성공을_검증한다(response);
            좋아요_상태가_true임을_검증한다(response);
        }

        @DisplayName("이미 좋아요한 사용자에게 다시 좋아요를 누르면 좋아요가 취소된다")
        @Test
        void when_user_unlikes_already_liked_user_then_like_is_removed() throws IOException {
            api_문서_타이틀("toggle_like", spec);

            // given
            String accessToken = 원준_완전_온보딩();
            Long targetUserId = 3L; // 박뷰js

            좋아요를_누른다(spec, accessToken, targetUserId); // 첫 번째 좋아요

            // when - 다시 좋아요 누르기 (토글)
            ExtractableResponse<Response> response = 좋아요를_누른다(spec, accessToken, targetUserId);

            // then
            좋아요_성공을_검증한다(response);
            좋아요_상태가_false임을_검증한다(response);
        }

        @DisplayName("사용자가 여러 명에게 연속으로 좋아요를 누를 수 있다")
        @Test
        void when_user_likes_multiple_users_then_all_likes_are_created() {
            api_문서_타이틀("multiple_likes", spec);

            // given - 김백엔드가 로그인
            String accessToken = 김백엔드_로그인();
            List<Long> targetUserIds = List.of(2L, 3L, 4L, 5L);

            // when - 여러 사용자에게 좋아요 누르기
            for (Long targetUserId : targetUserIds) {
                ExtractableResponse<Response> response = 좋아요를_누른다(spec, accessToken, targetUserId);
                좋아요_성공을_검증한다(response);
                좋아요_상태가_true임을_검증한다(response);
            }

            // then - 좋아요한 사용자 목록 확인
            ExtractableResponse<Response> likedUsersResponse = 좋아요한_사용자_목록을_조회한다(spec, accessToken);
            좋아요_목록_조회_성공을_검증한다(likedUsersResponse);
            좋아요한_사용자가_포함됨을_검증한다(likedUsersResponse, targetUserIds);
        }

    }

    @Nested
    @DisplayName("좋아요한 사용자 목록 조회")
    class LikedUsersListTest {

        @DisplayName("사용자가 좋아요한 사용자들의 목록을 조회할 수 있다")
        @Test
        void when_user_requests_liked_users_then_returns_liked_users_list() {
            api_문서_타이틀("get_liked_users_list", spec);

            // given - 이리액트가 로그인하고 여러 사용자에게 좋아요
            String accessToken = 이리액트_로그인();
            List<Long> targetUserIds = List.of(6L, 7L, 8L); // 김백엔드, 이스프링, 박자바

            for (Long targetUserId : targetUserIds) {
                좋아요를_누른다(spec, accessToken, targetUserId);
            }

            // when - 좋아요한 사용자 목록 조회
            ExtractableResponse<Response> response = 좋아요한_사용자_목록을_조회한다(spec, accessToken);

            // then
            좋아요_목록_조회_성공을_검증한다(response);
            좋아요한_사용자가_포함됨을_검증한다(response, targetUserIds);
            좋아요_목록의_카드_정보를_검증한다(response);
        }

        @DisplayName("좋아요하지 않은 사용자는 빈 목록을 반환한다")
        @Test
        void when_user_has_no_likes_then_returns_empty_list() {
            api_문서_타이틀("empty_liked_users_list", spec);

            // given - 새로운 사용자가 로그인 (아무도 좋아요하지 않음)
            String accessToken = 김프론트_로그인();

            // when - 좋아요한 사용자 목록 조회
            ExtractableResponse<Response> response = 좋아요한_사용자_목록을_조회한다(spec, accessToken);

            // then
            좋아요_목록_조회_성공을_검증한다(response);
            List<Map<String, Object>> likedUsers = response.jsonPath().getList("$");
            assertThat(likedUsers).isEmpty();
        }

        @DisplayName("좋아요 목록에서 사용자 정보가 올바르게 반환된다")
        @Test
        void when_user_requests_liked_users_then_returns_complete_user_info() throws IOException {
            api_문서_타이틀("liked_users_info_validation", spec);

            // given - 사용자가 로그인하고 좋아요
            String accessToken = 원준_완전_온보딩();    // 유저 아이디 1
            토킷_완전_온보딩();                        // 유저 아이디가 2
            Long targetUserId = 2L;

            좋아요를_누른다(spec, accessToken, targetUserId);

            // when - 좋아요한 사용자 목록 조회
            ExtractableResponse<Response> response = 좋아요한_사용자_목록을_조회한다(spec, accessToken);

            // then
            좋아요_목록_조회_성공을_검증한다(response);
            사용자_카드_정보_완성도를_검증한다(response);
        }

    }

    @Nested
    @DisplayName("카드 조회 시 좋아요 상태 반영")
    class LikeStatusInCardTest {

        @DisplayName("카드 조회 시 좋아요하지 않은 사용자는 isLikedByMe가 false로 표시된다")
        @Test
        void when_user_views_cards_then_not_liked_users_show_false_status() throws IOException {
            api_문서_타이틀("card_like_status_false", spec);

            // given - 토킷가 로그인 (아무도 좋아요하지 않음)
            String accessToken = 토킷_완전_온보딩();

            // when - 카드 목록 조회
            ExtractableResponse<Response> response = 카드리스트를_조회한다(spec, accessToken, 5);

            // then
            카드_리스트_조회_성공을_검증한다(response);

            // 모든 카드의 isLikedByMe가 false인지 확인
            List<Map<String, Object>> cards = response.jsonPath().getList("$");
            for (Map<String, Object> card : cards) {
                assertThat((Boolean) card.get("isLikedByMe")).isFalse();
            }
        }

    }

    @Nested
    @DisplayName("인증 및 권한 검증")
    class AuthenticationTest {

        @DisplayName("인증되지 않은 사용자가 좋아요를 누르면 401 에러를 반환한다")
        @Test
        void when_unauthenticated_user_tries_to_like_then_returns_401() {
            api_문서_타이틀("unauthorized_like_attempt", spec);

            // when - 인증 없이 좋아요 시도
            ExtractableResponse<Response> response = 인증없이_좋아요를_누른다(spec, 2L);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        }

        @DisplayName("인증되지 않은 사용자가 좋아요 목록을 조회하면 401 에러를 반환한다")
        @Test
        void when_unauthenticated_user_tries_to_get_liked_users_then_returns_401() {
            api_문서_타이틀("unauthorized_liked_users_access", spec);

            // when - 인증 없이 좋아요 목록 조회 시도
            ExtractableResponse<Response> response = 인증없이_좋아요_목록을_조회한다(spec);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        }

        @DisplayName("자기 자신에게는 좋아요를 누를 수 없다 (비즈니스 로직 검증)")
        @Test
        void when_user_tries_to_like_self_then_handles_appropriately() throws IOException {
            api_문서_타이틀("self_like_prevention", spec);

            // given - 원준 로그인 (userId: 1)
            String accessToken = 원준_완전_온보딩();

            // when - 자기 자신에게 좋아요 시도
            ExtractableResponse<Response> response = 좋아요를_누른다(spec, accessToken, 1L);

            // then - 400 Bad Request 에러 반환
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

    }

    @Nested
    @DisplayName("실시간 알림 통합 (이벤트 기반)")
    class NotificationIntegrationTest {

        private WebSocketStompClient stompClient;
        private BlockingQueue<NotificationResponse> notificationQueue;

        @BeforeEach
        void setUp() {
            notificationQueue = new ArrayBlockingQueue<>(1);
            stompClient = new WebSocketStompClient(new SockJsClient(
                    List.of(new WebSocketTransport(new StandardWebSocketClient()))));
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        }

        @DisplayName("사용자가 좋아요를 누르면 상대방은 실시간 알림을 수신한다")
        @Test
        void when_user_likes_another_user_then_notification_is_received() throws Exception {
            api_문서_타이틀("like_and_receive_notification", spec);

            // given
            String likerAccessToken = 원준_완전_온보딩();
            String likedUserAccessToken = 토킷_완전_온보딩();
            Long likedUserId = 2L;

            StompHeaders connectHeaders = new StompHeaders();
            connectHeaders.set("Authorization", "Bearer " + likedUserAccessToken);
            String wsUrl = "ws://localhost:" + port + "/ws";

            CountDownLatch subscribeLatch = new CountDownLatch(1);

            StompSession session = stompClient.connectAsync(wsUrl, new WebSocketHttpHeaders(), connectHeaders,
                            new StompSessionHandlerAdapter() {
                            })
                    .get(5, TimeUnit.SECONDS);

            // ⭐️ [핵심 수정] ⭐️
            // 1. 구독 시 사용할 별도의 StompHeaders 객체를 생성합니다.
            StompHeaders subscribeHeaders = new StompHeaders();
            subscribeHeaders.setDestination("/user/queue/notifications");
            // 2. 이 구독 요청에 대한 영수증(receipt)을 요청하는 헤더를 직접 추가합니다. ID는 아무 문자열이나 가능합니다.
            subscribeHeaders.setReceipt("subscribe-receipt");

            // 3. subscribe 메서드에 헤더 객체를 전달합니다.
            session.subscribe(subscribeHeaders, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return NotificationResponse.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    notificationQueue.offer((NotificationResponse) payload);
                }
            }).addReceiptTask(() -> subscribeLatch.countDown()); // 이제 이 코드가 정상 동작합니다.

            if (!subscribeLatch.await(5, TimeUnit.SECONDS)) {
                fail("STOMP 구독이 5초 내에 완료되지 않았습니다.");
            }

            // when: 구독이 보장된 후에 좋아요 API 호출
            좋아요를_누른다(spec, likerAccessToken, likedUserId);

            // then: 알림 메시지 수신 및 검증
            NotificationResponse receivedNotification = notificationQueue.poll(5, TimeUnit.SECONDS);

            assertThat(receivedNotification).as("5초 내에 알림을 수신해야 합니다.").isNotNull();
            assertThat(receivedNotification.getContent()).contains("님이 회원님을 좋아합니다");
            assertThat(receivedNotification.getNotificationType()).isEqualTo(NotificationType.LIKE);
            assertThat(receivedNotification.getRelatedUrl()).isEqualTo("/profile/1");
            assertThat(receivedNotification.isRead()).isFalse();

            session.disconnect();
        }

    }


}
