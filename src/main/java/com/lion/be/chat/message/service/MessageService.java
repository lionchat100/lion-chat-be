package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.dto.ChatMessageRequest;
import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.message.repository.ChatMessageRepository;
import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.chat.room.domain.entity.ChatRoom;
import com.lion.be.chat.room.domain.entity.ChatRoomUser;
import com.lion.be.chat.room.repository.ChatRoomRepository;
import com.lion.be.chat.room.repository.ChatRoomUserRepository;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final UserJpaRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(ChatMessageRequest request, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.chatRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ChatMessage message = ChatMessageRequest.fromRequest(request, sender);

        message.updateMessageStatus(MessageStatus.PENDING);
        chatMessageRepository.save(message);
        log.info("메시지 PENDING 상태로 저장됨: {}", message);

        try {
            String destination = "/topic/chatroom/" + message.getChatRoomId();
            ChatMessageResponse response = ChatMessageResponse.toResponse(message, sender, sender.getImageUrl(), false);
            messagingTemplate.convertAndSend(destination, response);

            message.updateMessageStatus(MessageStatus.PUBLISHED);
            chatMessageRepository.save(message);
            log.info("메시지 발행 성공. 상태를 PUBLISHED로 변경합니다.");

            chatRoom.updateRecentMessage(message.getContent(), message.getCreatedAt());
            chatRoomRepository.save(chatRoom);
            log.info("채팅방 마지막 내용, 시간 업데이트됨: {}번 방", chatRoom.getId());

            ChatRoomUser chatRoomUser = chatRoomUserRepository.findById_ChatRoomIdAndId_UserId(message.getChatRoomId(),
                    senderId);
            chatRoomUser.markAsRead();
            chatRoomUserRepository.save(chatRoomUser);

            ChatRoomUser receiverChatRoomUser = chatRoomUserRepository.findById_ChatRoomId(message.getChatRoomId())
                    .stream()
                    .filter(cru -> !cru.getUser().getId().equals(senderId))
                    .findFirst().orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
            receiverChatRoomUser.markAsUnRead();
            chatRoomUserRepository.save(receiverChatRoomUser);

        } catch (Exception e) {
            log.error("메시지 발행에 실패했습니다. 메시지는 PENDING 상태로 유지됩니다. MessageId: {}, Error: {}",
                    message.getId(), e.getMessage());
        }
    }

    public void updateReadStatus(String messageId, Long userId) {
        log.info("채팅 읽음, messageId: {}", messageId);

        ChatMessage message = chatMessageRepository.findById(new ObjectId(messageId))
                .orElseThrow(() -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND));
        message.updateMessageStatus(MessageStatus.DELIVERED);
        chatMessageRepository.save(message);
        log.info("메시지 읽음상태 업데이트됨: {}", messageId);

        ChatRoomUser receiverChatRoomUser = chatRoomUserRepository.findById_ChatRoomId(message.getChatRoomId()).stream()
                .filter(cru -> !cru.getUser().getId().equals(userId))
                .findFirst().orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        receiverChatRoomUser.markAsRead();
        chatRoomUserRepository.save(receiverChatRoomUser);
        log.info("채팅방 읽음상태 업데이트됨: {}번 방, userId: {}", message.getChatRoomId(), userId);
    }

    public List<ChatMessageResponse> findMessagesByIdAndLastId(Long roomId, Long lastId, Long userId) {
        chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        int pageSize = 30;
        Pageable pageable = PageRequest.of(
                lastId.intValue() / pageSize,
                pageSize,
                Sort.by("createdAt").descending()
        );

        Page<ChatMessage> messages = chatMessageRepository.findMessagesByIdAndLastId(roomId, pageable);
        boolean isEnd = messages.hasNext();

        List<ObjectId> unreadMessageIds = messages.getContent().stream()
                .filter(message -> !message.getSenderId().equals(userId))
                .map(ChatMessage::getId)
                .collect(Collectors.toList());

        if (!unreadMessageIds.isEmpty()) {
            chatMessageRepository.markMessagesAsRead(unreadMessageIds);
        }

        Set<Long> senderIds = messages.stream()
                .map(ChatMessage::getSenderId)
                .collect(Collectors.toSet());

        Map<Long, User> users = userRepository.findByIdIn(senderIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        ChatRoomUser chatRoomUser = chatRoomUserRepository.findById_ChatRoomIdAndId_UserId(roomId, userId);
        chatRoomUser.markAsRead();
        chatRoomUserRepository.save(chatRoomUser);

        List<ChatMessage> messageList = messages.getContent();
        int lastIndex = messageList.size() - 1;
        return IntStream.range(0, messageList.size())
                .mapToObj(i -> {
                    ChatMessage message = messageList.get(i);
                    User sender = users.get(message.getSenderId());

                    String imageUrl = sender.getUserPhotos().isEmpty()
                            ? null
                            : sender.getUserPhotos().get(0).getImageUrl();

                    boolean isLast = (i == lastIndex) && isEnd;

                    return new ChatMessageResponse(
                            message.getId().toString(),
                            message.getChatRoomId(),
                            message.getSenderId(),
                            message.getSenderName(),
                            imageUrl,
                            message.getCreatedAt(),
                            message.getContent(),
                            isLast
                    );
                })
                .collect(Collectors.toList());
    }
}
