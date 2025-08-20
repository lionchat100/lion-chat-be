package com.lion.be.user.controller.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lion.be.user.domain.PreferenceType;

public record UserProfileUpdateRequest
	(String bio,
	 List<Long> imageIds,
	 @JsonProperty("focusType")
	 PreferenceType preferenceType) {
}
