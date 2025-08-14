package com.lion.be.admin.controller.dto;

import java.time.LocalDateTime;

import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;

public record BanUserResponse(
	String message,
	Long bannedUserId,
	String bannedUserEmail,
	String reason,
	Role role,
	LocalDateTime bannedAt
) {
	public static BanUserResponse of(User user, String reason) {
		return new BanUserResponse(
			"사용자 정지를 성공하였습니다.",
			user.getId(),
			user.getEmail(),
			reason,
			user.getRole(),
			LocalDateTime.now()
		);
	}
}
