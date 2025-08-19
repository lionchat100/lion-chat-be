package com.lion.be.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.image.domain.entity.Image;
import com.lion.be.image.repository.ImageRepository;
import com.lion.be.image.service.ImageUploadService;
import com.lion.be.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserImageService {

	private static final int MAX_IMAGES = 3;

	private final ImageRepository imageRepository;
	private final ImageUploadService imageUploadService;

	@Transactional
	public void updateUserImages(User user, List<Long> imageIds) {
		// 1. 기존 이미지 삭제
		deleteExistingImages(user);

		// 2. 새 이미지 추가 (null이거나 빈 리스트면 이미지 없이 저장)
		if (hasValidImageIds(imageIds)) {
			addNewImages(user, imageIds);
		}
	}

	@Transactional
	public void setInitialUserImages(User user, List<Long> imageIds) {
		validateImageIds(imageIds);
		List<Image> images = getValidatedImages(imageIds);
		addImagesToUser(user, images);
	}

	private void validateImageIds(List<Long> imageIds) {
		if (imageIds == null || imageIds.isEmpty()) {
			throw new CustomException(ErrorCode.MINIMUM_PHOTOS_REQUIRED);
		}
		if (imageIds.size() > MAX_IMAGES) {
			throw new CustomException(ErrorCode.MAXIMUM_PHOTOS_REQUIRED);
		}
	}

	private boolean hasValidImageIds(List<Long> imageIds) {
		return imageIds != null && !imageIds.isEmpty();
	}

	private List<Image> getValidatedImages(List<Long> imageIds) {
		return imageIds.stream()
			.map(this::getImageById)
			.toList();
	}

	private Image getImageById(Long imageId) {
		try {
			return imageRepository.fetchById(imageId);
		} catch (CustomException e) {
			throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
		}
	}

	private void deleteExistingImages(User user) {
		if (!user.getUserPhotos().isEmpty()) {
			imageUploadService.deleteUserPhotos(new ArrayList<>(user.getUserPhotos()), user.getId());
			user.getUserPhotos().clear();
		}
	}

	private void addNewImages(User user, List<Long> imageIds) {
		List<Image> images = getValidatedImages(imageIds);
		addImagesToUser(user, images);
	}

	private void addImagesToUser(User user, List<Image> images) {
		for (int i = 0; i < images.size(); i++) {
			user.addProfileImage(images.get(i), i + 1);
		}
	}
}
