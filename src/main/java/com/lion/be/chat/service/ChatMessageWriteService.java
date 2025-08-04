package com.lion.be.chat.service;

import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.repository.ChatMessageRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageWriteService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    // void -> ChatMessage 반환 타입 변경
    public ChatMessage saveMessage(Long chatRoomId, String senderName, Long senderId, String content,
                                   LocalDateTime date) {
        ChatMessage chatMessage = new ChatMessage(senderId, senderName, chatRoomId, date.toInstant(ZoneOffset.UTC),
                content, false);
        return chatMessageRepository.save(chatMessage); // 저장된 객체를 반환
    }

    @Transactional
    public void markAsRead(Long chatRoomId, Long receiverId, Instant currentDt) {
        chatMessageRepository.updateReadStatus(chatRoomId, receiverId, currentDt);
    }

}
