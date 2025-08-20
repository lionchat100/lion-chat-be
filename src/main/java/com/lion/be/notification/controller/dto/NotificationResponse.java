package com.lion.be.notification.controller.dto;

import com.lion.be.notification.domain.NotificationType;
import com.lion.be.notification.domain.entity.Notification;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String content;
    private String relatedUrl;
    private boolean isRead;
    private NotificationType notificationType;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .relatedUrl(notification.getRelatedUrl())
                .isRead(notification.isRead())
                .notificationType(notification.getNotificationType())
                .createdAt(notification.getCreatedAt())
                .build();
    }

}