package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.message.repository.ChatMessageRepository;
import com.lion.be.chat.room.domain.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageWriteService {

    private final ChatMessageRepository chatMessageRepository;

    public void updateMessageStatus(ChatMessage message, MessageStatus status) {
        message.updateMessageStatus(status);
        chatMessageRepository.save(message);
        log.debug("메시지 상태를 {}로 변경합니다.", status);
    }
}
