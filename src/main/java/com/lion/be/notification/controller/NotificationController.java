package com.lion.be.notification.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.notification.controller.dto.NotificationResponse;
import com.lion.be.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 현재 로그인한 사용자의 모든 알림을 페이징하여 조회
    @GetMapping("/api/notifications")
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.fetchMyNotifications(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(notifications);
    }

    // 특정 알림을 읽음 처리
    @PostMapping("/api/notifications/{notificationId}/read")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        notificationService.readNotification(notificationId, userPrincipal.getId());
        return ResponseEntity.ok().build();
    }

}
