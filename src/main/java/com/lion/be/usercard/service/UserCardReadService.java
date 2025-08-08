package com.lion.be.usercard.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepositoryImpl;
import com.lion.be.usercard.controller.dto.UserCardResponse;
import com.lion.be.usercard.util.UserCardFilterUtil;

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

	public List<UserCardResponse> getCards(Long userId, int size, List<Long> excludeUserIds) {
		List<Long> allExcludeUserIds = userViewHistoryService.getExcludeUserIds(userId, excludeUserIds);

		List<User> recommendedUsers = userCardFilterUtil.getRecommendedUsers(userId, size, allExcludeUserIds);

		List<Long> viewedUserIds = recommendedUsers.stream().map(User::getId).toList();
		userViewHistoryService.recordViewedUsers(userId, viewedUserIds);

		return recommendedUsers.stream()
			.map(UserCardResponse::from)
			.toList();
	}

	public UserCardResponse getMyCards(Long id) {
		return userRepositoryImpl.fetchById(id)
			.map(UserCardResponse::from)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
