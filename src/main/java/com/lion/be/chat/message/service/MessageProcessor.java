package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.dto.ChatMessageRequest;
import com.lion.be.chat.message.domain.dto.ChatMessageResponse;

public interface MessageProcessor {

    /**
     * 수신된 채팅 메시지를 처리합니다.
     *
     * @param message 처리할 채팅 메시지 응답 DTO
     */
    void processIncomingMessage(ChatMessageRequest message, Long senderId);

    /**
     * 메시지 처리에 실패했을 때 수행할 작업을 정의합니다.
     *
     * @param message 처리 실패한 채팅 메시지 응답 DTO
     */
    void processFailedMessage(ChatMessageResponse message);
}
