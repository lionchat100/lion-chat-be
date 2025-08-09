package com.lion.be.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {
	BACKEND("백엔드"),
	FRONTEND("프론트엔드"),
	UX_UI("UX/UI 디자이너"),
	PM("PM"),
	FULLSTACK("풀스택");

	private final String koreanName;
}
