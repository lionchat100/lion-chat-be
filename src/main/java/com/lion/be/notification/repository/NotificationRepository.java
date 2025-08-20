package com.lion.be.notification.repository;

import com.lion.be.notification.domain.entity.Notification;
import com.lion.be.notification.domain.entity.NotificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, NotificationId> {
}
