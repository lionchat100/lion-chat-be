package com.lion.be.chat.controller;

import com.lion.be.chat.domain.dto.MessageAckRequest;
import com.lion.be.chat.service.MessageDelivery;
import com.lion.be.chat.service.MessagePersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@RestController
@RequestMapping("/api/ws")
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final MessagePersistence messagePersistence;
    private final MessageDelivery messageDelivery;

    /**
     * 클라이언트로부터 메시지 읽음 확인을 요청합니다.
     * <br>
     * 클라이언트가 특정 메시지를 읽었음을 서버에 알리면, 메시지를 SENT로 갱신합니다.
     *
     * @param ackRequest 읽음 확인 요청 DTO (메시지 ID, 사용자 ID 포함)
     */
    @MessageMapping("/message.ack")
    public void handleMessageAck(MessageAckRequest ackRequest) {
        log.info("메시지 읽음 확인 요청 수신: {}", ackRequest);
        messagePersistence.markAsRead(ackRequest.messageId(), ackRequest.userId());
        log.info("메시지 읽음 상태 업데이트 완료: messageId={}, userId={}", ackRequest.messageId(), ackRequest.userId());
    }

    /**
     * WebSocket 세션 연결 이벤트 리스너입니다.
     * <br>
     * 사용자가 웹소켓에 연결할 때 발생하며, 연결된 사용자 ID를 통해 미수신 메시지를 찾아 클라이언트로 일괄 전달합니다.
     *
     * @param event 웹소켓 세션 연결 이벤트
     */
    @EventListener
    public void handleWebSocketConnectEvent(SessionConnectEvent event) {
        Long userId = extractUserIdFromSession(event.getMessage());
        if (userId != null) {
            log.info("사용자 웹소켓 연결: {}", userId);
            messageDelivery.deliverPendingMessages(userId);
            log.info("미수신 메시지 전송 완료: userId={}", userId);
        } else {
            log.error("웹소켓 연결 실패: 사용자 ID 추출 불가");
        }
    }

    /**
     * STOMP 메시지에서 사용자 ID를 추출합니다.
     *
     * @param message STOMP 메시지 객체
     * @return 사용자 ID (Long) 또는 파싱 실패 시 null 반환
     */
    private Long extractUserIdFromSession(Message<?> message) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            return Long.parseLong(accessor.getUser().getName());
    }
}
