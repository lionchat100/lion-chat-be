package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.dto.ChatMessageResponse;

public interface MessageDelivery {

    /**
     * 특정 사용자에게 단일 메시지를 전송합니다.
     *
     * @param userId  대상 사용자 ID
     * @param message 전송할 채팅 메시지 응답 DTO
     * @return 메시지 전송 성공 시 true, 실패 시 false 반환
     */
    boolean deliverToClient(Long userId, ChatMessageResponse message);

    /**
     * 특정 사용자의 미수신 메시지들을 모두 전송합니다.
     *
     * @param userId 대상 사용자 ID
     */
    void deliverPendingMessages(Long userId);
}
