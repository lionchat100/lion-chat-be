package com.lion.be.user.domain.entity;

import com.lion.be.user.domain.OnboardingStatus;

import lombok.Getter;

@Getter
public class OnboardingStatusVO {
	private final boolean completed;

	private OnboardingStatusVO(boolean completed) {
		this.completed = completed;
	}

	public static OnboardingStatusVO from(OnboardingStatus status) {
		return new OnboardingStatusVO(status == OnboardingStatus.COMPLETED);
	}

	public static OnboardingStatusVO from(User user) {
		return new OnboardingStatusVO(user.isOnboardingCompleted());
	}
}
