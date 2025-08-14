package com.lion.be.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.admin.controller.dto.BanUserRequest;
import com.lion.be.admin.controller.dto.BanUserResponse;
import com.lion.be.admin.controller.dto.UnbanUserRequest;
import com.lion.be.admin.controller.dto.UnbanUserResponse;
import com.lion.be.auth.service.RefreshTokenService;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.service.UserReadService;
import com.lion.be.user.service.UserWriteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserWriteService {
	private final UserWriteService userWriteService;
	private final RefreshTokenService refreshTokenService;
	private final UserReadService userReadService;

	// 사용자 차단 (Role 변경 + 토큰 처리)
	public BanUserResponse banUser(BanUserRequest request) {
		User user = userReadService.fetchByEmail(request.email());
		// 1. Role 변경
		user.updateRole(Role.BANNED);
		userWriteService.save(user);
		// 2. 토큰 처리
		refreshTokenService.deleteToken(user.getEmail());

		return BanUserResponse.of(user, request.reason());
	}

	// 차단 해제 (Role만 변경)
	public UnbanUserResponse unbanUser(UnbanUserRequest request) {
		User user = userReadService.fetchByEmail(request.email());

		user.updateRole(Role.USER);
		userWriteService.save(user);

		return UnbanUserResponse.of(user, request.reason());
	}
}
