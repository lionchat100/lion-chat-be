package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.entity.ChatMessage;

public interface MessagePublisher {

    /**
     * 채팅 메시지를 외부 메시지 브로커 등으로 발행합니다.
     *
     * @param message 발행할 채팅 메시지 엔티티
     */
    void publishMessage(ChatMessage message);

    /**
     * 메시지 발행과 관련된 콜백 메서드를 설정합니다.
     */
    void setUpCallbacks();
}
