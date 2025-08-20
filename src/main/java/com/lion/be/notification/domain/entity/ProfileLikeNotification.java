package com.lion.be.notification.domain.entity;

import com.lion.be.notification.domain.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("PROFILE_LIKE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileLikeNotification extends Notification {

    //todo @Id 제거! 부모의 복합키 사용
    @Column(name = "profile_id")
    private Long profileId;

    private ProfileLikeNotification(Long fromUserId, Long toUserId, Long profileId) {
        super(fromUserId, toUserId, NotificationType.PROFILE_LIKE);
        this.profileId = profileId;
    }

    public static ProfileLikeNotification create(Long fromUserId, Long toUserId, Long profileId) {
        return new ProfileLikeNotification(fromUserId, toUserId, profileId);
    }
}
