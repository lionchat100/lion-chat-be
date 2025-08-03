package com.lion.be.chat.domain.chatroomuser.service;

import com.lion.be.chat.domain.chatroomuser.entity.ChatRoomUser;
import com.lion.be.chat.domain.chatroomuser.repository.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomUserWriteService {

    private final ChatRoomUserRepository chatRoomUserRepository;


    @Transactional
    public void updateOpponentToUnread(Long chatRoomId, Long senderId) {
        ChatRoomUser opponent = chatRoomUserRepository.findOpponent(chatRoomId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room user not found"));

        opponent.setIsRead(false);
    }


    @Transactional
    public void updateReceiverToRead(Long chatRoomId, Long receiverId) {
        ChatRoomUser currentRoomUser = chatRoomUserRepository.findMine(chatRoomId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room user not found"));

        currentRoomUser.setIsRead(true);
    }
}
