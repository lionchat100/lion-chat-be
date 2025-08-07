package com.lion.be.user.controller.dto;

import java.util.List;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.University;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OnboardingRequest(

	@NotEmpty(message = "대표 사진을 최소 1장 추가해주세요.")
	@Size(max = 3, message = "사진은 최대 3장까지 업로드 가능합니다.")
	List<String> userPhotos,

	@NotNull(message = "성별을 선택해주세요.")
	Gender gender,

	@NotNull(message = "대학교를 선택해주세요.")
	University university,

	@NotNull(message = "직책을 선택해주세요.")
	Position position,

	@NotNull(message = "MBTI를 선택해주세요.")
	Mbti mbti,

	@NotBlank(message = "한줄소개 입력해주세요.")
	String bio,

	@AssertTrue(message = "필수 약관에 동의해주세요.")
	Boolean requiredAgreements, // 필수 약관들을 묶어서 처리

	Boolean marketingAgreement,  // 마케팅 동의 더 추가될수있음

	Boolean isUniversityView // 학교정보 공개 여부
) {
}
