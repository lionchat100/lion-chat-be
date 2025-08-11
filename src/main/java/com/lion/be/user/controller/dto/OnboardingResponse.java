package com.lion.be.user.controller.dto;

public record OnboardingResponse(
	Long userId,
	String message,
	Boolean isOnboardingCompleted

) {

	public static OnboardingResponse success(Long userId) {
		return new OnboardingResponse(
			userId,
			"온보딩이 완료되었습니다.",
			true
		);
	}
}
