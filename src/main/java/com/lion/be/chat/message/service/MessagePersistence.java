package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.message.repository.ChatMessageRepository;
import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.chat.room.domain.entity.ChatRoomUser;
import com.lion.be.chat.room.repository.ChatRoomUserRepository;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePersistence {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public void updateMessageStatus(ChatMessage message, MessageStatus status) {
        message.updateMessageStatus(status);
        chatMessageRepository.save(message);
        log.debug("메시지 상태를 {}로 변경합니다.", status);
    }
}
