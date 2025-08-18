package com.lion.be.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.image.domain.entity.Image;
import com.lion.be.image.repository.ImageRepository;
import com.lion.be.image.service.ImageUploadService;
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
	private final ImageRepository imageRepository;
	private final ImageUploadService imageUploadService;

	public void save(User user) {
        userRepository.save(user);
    }

	public OnboardingResponse completeUserOnboarding(Long userId, OnboardingRequest request) {
		User user = userRepository.fetchById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (userRepository.existsByNickname(request.nickname())) {
			throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}

		// 1. 이미지 ID 검증 및 조회
		List<Image> images = validateAndGetImages(request.imageIds());

		// 2. UserPhoto 생성 (중간 테이블을 통한 연결)
		for (int i = 0; i < images.size(); i++) {
			user.addProfileImage(images.get(i), i + 1);
		}

		// 3. 온보딩 데이터 설정 (이미지 관련 로직 제외)
		OnboardingData data = OnboardingData.from(request);
		user.completeOnboarding(data);

		// 4. 클러스터 배정
		Integer clusterId = assignClusterToNewUser(user);
		user.assignToCluster(clusterId);

		userRepository.save(user);

		return OnboardingResponse.success(userId);
	}

	private List<Image> validateAndGetImages(List<Long> imageIds) {
		if (imageIds == null || imageIds.isEmpty()) {
			throw new CustomException(ErrorCode.MINIMUM_PHOTOS_REQUIRED);
		}
		if (imageIds.size() > 3) {
			throw new CustomException(ErrorCode.MAXIMUM_PHOTOS_REQUIRED);
		}

		return imageIds.stream()
			.map(imageId -> {
				try {
					return imageRepository.fetchById(imageId);
				} catch (CustomException e) {
					throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
				}
			})
			.toList();
	}

	private Integer assignClusterToNewUser(User newUser) {
		return userCardFilterUtil.assignNewUserToCluster(newUser);
	}

	public UserProfileUpdateResponse updateUserProfile(UserPrincipal userPrincipal,
		UserProfileUpdateRequest request) {
		User user = userRepository.fetchById(userPrincipal.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (request.bio() != null) {
			user.updateBio(request.bio());
		}

		if (request.preferenceType() != null) {
			user.updatePreferenceType(request.preferenceType());
		}

		if (request.imageIds() != null) {
			imageUploadService.deleteUserPhotos(new ArrayList<>(user.getUserPhotos()), user.getId());

			user.getUserPhotos().clear();

			List<Image> images = validateAndGetImages(request.imageIds());
			for (int i = 0; i < images.size(); i++) {
				user.addProfileImage(images.get(i), i + 1);
			}
		}

		userRepository.save(user);
		return UserProfileUpdateResponse.success(user.getId());
	}
}
