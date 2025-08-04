package com.lion.be.user.domain.entity.dto;

import java.util.List;

import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OnboardingData {
	private final Gender gender;
	private final String university;
	private final String position;
	private final Mbti mbti;
	private final List<String> userPhotos;

	public static OnboardingData from(OnboardingRequest request) {
		return OnboardingData.builder()
				.gender(request.getGender())
				.university(request.getUniversity())
				.position(request.getPosition())
				.mbti(request.getMbti())
				.userPhotos(request.getUserPhotos())
				.build();
	}
}
