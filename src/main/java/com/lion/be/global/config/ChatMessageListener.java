package com.lion.be.global.config;


import com.lion.be.chat.domain.dto.ChatMessageDto;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.service.ChatMessageWriteService;
import com.lion.be.chat.service.ChatRoomService;
import com.lion.be.chat.service.ChatRoomUserWriteService;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.service.UserReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageWriteService chatMessageWriteService;
    private final ChatRoomService chatRoomService;
    private final ChatRoomUserWriteService chatRoomUserWriteService;
    private final UserReadService userReadService;

    @Transactional
    @JmsListener(
            destination = "chat.queue",               // ✨ 구독할 토픽 이름. 토픽을 구독해둬야 분산된 서버가 각각 메시지를 받을 수 있다.
            containerFactory = "jmsQueueListenerContainerFactory" // ✨ ActiveMQConfig에 정의한 Queue용 팩토리 지정g
    )
    public void handleChatMessage(ChatMessageRequest messageRequest) {
        log.info("Received message from ActiveMQ for chat room {}: {}", messageRequest.getChatRoomId(), messageRequest.getContent());

        // 1. 발신자 정보 조회 (DTO에 senderId가 있으므로 DB에서 전체 엔티티 조회)
        User sender = userReadService.fetchById(messageRequest.getSenderId());

        // 2. 메시지 저장 (MongoDB)
        ChatMessage savedMessage = chatMessageWriteService.saveMessage(
                messageRequest.getChatRoomId(),
                sender.getName(),
                sender.getId(),
                messageRequest.getContent(),
                messageRequest.getDate()
        );

        // 3. 채팅방 정보 업데이트 (RDBMS, 낙관적 락 적용됨)
        chatRoomService.updateRecentMessage(
                messageRequest.getChatRoomId(),
                messageRequest.getContent(),
                messageRequest.getDate()
        );

        // 4. 상대방을 '안 읽음' 상태로 변경 (RDBMS)
        chatRoomUserWriteService.updateOpponentToUnread(messageRequest.getChatRoomId(), sender.getId());

        // 5. 클라이언트에게 최종 메시지 DTO를 만들어 브로드캐스팅
        ChatMessageDto messageDto = new ChatMessageDto(
                savedMessage.getId().toHexString(),
                savedMessage.getSenderName(),
                savedMessage.getSenderId(),
                LocalDateTime.ofInstant(savedMessage.getDate(), ZoneId.of("Asia/Seoul")),
                savedMessage.getContent()
        );

        // [중요] 토픽 경로 수정: 기존 코드와 일치시키기
        String destination = "/topic/chatroom" + messageRequest.getChatRoomId();
        messagingTemplate.convertAndSend(destination, messageDto);

        log.info("Processed and sent message to destination: {}", destination);
    }
}