package com.lion.be.notification.domain.entity;

import com.lion.be.notification.domain.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedLikeNotification extends Notification {
    private Long feedId;

    public FeedLikeNotification(Long senderId, Long receiverId, Long feedId){
        this.feedId = feedId;
        this.type = NotificationType.POST_LIKE;
        this.id = new NotificationId(senderId, receiverId);
    }
}
