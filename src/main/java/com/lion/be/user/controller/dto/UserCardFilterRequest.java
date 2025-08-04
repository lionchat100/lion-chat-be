package com.lion.be.user.controller.dto;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.entity.University;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCardFilterRequest {
	private Gender preferredGender;
	private Mbti preferredMbti;
	private University preferredUniversity;
	private Position preferredPosition;

}
