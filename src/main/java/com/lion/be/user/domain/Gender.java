package com.lion.be.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
	WOMEN("여성"),
	MEN("남성");

	private final String koreanName;
}
