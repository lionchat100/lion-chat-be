package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.dto.ChatMessageRequest;
import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.message.repository.ChatMessageRepository;
import com.lion.be.chat.message.repository.MessageMapper;
import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.chat.room.domain.entity.ChatRoom;
import com.lion.be.chat.room.domain.entity.ChatRoomUser;
import com.lion.be.chat.room.repository.ChatRoomRepository;
import com.lion.be.chat.room.repository.ChatRoomUserRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final UserJpaRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageMapper mapper;
    private final MessageBroker messageBroker;

    public void sendMessage(ChatMessageRequest request, Long senderId) {
        log.info("메시지 요청 들어옴: {}, senderId: {}", request, senderId);

        ChatMessage message = mapper.fromRequest(request, senderId);
        message.updateMessageStatus(MessageStatus.PENDING);
        chatMessageRepository.save(message);
        log.info("메시지 저장됨: {}", message);

        message.updateMessageStatus(MessageStatus.PUBLISHED);
        messageBroker.publishMessage(message);
        chatMessageRepository.save(message);
        log.info("메시지 발행 완료: {}", message);

        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId()).get();
        chatRoom.updateRecentMessage(message.getContent(), message.getCreatedAt());
        log.info("채팅방 마지막 내용, 시간 업데이트됨: {}번 방", chatRoom.getId());

        ChatRoomUser chatRoomUser = chatRoomUserRepository.findById_ChatRoomIdAndId_UserId(message.getChatRoomId(), senderId);
        chatRoomUser.markAsRead();
        chatRoomUserRepository.save(chatRoomUser);
    }

    public void updateReadStatus(String messageId, Long userId) {
        log.info("채팅 읽음, messageId: {}", messageId);

        ChatMessage message = chatMessageRepository.findById(new ObjectId(messageId)).get();
        message.updateMessageStatus(MessageStatus.DELIVERED);
        chatMessageRepository.save(message);
        log.info("메시지 읽음상태 업데이트됨: {}", messageId);

        ChatRoomUser receiverChatRoomUser = chatRoomUserRepository.findById_ChatRoomId(message.getChatRoomId()).stream()
                .filter(cru -> !cru.getUser().getId().equals(userId))
                .findFirst().get();
        receiverChatRoomUser.markAsRead();
        chatRoomUserRepository.save(receiverChatRoomUser);
        log.info("채팅방 읽음상태 업데이트됨: {}번 방, userId: {}", message.getChatRoomId(), userId);
    }

    public List<ChatMessageResponse> findMessagesByIdAndLastId(Long roomId, Long lastId) {
        int pageSize = 30;
        Pageable pageable = PageRequest.of(
                lastId.intValue() / pageSize,
                pageSize,
                Sort.by("createdAt").descending()
        );

        Page<ChatMessage> messages = chatMessageRepository.findMessagesByIdAndLastId(roomId, pageable);
        boolean isEnd = messages.hasNext();
        Set<Long> senderIds = messages.stream()
                .map(ChatMessage::getSenderId)
                .collect(Collectors.toSet());
        Map<Long, User> users = userRepository.findByIdIn(senderIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        return messages.map(message -> {
            User sender = users.get(message.getSenderId());
            String imageUrl = (sender != null) ? sender.getImageUrl() : null;
            return new ChatMessageResponse(
                    message.getId().toString(),
                    message.getChatRoomId(),
                    message.getSenderId(),
                    message.getSenderName(),
                    imageUrl,
                    message.getCreatedAt(),
                    message.getContent(),
                    isEnd
            );
        }).getContent();
    }
}
