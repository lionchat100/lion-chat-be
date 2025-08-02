package com.lion.be.chat.domain.chatroom.service;

import com.lion.be.chat.domain.chatroom.dto.ChatRoomListDto;
import com.lion.be.chat.domain.chatroom.entity.ChatRoom;
import com.lion.be.chat.domain.chatroom.repository.ChatRoomRepository;
import com.lion.be.chat.domain.chatroomuser.repository.ChatRoomUserRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomReadService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;

    public List<ChatRoomListDto> fetchAll(Long currentUserId) {
        User user =userRepository.fetchById(currentUserId).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );

       return chatRoomRepository.findAllChatRoom(currentUserId);

    }

    public boolean isThereRoom(Long roomId) {
        return chatRoomRepository.findChatRoom(roomId).isPresent();
    }
}
