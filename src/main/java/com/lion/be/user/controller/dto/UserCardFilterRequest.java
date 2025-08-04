package com.lion.be.user.controller.dto;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;

public record UserCardFilterRequest (
	Gender preferredGender,
	Mbti preferredMbti,
	String preferredUniversityName,
	Position preferredPosition
){
}
