package com.lion.be.chat.domain.chatroom.service;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.chatroom.entity.ChatRoom;
import com.lion.be.chat.domain.chatroom.repository.ChatRoomRepository;
import com.lion.be.chat.domain.chatroomuser.entity.ChatRoomUser;
import com.lion.be.chat.domain.chatroomuser.repository.ChatRoomUserRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomWriteService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom joinChatRoom(Long currentUserId, Long opponentUserId) {
        User currentUser = userRepository.fetchById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User opponentUser = userRepository.fetchById(opponentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Opponent user not found"));

        if(currentUser.getId().equals(opponentUser.getId())){
            throw new IllegalArgumentException("Cannot create a chat room with yourself");
        }

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomIdInTwo(currentUserId, opponentUserId);
        if(optionalChatRoom.isPresent()){
            return optionalChatRoom.get();
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

        return chatRoomRepository.save(chatRoom);

    }

    @Transactional
    public void updateRecentMessage(Long chatRoomId, String content, LocalDateTime date) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        chatRoom.setRecentMessageContent(content);
        chatRoom.setRecentMessageDt(date);
    }

}
