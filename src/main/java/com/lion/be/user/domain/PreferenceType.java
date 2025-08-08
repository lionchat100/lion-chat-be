package com.lion.be.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreferenceType {
	MBTI_FOCUSED("MBTI 중시"),
	POSITION_FOCUSED("거리/직무 중시");

	private final String koreanName;
}
