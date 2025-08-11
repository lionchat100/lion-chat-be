package com.lion.be.user.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreferenceType {
	PREFERENCE_FOCUSED("PREFERENCE_FOCUSED"),
	POSITION_FOCUSED("POSITION_FOCUSED"),
	CAREER_FOCUSED("CAREER_FOCUSED");

	@JsonValue
	private final String koreanName;
}
