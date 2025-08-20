package com.lion.be.notification.repository;

import com.lion.be.notification.domain.entity.Notification;
import com.lion.be.notification.repository.persistence.jpa.NotificationJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public void save(Notification notification) {
        notificationJpaRepository.save(notification);
    }

    @Override
    public Page<Notification> findByRecipientId(Long recipientId, Pageable pageable) {
        return notificationJpaRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, pageable);
    }

    @Override
    public Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId) {
        return notificationJpaRepository.findByIdAndRecipientId(id, recipientId);
    }

}
