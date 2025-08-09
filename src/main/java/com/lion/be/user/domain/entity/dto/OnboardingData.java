package com.lion.be.user.domain.entity.dto;

import java.util.List;

import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.PreferenceType;
import com.lion.be.user.domain.University;

public record OnboardingData(
	String nickname,
	Gender gender,
	University university,
	Position position,
	Mbti mbti,
	List<String> userPhotos,
	String bio,
	Boolean requiredAgreements,  // 필수 약관
	Boolean marketingAgreements,  // 마케팅 약관
	Boolean isUniversityView, // 학교 정보 공개여부
	PreferenceType preferenceType
) {

	public static OnboardingData from(OnboardingRequest request) {
		return new OnboardingData(
			request.nickname(),
			request.gender(),
			request.university(),
			request.position(),
			request.mbti(),
			request.userPhotos(),
			request.bio(),
			request.requiredAgreements(),
			request.marketingAgreement(),
			request.isUniversityView(),
			request.preferenceType()
		);
	}
}
