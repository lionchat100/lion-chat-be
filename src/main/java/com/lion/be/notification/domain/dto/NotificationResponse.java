package com.lion.be.notification.domain.dto;

import com.lion.be.notification.domain.NotificationType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record NotificationResponse(
        Long id,
        Long senderId,
        String senderNickName,
        Long receiverId,
        String receiverNickName,
        String notificationType,
        LocalDateTime createdAt,
        String imageURl
) {
    public static NotificationResponse toResponse(
            Long id,
            Long senderId,
            String senderNickname,
            Long receiverId,
            String receiverNickname,
            NotificationType notificationType,
            LocalDateTime createdAt,
            String imageURl
    ) {
        return new NotificationResponse(
                id,
                senderId,
                senderNickname,
                receiverId,
                receiverNickname,
                notificationType.toString(),
                createdAt.atZone(ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime(),
                imageURl
        );
    }
}
