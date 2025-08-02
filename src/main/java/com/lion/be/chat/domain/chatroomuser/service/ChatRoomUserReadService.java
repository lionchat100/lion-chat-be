package com.lion.be.chat.domain.chatroomuser.service;

import com.lion.be.chat.domain.chatroom.repository.ChatRoomRepository;
import com.lion.be.chat.domain.chatroomuser.entity.ChatRoomUser;
import com.lion.be.chat.domain.chatroomuser.repository.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomUserReadService {

    private final ChatRoomUserRepository chatRoomUserRepository;

    public boolean isThereRoomUser(Long roomId, Long currentMemberId) {
        return chatRoomUserRepository.findMine(roomId, currentMemberId).isPresent();
    }
}
