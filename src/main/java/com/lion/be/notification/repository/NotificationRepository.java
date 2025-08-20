package com.lion.be.notification.repository;

import com.lion.be.notification.domain.entity.Notification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepository {

    void save(Notification notification);

    Page<Notification> findByRecipientId(Long recipientId, Pageable pageable);

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

}
