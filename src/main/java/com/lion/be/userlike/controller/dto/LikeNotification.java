package com.lion.be.userlike.controller.dto;

import java.time.LocalDateTime;

public record LikeNotification(
	Long fromUserId,
	String fromUserName,
	String message,
	LocalDateTime timestamp
) {
	// 편의 생성자
	public LikeNotification(Long fromUserId, String fromUserName, String message) {
		this(fromUserId, fromUserName, message, LocalDateTime.now());
	}
}
