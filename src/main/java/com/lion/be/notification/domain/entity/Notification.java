package com.lion.be.notification.domain.entity;


import com.lion.be.notification.domain.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Notification {

    @EmbeddedId
    protected NotificationId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    protected NotificationType type;

}
