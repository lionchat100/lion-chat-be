package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.entity.ChatRoom;
import com.lion.be.chat.domain.entity.ChatRoomUser;
import com.lion.be.chat.repository.ChatRoomRepository;
import com.lion.be.chat.repository.ChatRoomUserRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;

    @Override
    public ChatRoom createChatRoom(ChatMessageRequest chatMessageRequest) {
        return findOrCreateChatRoom(chatMessageRequest.senderId(), chatMessageRequest.receiverId());
    }

    public ChatRoom findOrCreateChatRoom(Long senderId, Long receiverId) {
        Optional<Long> existingChatRoomId = chatRoomUserRepository.findChatRoomIdByTwoUserIds(senderId, receiverId);

        if (existingChatRoomId.isPresent()) {
            log.info("기존 채팅방 찾음. ChatRoomId: {}", existingChatRoomId.get());
            return chatRoomRepository.findById(existingChatRoomId.get())
                    .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        }

        log.info("새 채팅방 생성 시작. SenderId: {}, ReceiverId: {}", senderId, receiverId);
        User sender = userRepository.findById(senderId);
        User receiver = userRepository.findById(receiverId);

        ChatRoom chatRoom = new ChatRoom(false);
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        List<ChatRoomUser> chatRoomUsers = createChatRoomUsers(savedChatRoom, sender, receiver);
        chatRoomUserRepository.saveAll(chatRoomUsers);

        log.info("새 채팅방 생성 완료. ChatRoomId: {}", savedChatRoom.getId());
        return savedChatRoom;
    }

    private List<ChatRoomUser> createChatRoomUsers(ChatRoom chatRoom, User sender, User receiver) {
        List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

        ChatRoomUser senderChatRoomUser = new ChatRoomUser(chatRoom, sender, true);
        chatRoomUsers.add(senderChatRoomUser);

        ChatRoomUser receiverChatRoomUser = new ChatRoomUser(chatRoom, receiver, false);
        chatRoomUsers.add(receiverChatRoomUser);

        chatRoom.addChatRoomUser(senderChatRoomUser);
        chatRoom.addChatRoomUser(receiverChatRoomUser);

        return chatRoomUsers;
    }
}
