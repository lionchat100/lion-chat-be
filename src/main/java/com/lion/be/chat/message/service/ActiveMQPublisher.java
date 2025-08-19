package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.room.repository.ChatRoomUserRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActiveMQPublisher implements MessagePublisher {

    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String DESTINATION = "/topic/chatroom/";

    @Override
    public void publishMessage(ChatMessage message) {
        String destination = DESTINATION + message.getChatRoomId();
        User sender = userRepository.findById(message.getSenderId());
        ChatMessageResponse response = ChatMessageResponse.toResponse(message, sender, sender.getImageUrl(), false);
        messagingTemplate.convertAndSend(destination, response);
    }
}
