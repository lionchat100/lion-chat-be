package com.lion.be.notification.domain.dto;

import com.lion.be.notification.domain.NotificationType;

import java.time.ZonedDateTime;

public record NotificationResponse(
        Long senderId,
        Long receiverId,
        Long destination,
        String notificationType,
        ZonedDateTime createdAt,
        String imageURl
) {
    public static NotificationResponse toResponse(
            Long senderId,
            Long receiverId,
            Long destination,
            NotificationType notificationType,
            ZonedDateTime createdAt,
            String imageURl
    ) {
        return new NotificationResponse(
                senderId,
                receiverId,
                destination,
                notificationType.toString(),
                createdAt,
                imageURl
        );
    }
}
