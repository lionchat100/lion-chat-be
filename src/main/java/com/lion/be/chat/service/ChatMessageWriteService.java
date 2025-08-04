package com.lion.be.chat.service;

import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
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
