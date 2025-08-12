package com.lion.be.user.controller.dto;

import java.util.List;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.PreferenceType;
import com.lion.be.user.domain.University;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OnboardingRequest(
	@NotEmpty
	@Size(min = 1, max = 8 ,message = "닉네임은 최대 8자 이하입니다.")
	String nickname,

	@NotNull
	@Size(min = 1, max = 3, message = "프로필 사진은 1-3장이어야 합니다")
	List<Long> imageIds,

	@NotNull(message = "성별을 선택해주세요.")
	Gender gender,

	@NotNull(message = "대학교를 선택해주세요.")
	University university,

	@NotNull(message = "직책을 선택해주세요.")
	Position position,

	@NotNull(message = "MBTI를 선택해주세요.")
	Mbti mbti,

	@NotEmpty(message = "한줄소개 입력해주세요.")
	@Size(min = 1, max =30, message = "자기소개는 최대 30자 입니다.")
	String bio,

	@AssertTrue(message = "필수 약관에 동의해주세요.")
	Boolean requiredAgreements, // 필수 약관들을 묶어서 처리

	Boolean marketingAgreements,  // 마케팅 동의 더 추가될수있음

	Boolean isUniversityView, // 학교정보 공개 여부

	@NotNull(message = "선호를 선택해주세요.")
	PreferenceType preferenceType
) {
}
