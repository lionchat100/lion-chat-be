package com.lion.be.user.controller.dto;

import java.util.List;

import com.lion.be.user.domain.PreferenceType;

public record UserProfileUpdateRequest
	(String bio,
	 List<Long> imageIds,
	 PreferenceType preferenceType) {
}
