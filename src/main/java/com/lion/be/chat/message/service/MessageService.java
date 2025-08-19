package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.chat.room.domain.entity.ChatRoom;
import com.lion.be.chat.room.domain.entity.ChatRoomUser;
import com.lion.be.chat.room.repository.ChatRoomUserRepository;
import com.lion.be.chat.room.service.ChatRoomPersistence;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessagePublisher messagePublisher;
    private final MessagePersistence messagePersistence;
    private final ChatRoomPersistence chatRoomPersistence;

    @Transactional
    public void publishMessage(ChatMessage message, ChatRoom chatRoom, ChatRoomUser chatRoomSender) {
        messagePublisher.publishMessage(message);

        ChatRoomUser receiverChatRoomUser = findOpponentChatRoomUser(message.getChatRoomId(), chatRoomSender.getUser());
        messagePersistence.updateMessageStatus(message, MessageStatus.PUBLISHED);
        chatRoomPersistence.updateChatRoomRecentMessage(chatRoom, message);
        chatRoomPersistence.updateChatRoomUserReadStatus(chatRoomSender, true);
        chatRoomPersistence.updateChatRoomUserReadStatus(receiverChatRoomUser, false);
    }

    public ChatRoomUser findOpponentChatRoomUser(Long chatRoomId, User sender) {
        return chatRoomUserRepository.findById_ChatRoomId(chatRoomId)
                .stream()
                .filter(cru -> !cru.getUser().getId().equals(sender.getId()))
                .findFirst().orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }
}
