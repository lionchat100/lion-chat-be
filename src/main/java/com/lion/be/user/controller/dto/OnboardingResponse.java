package com.lion.be.user.controller.dto;

import com.lion.be.user.domain.OnboardingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingResponse {
	private Long userId;
	private String message;
	private OnboardingStatus status;

	public static OnboardingResponse success(Long userId) {
		return OnboardingResponse.builder()
			.userId(userId)
			.message("온보딩이 완료되었습니다.")
			.status(OnboardingStatus.COMPLETED)
			.build();
	}
}
