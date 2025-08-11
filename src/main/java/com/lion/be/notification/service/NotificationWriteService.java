package com.lion.be.notification.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.lion.be.userlike.controller.dto.LikeNotification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationWriteService {

	private final SimpMessagingTemplate messagingTemplate;

	public void sendToUser(Long userId, LikeNotification notification) {
		try {
			messagingTemplate.convertAndSendToUser(
				userId.toString(),
				"/queue/like-notifications",
				notification
			);

			log.info("좋아요 알림 전송 성공: userId={}", userId);
		} catch (Exception e) {
			log.error("좋아요 알림 전송 실패: userId={}", userId, e);
		}
	}
}
