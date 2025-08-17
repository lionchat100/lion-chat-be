package com.lion.be.chat.room.service;

import com.lion.be.chat.room.domain.dto.ChatRoomResponse;
import com.lion.be.chat.room.domain.entity.ChatRoom;
import com.lion.be.chat.room.domain.entity.ChatRoomUser;
import com.lion.be.chat.room.repository.ChatRoomJpaRepository;
import com.lion.be.chat.room.repository.ChatRoomQueryDslRepository;
import com.lion.be.chat.room.repository.ChatRoomRepository;
import com.lion.be.chat.room.repository.ChatRoomUserRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    @Transactional
    public Long findOrCreateChatRoom(Long senderId, Long receiverId) {

        Optional<Long> chatRoomId = chatRoomUserRepository.findChatRoomIdByTwoUserIds(senderId, receiverId);
        if (chatRoomId.isPresent()) {
            log.info("기존 채팅방이 존재합니다. ChatRoomId: {}", chatRoomId.get());
            return chatRoomId.get();
        } else {
            ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom());

            User user1 = userRepository.findById(senderId);
            User user2 = userRepository.findById(receiverId);
            ChatRoomUser user1ChatRoomUser = ChatRoomUser.create(chatRoom, user1);
            ChatRoomUser user2ChatRoomUser = ChatRoomUser.create(chatRoom, user2);
            chatRoomUserRepository.save(user1ChatRoomUser);
            chatRoomUserRepository.save(user2ChatRoomUser);
            chatRoomRepository.save(chatRoom);
            log.info("새 채팅방을 생성합니다. ChatRoomId: {}", chatRoom.getId());

            return chatRoom.getId();
        }
    }

    /**
     * 채팅방에 입장할 유저 2명의 정보를 저장합니다.
     *
     * @param user1
     * @param user2
     * @return 생성된 유저 리스트
     */
    public List<ChatRoomUser> createChatRoomUsers(ChatRoomUser user1, ChatRoomUser user2) {
        chatRoomUserRepository.save(user1);
        chatRoomUserRepository.save(user2);
        return List.of(user1, user2);
    }

    /**
     * 생성할 채팅방을 저장합니다.
     *
     * @param chatRoom
     * @return 생성된 채팅방 객체
     */
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    /**
     * 채팅방 리스트를 반환합니다.
     *
     * @param userId
     * @return 채팅방 리스트
     */
    public List<ChatRoomResponse> getChatRooms(Long userId) {
        return chatRoomRepository.findChatRoomListByUserId(userId);
    }
}
