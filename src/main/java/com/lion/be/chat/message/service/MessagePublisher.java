package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.entity.ChatMessage;

public interface MessagePublisher {

    /**
     * 채팅 메시지 브로커에 발행
     *
     * @param message
     */
    void publishMessage(ChatMessage message);
}
