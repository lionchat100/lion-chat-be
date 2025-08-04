package com.lion.be.user.domain.entity.dto;

import java.util.List;

import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.entity.University;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OnboardingData {
	private final Gender gender;
	private final String universityName;
	private final Position position;
	private final Mbti mbti;
	private final List<String> userPhotos;

	public static OnboardingData from(OnboardingRequest request) {
		return OnboardingData.builder()
				.gender(request.getGender())
				.universityName(request.getUniversityName())
				.position(request.getPosition())
				.mbti(request.getMbti())
				.userPhotos(request.getUserPhotos())
				.build();
	}
}
