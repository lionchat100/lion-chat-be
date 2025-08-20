package com.lion.be.notification.service;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.image.repository.ImageRepository;
import com.lion.be.notification.domain.NotificationType;
import com.lion.be.notification.domain.dto.NotificationEvent;
import com.lion.be.notification.domain.dto.NotificationResponse;
import com.lion.be.notification.domain.entity.Notification;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import com.lion.be.user.service.UserReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationWriteService notificationWriteService;
    private final UserReadService userReadService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processMessage(NotificationEvent event){
        Notification notification = notificationWriteService.save(event.fromUserId(), event.toUserId(), event.type(), event.targetId());

        User receiver = userReadService.fetchById(event.fromUserId());
        User sender = userRepository.fetchByIdWithPhotos(event.toUserId()).orElseThrow(
                ()->new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        String destination = "/topic/alarm/"+receiver.getId();
        String imageUrl = sender.getUserPhotos().get(0).getImageUrl();

        NotificationResponse message = NotificationResponse.toResponse(
                notification.getId(),
                sender.getId(),
                sender.getNickname(),
                receiver.getId(),
                receiver.getNickname(),
                event.type(),
                notification.getCreatedAt(),
                imageUrl
        );

        messagingTemplate.convertAndSend(destination, message);

    }
}
