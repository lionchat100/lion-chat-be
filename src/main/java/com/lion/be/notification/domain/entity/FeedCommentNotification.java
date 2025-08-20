package com.lion.be.notification.domain.entity;

import com.lion.be.notification.domain.NotificationType;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedCommentNotification extends Notification {
    private Long feedId;

    public FeedCommentNotification(Long senderId, Long receiverId, Long feedId){
        this.feedId = feedId;
        this.type = NotificationType.COMMENT;
        this.id = new NotificationId(senderId, receiverId);
    }

}
