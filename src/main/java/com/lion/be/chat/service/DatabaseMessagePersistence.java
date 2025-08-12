package com.lion.be.chat.service;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.domain.entity.ChatRoom;
import com.lion.be.chat.repository.ChatMessageRepository;
import com.lion.be.chat.repository.ChatRoomRepository;
import com.lion.be.user.repository.UserRepository;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatabaseMessagePersistence implements MessagePersistence {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 채팅 메시지를 DB에 저장
     *
     * @param message 저장할 채팅 메시지(도메인 객체)
     * @return 저장된 채팅 메시지(도메인 객체, ID 및 저장 시각이 반영됨)
     *
     * 과정:
     * 1. 도메인 객체를 DB 엔티티로 변환
     * 2. DB에 저장
     * 3. 저장된 엔티티를 다시 도메인 객체로 변환하여 반환
     */
    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        ChatMessage saved = chatMessageRepository.save(message);

        chatRoomRepository.findById(message.getChatRoomId())
                .ifPresentOrElse(
                        room -> {
                            room.updateRecentMessage(message.getContent(), ZonedDateTime.now());
                            room.markAsRead();
                            chatRoomRepository.save(room);
                        }, () -> log.warn("채팅방을 찾을 수 없습니다. ChatRoomId: {}", message.getChatRoomId())
                );

        return saved;
    }

    /**
     * 특정 메시지의 상태(Status)를 업데이트
     *
     * @param messageId 메시지의 ObjectId
     * @param status    변경할 상태 (예: PENDING → SENT)
     *                  <p>
     *                  과정:
     *                  1. ID로 메시지 조회
     *                  2. 조회 결과가 있으면 기존 엔티티 데이터를 복사하되 상태만 변경
     *                  3. 변경된 엔티티를 DB에 다시 저장
     */
    @Override
    public void updateMessageStatus(ObjectId messageId, MessageStatus status) {
        chatMessageRepository.findById(messageId).ifPresentOrElse(
                entity -> {
                    entity.updateStatus(status);
                    log.debug("메시지 상태 업데이트: messageId={}, status={}", messageId, status);
                },
                () -> log.warn("메시지를 찾을 수 없습니다. MessageId: {}", messageId)
        );
    }

    /**
     * 특정 메시지를 '읽음' 상태로 표시
     *
     * @param messageId 메시지 ID
     * @param userId    읽은 사용자 ID
     *                  <p>
     *                  조건:
     *                  - 보낸 사람(senderId)와 읽는 사람이 동일한 경우에만 '읽음' 처리
     *                  <p>
     *                  과정:
     *                  1. 메시지를 ID로 조회
     *                  2. 보낸 사람과 userId가 같으면 isRead=true로 설정하여 저장
     */
    @Override
    public void markAsRead(String messageId, Long userId) {
        chatMessageRepository.findById(new ObjectId(messageId)).ifPresentOrElse(
                entity -> {
                    if (!entity.getSenderId().equals(userId)) {
                        ChatMessage updatedMessage = new ChatMessage();
                        updatedMessage.markAsRead(
                                entity.getId()
                        );
                        chatMessageRepository.save(updatedMessage);
                        log.info("메시지 읽음 처리 완료: messageId={}, userId={}", messageId, userId);
                    } else {
                        log.debug("발신자는 자신의 메시지를 읽음 처리할 수 없습니다: messageId={}, userId={}", messageId, userId);
                    }
                },
                () -> log.warn("메시지를 찾을 수 없습니다. MessageId: {}", messageId)
        );
    }

    /**
     * 특정 사용자의 'PENDING' 상태 메시지 목록 조회
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 보낸 PENDING 상태의 메시지 리스트(도메인 객체)
     *
     * 과정:
     * 1. senderId와 상태로 메시지 필터링 조회
     * 2. 엔티티를 도메인 객체로 변환
     * 3. 변환된 리스트 반환
     */
    @Override
    public List<ChatMessage> getPendingMessages(Long userId) {
        return chatMessageRepository.findPendingMessageByReceiverId(userId);
    }

    @Override
    public Page<ChatMessageResponse> findMessagesByIdAndLastId(Long roomId, Long lastId, Pageable pageable) {
        chatRoomRepository.findById(roomId).ifPresent(
                chatRoom -> chatRoomRepository.save(new ChatRoom(
                        chatRoom.getId(),
                        chatRoom.getIsDeleted(),
                        chatRoom.getRegDt(),
                        chatRoom.getChatRoomUsers(),
                        chatRoom.getRecentMessageContent(),
                        chatRoom.getRecentMessageDt(),
                        true
                ))
        );

        Page<ChatMessage> messages = chatMessageRepository.findMessagesByIdAndLastId(roomId, lastId, pageable);
        boolean isEnd = !messages.hasNext();

        return messages.map(message ->
                new ChatMessageResponse(
                        message.getId().toString(),
                        message.getChatRoomId(),
                        message.getSenderId(),
                        message.getSenderName(),
                        userRepository.findById(message.getSenderId()).getImageUrl(), // FIXME: N+1 문제
                        message.getCreatedAt(),
                        message.getContent(),
                        isEnd
                )
        );
    }

}
