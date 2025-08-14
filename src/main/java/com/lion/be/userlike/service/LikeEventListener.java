package com.lion.be.userlike.service;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.lion.be.notification.service.NotificationWriteService;
import com.lion.be.userlike.controller.dto.LikeNotification;
import com.lion.be.userlike.domain.entity.LikeCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LikeEventListener {

	private final NotificationWriteService notificationWriteService;

	@EventListener
	@Async
	public void handleLikeCreated(LikeCreatedEvent event) {
		LikeNotification notification = new LikeNotification(
			event.getFromUserId(),
			event.getFromUserNickname(),
			"님이 회원님을 좋아합니다"
		);

		notificationWriteService.sendToUser(event.getToUserId(), notification);
	}
}
