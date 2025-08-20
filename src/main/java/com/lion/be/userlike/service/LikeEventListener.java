package com.lion.be.userlike.service;

import com.lion.be.notification.domain.NotificationType;
import com.lion.be.notification.service.NotificationService;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.service.UserReadService;
import com.lion.be.userlike.domain.entity.LikeCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventListener {

    private final NotificationService notificationService;
    private final UserReadService userReadService;

    @EventListener
    @Async
    public void handleLikeCreated(LikeCreatedEvent event) {
        User recipient = userReadService.fetchById(event.toUserId());

        String content = event.fromUserNickname() + "님이 회원님을 좋아합니다.";
        String relatedUrl = "/profile/" + event.fromUserId();

        notificationService.send(recipient, NotificationType.LIKE, content, relatedUrl);
    }

}
