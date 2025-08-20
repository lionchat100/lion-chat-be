package com.lion.be.notification.domain.entity;


import com.lion.be.global.entity.BaseEntity;
import com.lion.be.notification.domain.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUserId;
    private Long toUserId;

    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    private Long targetId;

    public Notification(Long fromUserId, Long toUserId, Long targetId, NotificationType type){
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.targetId = targetId;
        this.type = type;
    }

}
