package com.lion.be.user.controller.dto;

import java.util.List;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequest {

	@NotEmpty(message = "대표 사진을 최소 1장 추가해주세요.")
	@Size(max = 3, message = "사진은 최대 3장까지 업로드 가능합니다.")
	private List<String> imageUrls;

	@NotNull(message = "성별을 선택해주세요.")
	private Gender gender;

	@NotBlank(message = "대학교를 입력해주세요.")
	private String university;

	@NotBlank(message = "직책을 입력해주세요.")
	private String position;

	@NotNull(message = "MBTI를 선택해주세요.")
	private Mbti mbti;
}
