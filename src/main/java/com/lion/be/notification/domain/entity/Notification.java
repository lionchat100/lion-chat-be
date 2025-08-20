package com.lion.be.notification.domain.entity;

import com.lion.be.global.entity.BaseEntity;
import com.lion.be.notification.domain.NotificationType;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient; // 알림을 받는 유저

    @Column(nullable = false)
    private String content; // 알림 내용

    @Column
    private String relatedUrl; // 알림 클릭 시 이동할 URL

    @Column(nullable = false)
    private boolean isRead = false; // 읽음 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType; // 알림 종류

    public Notification(User recipient, NotificationType notificationType, String content, String relatedUrl) {
        this.recipient = recipient;
        this.notificationType = notificationType;
        this.content = content;
        this.relatedUrl = relatedUrl;
    }

    public void read() {
        this.isRead = true;
    }

}