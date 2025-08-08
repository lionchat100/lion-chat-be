package com.lion.be.user.controller.dto;

import java.util.List;

public record OnboardingLabelsResponse(
	List<GenderOption> genders,
	List<UniversityOption> universities,
	List<PositionOption> positions,
	List<MbtiOption> mbtis,
	List<PreferenceTypeOption> preferenceType
) {
	public record GenderOption(String code, String name) {}
	public record UniversityOption(String code, String name) {}
	public record PositionOption(String code, String name) {}
	public record MbtiOption(String code, String name) {}
	public record PreferenceTypeOption(String code, String name) {}
}
