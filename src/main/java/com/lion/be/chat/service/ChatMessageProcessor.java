package com.lion.be.chat.service;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatRoomUser;
import com.lion.be.chat.repository.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageProcessor implements MessageProcessor {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageDelivery messageDelivery;
    private final MessagePersistence messagePersistence;

    /**
     * 수신한 채팅 메시지를 처리합니다.
     * <br>
     * 1. 메시지 대상자(수신자) 결정
     * <br>
     * 2. 대상자에게 메시지 전달 시도
     * <br>
     * 3. 성공 시 상태를 DELIVERED로, 실패 시 PENDING으로 갱신
     *
     * @param message 수신된 채팅 메시지 응답 DTO
     */
    @Override
    public void processIncomingMessage(ChatMessageResponse message) {
        Long targetUserId = determineTargetUser(message);

        boolean delivered = messageDelivery.deliverToClient(targetUserId, message);

        if (delivered) {
            messagePersistence.updateMessageStatus(new ObjectId(message.messageId()), MessageStatus.DELIVERED);
        } else {
            messagePersistence.updateMessageStatus(new ObjectId(message.messageId()), MessageStatus.PENDING);
        }
    }

    /**
     * 메시지 처리 실패 시 PENDING 처리된 메시지들을 재전송 대기 상태로 유지시킵니다.
     *
     * @param message 처리 실패한 채팅 메시지 응답 DTO
     */
    @Override
    public void processFailedMessage(ChatMessageResponse message) {
        log.warn("메시지 처리 실패: {}", message);
        messagePersistence.updateMessageStatus(new ObjectId(message.messageId()), MessageStatus.PENDING);
    }

    private Long determineTargetUser(ChatMessageResponse message) {
        Set<ChatRoomUser> userSet = chatRoomUserRepository.findById_ChatRoomId(message.chatRoomId());
        Long senderId = message.senderId();
        return userSet.stream()
                .filter(user -> !user.getId().getUserId().equals(senderId))
                .findFirst()
                .map(user -> user.getId().getUserId())
                .orElse(null);
    }
}
