package com.lion.be.usercard.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepositoryImpl;
import com.lion.be.usercard.controller.dto.UserCardResponse;
import com.lion.be.usercard.util.UserCardFilterUtil;
import com.lion.be.userlike.service.UserLikesReadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCardReadService {

	private final UserViewHistoryService userViewHistoryService;
	private final UserCardFilterUtil userCardFilterUtil;
	private final UserRepositoryImpl userRepositoryImpl;
	private final UserLikesReadService userLikesReadService;

	public List<UserCardResponse> getCards(Long userId, int size, List<Long> excludeUserIds) {
		List<Long> allExcludeUserIds = userViewHistoryService.getExcludeUserIds(userId, excludeUserIds);
		List<User> recommendedUsers = userCardFilterUtil.getRecommendedUsers(userId, size, allExcludeUserIds);

		return convertToUserCardResponses(userId, recommendedUsers);
	}

	public UserCardResponse getUserCard(Long id) {
		return userRepositoryImpl.fetchById(id)
			.map(user -> UserCardResponse.from(user, false))
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	public List<UserCardResponse> getCardsByPosition(Long userId, int size, List<Long> excludeUserIds, Position position) {
		List<Long> allExcludeUserIds = userViewHistoryService.getExcludeUserIds(userId, excludeUserIds);
		List<User> users = userCardFilterUtil.getRecommendedUsersByPosition(userId, size, allExcludeUserIds, position);

		return convertToUserCardResponses(userId, users);
	}

	private List<UserCardResponse> convertToUserCardResponses(Long currentUserId, List<User> users) {
		if (users.isEmpty()) {
			return List.of();
		}

		// 1. 조회한 사용자 ID 목록 추출
		List<Long> viewedUserIds = users.stream()
			.map(User::getId)
			.toList();

		// 2. 조회 이력 기록
		userViewHistoryService.recordViewedUsers(currentUserId, viewedUserIds);

		// 3. 좋아요 상태 확인
		Set<Long> likedUserIds = userLikesReadService.getLikedUserIds(currentUserId, viewedUserIds);

		// 4. Response DTO 변환
		return users.stream()
			.map(user -> UserCardResponse.from(user, likedUserIds.contains(user.getId())))
			.toList();
	}
}
