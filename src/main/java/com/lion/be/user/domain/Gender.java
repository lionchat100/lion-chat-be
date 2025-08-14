package com.lion.be.user.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
	WOMEN("여성"),
	MEN("남성");

	private final String koreanName;

	@JsonValue
	public String getKoreanName() {
		return koreanName;
	}

	@JsonCreator
	public static Gender fromValue(String value) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("Gender value cannot be null or empty");
		}

		// 한국어 이름으로 매칭
		return Arrays.stream(values())
			.filter(gender -> gender.getKoreanName().equals(value))
			.findFirst()
			.orElseGet(() -> {
				// 영어 이름으로도 시도
				try {
					return valueOf(value.toUpperCase());
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException("Invalid gender: " + value);
				}
			});
	}
}
