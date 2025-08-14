package com.lion.be.user.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {
	FRONTEND("프론트엔드"),
	BACKEND("백엔드"),
	FULLSTACK("풀스택"),
	AI("AI"),
	UX_UI("디자인"),
	PM("PM")
	;

	private final String koreanName;

	@JsonValue
	public String getKoreanName() {
		return koreanName;
	}

	@JsonCreator
	public static Position fromValue(String value) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("Position value cannot be null or empty");
		}

		// 한국어 이름으로 매칭
		return Arrays.stream(values())
			.filter(position -> position.getKoreanName().equals(value))
			.findFirst()
			.orElseGet(() -> {
				// 영어 이름으로도 시도
				try {
					return valueOf(value.toUpperCase());
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException("Invalid position: " + value);
				}
			});
	}
}
