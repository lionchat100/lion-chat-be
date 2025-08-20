package com.lion.be.notification.repository.persistence.jpa;

import com.lion.be.notification.domain.entity.Notification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    // 특정 수신자의 알림 목록을 최신 순으로 페이징하여 조회
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    // 특정 알림을 찾되, 수신자 ID를 함께 검증하여 다른 사람의 알림을 조작하지 못하도록 함
    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

}
