package com.lion.be.user.controller.dto;

import com.lion.be.user.domain.OnboardingStatus;

public record OnboardingResponse(
	Long userId,
	String message,
	OnboardingStatus status

) {

	public static OnboardingResponse success(Long userId) {
		return new OnboardingResponse(
			userId,
			"온보딩이 완료되었습니다.",
			OnboardingStatus.COMPLETED
		);
	}
}
