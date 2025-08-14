package com.lion.be.admin.controller.dto;

import java.time.LocalDateTime;

import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;

public record UnbanUserResponse (
	String message,
	Long unbanUserId,
	String unbanUserEmail,
	String reason,
	Role role,
	LocalDateTime unbanAt
){
	public static UnbanUserResponse of(User user, String reason) {
		return new UnbanUserResponse(
			"사용자 해지를 성공하였습니다.",
			user.getId(),
			user.getEmail(),
			reason,
			user.getRole(),
			LocalDateTime.now()
		);
	}
}
