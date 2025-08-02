package com.lion.be.chat.domain.chatmessage.service;

import com.lion.be.chat.domain.ChatMessageValue;
import com.lion.be.chat.domain.chatmessage.entity.ChatMessage;
import com.lion.be.chat.domain.chatmessage.repository.ChatMessageRepository;
import com.lion.be.chat.domain.chatroom.repository.ChatRoomRepository;
import com.lion.be.chat.domain.chatroomuser.repository.ChatRoomUserRepository;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ChatMessageWriteService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void saveMessage(Long chatRoomId, String senderName, Long senderId, String content, LocalDateTime date) {
        chatMessageRepository.save(
                new ChatMessage(senderId, senderName, chatRoomId, date.toInstant(ZoneOffset.UTC), content, false)
        );
    }

    @Transactional
    public void markAsRead(Long chatRoomId, Long receiverId, Instant currentDt) {
        chatMessageRepository.updateReadStatus(chatRoomId, receiverId, currentDt);
    }
}
