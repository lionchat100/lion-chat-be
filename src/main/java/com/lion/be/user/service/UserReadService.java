package com.lion.be.user.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.auth.controller.dto.CurrentUserResponse;
import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.controller.dto.OnboardingLabelsResponse;
import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.PreferenceType;
import com.lion.be.user.domain.University;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReadService {

    private final UserRepository userRepository;

    public User fetchByEmail(String email) {
        return userRepository.fetchByEmail(email)
            .orElseThrow(() -> new RuntimeException("fetchByEmail"));
    }

    public CurrentUserResponse fetchCurrentUserResponse(Long userId) {
        User user = fetchById(userId);
        return new CurrentUserResponse(user.getId(), user.getEmail(), user.getName(), user.getImageUrl());
    }

    public User fetchById(Long userId) {
        return userRepository.fetchById(userId)
            .orElseThrow(() -> new RuntimeException("fetchById"));
    }

    /**
     * 온보딩 완료 여부 검증
     */
    public void validateOnboardingCompleted(Long userId) {
        User user = fetchById(userId);

        if (!user.isOnboardingCompleted()) {
            throw new CustomException(ErrorCode.USER_ONBOARDING_NOT_COMPLETED);
        }
    }

    /**
     * 온보딩 옵션 조회
     */
    public OnboardingLabelsResponse getOnboardingOptions(UserPrincipal userPrincipal) {
        if(userPrincipal == null) {
            return null;
        }
        return new OnboardingLabelsResponse(
            createGenderOptions(),
            createUniversityOptions(),
            createPositionOptions(),
            createMbtiOptions(),
			createPreferenceOptions()
        );
    }

    private List<OnboardingLabelsResponse.GenderOption> createGenderOptions() {
        return Arrays.stream(Gender.values())
            .map(gender -> new OnboardingLabelsResponse.GenderOption(gender.name(), gender.getKoreanName()))
            .toList();
    }

    private List<OnboardingLabelsResponse.UniversityOption> createUniversityOptions() {
        return Arrays.stream(University.values())
            .map(univ -> new OnboardingLabelsResponse.UniversityOption(univ.name(), univ.getKoreanName()))
            .toList();
    }

    private List<OnboardingLabelsResponse.PositionOption> createPositionOptions() {
        return Arrays.stream(Position.values())
            .map(position -> new OnboardingLabelsResponse.PositionOption(
                position.name(),
                position.getKoreanName()
            ))
            .toList();
    }

    private List<OnboardingLabelsResponse.MbtiOption> createMbtiOptions() {
        return Arrays.stream(Mbti.values())
            .map(mbti -> new OnboardingLabelsResponse.MbtiOption(mbti.name(), mbti.name()))
            .toList();
    }

	private List<OnboardingLabelsResponse.PreferenceTypeOption> createPreferenceOptions() {
		return Arrays.stream(PreferenceType.values())
			.map(preferenceType -> new OnboardingLabelsResponse.PreferenceTypeOption(
				preferenceType.name(),
				preferenceType.getKoreanName()))
			.toList();
	}
}
