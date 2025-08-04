package com.lion.be.chat.service;

import com.lion.be.chat.repository.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomUserReadService {

    private final ChatRoomUserRepository chatRoomUserRepository;

    public boolean isThereRoomUser(Long roomId, Long currentMemberId) {
        return chatRoomUserRepository.findMine(roomId, currentMemberId).isPresent() &&
               chatRoomUserRepository.findOpponent(roomId, currentMemberId).isPresent();
    }
}
