package com.lion.be.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.controller.dto.OnboardingRequest;
import com.lion.be.user.controller.dto.OnboardingResponse;
import com.lion.be.user.controller.dto.UserProfileUpdateRequest;
import com.lion.be.user.controller.dto.UserProfileUpdateResponse;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.dto.OnboardingData;
import com.lion.be.user.repository.UserRepository;
import com.lion.be.usercard.util.UserCardFilterUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserWriteService {

	private final UserRepository userRepository;
	private final UserCardFilterUtil userCardFilterUtil;
	private final UserImageService userImageService;

	public void save(User user) {
		userRepository.save(user);
	}

	public OnboardingResponse completeUserOnboarding(Long userId, OnboardingRequest request) {
		User user = getUserById(userId);

		validateNicknameAvailability(request.nickname());

		userImageService.addInitialUserImages(user, request.imageIds());

		// 온보딩 데이터 설정
		completeUserOnboardingData(user, request);
		// 클러스터 할당
		assignClusterToUser(user);

		userRepository.save(user);
		return OnboardingResponse.success(userId);
	}

	public UserProfileUpdateResponse updateUserProfile(UserPrincipal userPrincipal,
		UserProfileUpdateRequest request) {
		User user = getUserById(userPrincipal.getId());

		updateUserBasicInfo(user, request);
		updateUserImages(user, request);

		userRepository.save(user);
		return UserProfileUpdateResponse.success(user.getId());
	}

	private User getUserById(Long userId) {
		return userRepository.fetchById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	private void validateNicknameAvailability(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}
	}

	private void completeUserOnboardingData(User user, OnboardingRequest request) {
		OnboardingData data = OnboardingData.from(request);
		user.completeOnboarding(data);
	}

	private void assignClusterToUser(User user) {
		Integer clusterId = userCardFilterUtil.assignNewUserToCluster(user);
		user.assignToCluster(clusterId);
	}

	private void updateUserBasicInfo(User user, UserProfileUpdateRequest request) {
		if (request.bio() != null) {
			user.updateBio(request.bio());
		}

		if (request.preferenceType() != null) {
			user.updatePreferenceType(request.preferenceType());
		}
	}

	private void updateUserImages(User user, UserProfileUpdateRequest request) {
		userImageService.updateUserImages(user, request.imageIds());
	}
}
