package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatRoomDto;
import com.lion.be.chat.domain.dto.ChatRoomListResponse;
import com.lion.be.chat.domain.entity.ChatRoom;
import com.lion.be.chat.repository.ChatRoomRepository;
import com.lion.be.chat.domain.entity.ChatRoomUser;
import com.lion.be.chat.repository.ChatRoomUserRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 채팅방 목록을 조회합니다.
     *
     * @param userId 조회하려는 사용자의 ID
     * @return 채팅방 목록
     */
    public List<ChatRoomListResponse> getMyChatRooms(Long userId) {
        return chatRoomUserRepository.findMyChatRoomListByUserId(userId);
    }

    public boolean isThereRoom(Long roomId) {
        return chatRoomRepository.findChatRoom(roomId).isPresent();
    }

    @Transactional
    public ChatRoomDto joinChatRoom(Long currentUserId, Long opponentUserId) {
        User currentUser = userRepository.fetchById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User opponentUser = userRepository.fetchById(opponentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Opponent user not found"));

        if(currentUser.getId().equals(opponentUser.getId())){
            throw new IllegalArgumentException("Cannot create a chat room with yourself");
        }

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomIdInTwo(currentUserId, opponentUserId);
        if(optionalChatRoom.isPresent()){
            return new ChatRoomDto(optionalChatRoom.get().getId());
        }

        ChatRoom chatRoom = new ChatRoom(false);
        ChatRoomUser currentUserChatRoom = new ChatRoomUser(false);
        ChatRoomUser opponentUserChatRoom = new ChatRoomUser(false);

        currentUserChatRoom.setUser(currentUser);
        opponentUserChatRoom.setUser(opponentUser);
        currentUserChatRoom.setChatRoom(chatRoom);
        opponentUserChatRoom.setChatRoom(chatRoom);

        chatRoom.addChatRoomUser(currentUserChatRoom);
        chatRoom.addChatRoomUser(opponentUserChatRoom);
        currentUser.addChatRoomUser(currentUserChatRoom);
        opponentUser.addChatRoomUser(opponentUserChatRoom);

        ChatRoom result = chatRoomRepository.save(chatRoom);

        return new ChatRoomDto(result.getId());

    }

    @Transactional
    public void updateRecentMessage(Long chatRoomId, String content, LocalDateTime date) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        chatRoom.setRecentMessageContent(content);
        chatRoom.setRecentMessageDt(date);
    }
}
