package com.lion.be.chat.service;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessagePersistence {

    /**
     * 채팅 메시지를 저장합니다.
     *
     * @param message 저장할 채팅 메시지 엔티티
     * @return 저장된 채팅 메시지 엔티티
     */
    ChatMessage saveMessage(ChatMessage message);

    /**
     * 특정 메시지의 상태를 업데이트합니다.
     *
     * @param messageId 상태를 변경할 메시지의 ObjectId
     * @param status    변경할 메시지 상태
     */
    void updateMessageStatus(ObjectId messageId, MessageStatus status);

    /**
     * 특정 메시지를 읽음 처리합니다.
     *
     * @param messageId 읽음 처리할 메시지 ID (문자열 형식)
     * @param userId    읽음 처리하는 사용자 ID
     */
    void markAsRead(String messageId, Long userId);

    /**
     * 특정 사용자의 미수신 상태인 메시지를 조회합니다.
     *
     * @param userId 조회 대상 사용자 ID
     * @return 미처리 메시지 목록
     */
    List<ChatMessage> getPendingMessages(Long userId);

    /**
     * 채팅방의 메시지를 조회합니다.
     *
     * @param roomId   채팅방 ID
     * @param lastId   마지막으로 조회한 메시지 ID (0이면 최신부터)
     * @param pageable 페이징 정보
     * @return 메시지 목록
     */
    List<ChatMessageResponse> findMessagesByIdAndLastId(Long roomId, Long lastId, Pageable pageable);

}

