package com.lion.be.user.domain.entity.dto;

import java.util.List;

import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;

public record OnboardingData(
	Gender gender,
	String universityName,
	Position position,
	Mbti mbti,
	List<String> userPhotos
) {

	public static OnboardingData from(OnboardingRequest request) {
		return new OnboardingData(
			request.gender(),
			request.universityName(),
			request.position(),
			request.mbti(),
			request.userPhotos()
		);
	}
}
