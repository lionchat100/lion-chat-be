package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.message.repository.MessageMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StompWebSocketMessenger implements WebSocketMessenger {

    private static final String CHAT_ROOM_PREFIX = "/topic/chatroom/";

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageMapper adapter;

    @Override
    public boolean deliverToClient(@NotNull Long userId, ChatMessageResponse message) {
        try {
            String destination = CHAT_ROOM_PREFIX + message.chatRoomId();
            messagingTemplate.convertAndSend(destination, message);
            log.info("WebSocket 메시지 전송 성공: userId={}, messageId={}", userId, message.messageId());
            return true;
        } catch (Exception e) {
            log.error("WebSocket 메시지 전송 실패: userId={}, messageId={}, error={}",
                    userId, message.messageId(), e.getMessage());
            return false;
        }
    }
}
