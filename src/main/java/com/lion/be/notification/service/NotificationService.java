package com.lion.be.notification.service;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.notification.controller.dto.NotificationResponse;
import com.lion.be.notification.domain.NotificationType;
import com.lion.be.notification.domain.entity.Notification;
import com.lion.be.notification.repository.NotificationRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.userlike.controller.dto.LikeNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

//    @Transactional
//    public void sendToUser(Long userId, LikeNotification notification) {
//        try {
//            messagingTemplate.convertAndSendToUser(
//                    userId.toString(),
//                    "/queue/like-notifications",
//                    notification
//            );
//
//            log.info("좋아요 알림 전송 성공: userId={}", userId);
//        } catch (Exception e) {
//            log.error("좋아요 알림 전송 실패: userId={}", userId, e);
//        }
//    }

    @Transactional
    public void send(User recipient, NotificationType notificationType, String content, String relatedUrl) {
        try {
            // 1. 알림 객체 생성 및 DB 저장
            Notification notification = new Notification(recipient, notificationType, content, relatedUrl);

            notificationRepository.save(notification);
            log.info("알림 저장 성공: recipientId={}, type={}", recipient.getId(), notificationType);

            // 2. 실시간 알림 전송
            // DTO로 변환하여 전송
            NotificationResponse notificationResponse = NotificationResponse.from(notification);

            // StompInterceptor에서 설정한 user 정보를 기반으로 특정 사용자에게 메시지를 보냄
            // /user/{userId}/queue/notifications 경로로 전송됨
            messagingTemplate.convertAndSendToUser(
                    recipient.getId().toString(),
                    "/queue/notifications", // 프론트엔드와 약속된 구독 경로
                    notificationResponse
            );

            log.info("실시간 알림 전송 성공: recipientId={}", recipient.getId());
        } catch (Exception e) {
            log.error("알림 생성 및 전송 실패: recipientId={}", recipient.getId(), e);
        }
    }

    /**
     * 현재 사용자의 알림 목록을 페이징하여 조회합니다.
     * @param userId 현재 로그인한 사용자의 ID
     * @param pageable 페이징 정보
     * @return 페이징 처리된 알림 응답 DTO
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> fetchMyNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByRecipientId(userId, pageable);
        return notifications.map(NotificationResponse::from);
    }

    /**
     * 특정 알림을 읽음 처리합니다.
     * @param notificationId 읽음 처리할 알림의 ID
     * @param userId 현재 로그인한 사용자의 ID (보안 검증용)
     */
    @Transactional
    public void readNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.read(); // 엔티티의 상태를 변경 (더티 체킹으로 업데이트)
    }

}
