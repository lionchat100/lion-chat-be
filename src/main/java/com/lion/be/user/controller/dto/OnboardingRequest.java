package com.lion.be.user.controller.dto;

import java.util.List;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;

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

	@NotBlank(message = "대학교를 입력해주세요.")
	String universityName,

	@NotNull(message = "직책을 선택해주세요.")
	Position position,

	@NotNull(message = "MBTI를 선택해주세요.")
	Mbti mbti
) {
}
