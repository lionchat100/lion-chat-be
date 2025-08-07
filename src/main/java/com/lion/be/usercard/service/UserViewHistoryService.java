package com.lion.be.usercard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserViewHistoryService {

	private final RedisTemplate<String, Object> redisTemplate;

	// Redis key prefix
	private static final String VIEW_HISTORY_KEY_PREFIX = "user:view_history:";

	// TTL: 10분
	private static final Duration TTL = Duration.ofMinutes(10);

	/**
	 * 사용자가 본 카드들을 기록합니다. (10분간 유지)
	 *
	 * @param userId 현재 사용자 ID
	 * @param viewedUserIds 본 사용자들의 ID 목록
	 */
	public void recordViewedUsers(Long userId, List<Long> viewedUserIds) {
		if (viewedUserIds == null || viewedUserIds.isEmpty()) {
			return;
		}

		log.debug("사용자 {}가 본 카드들 기록: {}", userId, viewedUserIds);

		String key = getViewHistoryKey(userId);

		// Redis Set에 추가 (중복 자동 제거)
		String[] userIdStrings = viewedUserIds.stream()
			.map(String::valueOf)
			.toArray(String[]::new);

		redisTemplate.opsForSet().add(key, (Object[]) userIdStrings);

		// TTL 설정 (10분)
		redisTemplate.expire(key, TTL);

		// 현재 총 조회 이력 수 로깅
		Long totalViewCount = redisTemplate.opsForSet().size(key);
		log.debug("사용자 {}의 총 조회 이력: {}개 (TTL: {}분)", userId, totalViewCount, TTL.toMinutes());
	}

	/**
	 * 사용자가 이미 본 사용자 ID들을 조회합니다.
	 *
	 * @param userId 현재 사용자 ID
	 * @return 이미 본 사용자 ID 목록
	 */
	public List<Long> getViewedUserIds(Long userId) {
		String key = getViewHistoryKey(userId);

		Set<Object> viewedIds = redisTemplate.opsForSet().members(key);

		if (viewedIds == null || viewedIds.isEmpty()) {
			return Collections.emptyList();
		}

		return viewedIds.stream()
			.map(id -> Long.valueOf(id.toString()))
			.toList();
	}

	/**
	 * 카드 조회할 때 제외해야 할 모든 사용자 ID들을 반환합니다.
	 * (이미 본 사용자들 + 추가로 제외할 사용자들)
	 *
	 * @param userId 현재 사용자 ID
	 * @param additionalExcludes 추가로 제외할 사용자 ID들
	 * @return 제외해야 할 모든 사용자 ID 목록
	 */
	public List<Long> getExcludeUserIds(Long userId, List<Long> additionalExcludes) {

		// 이미 본 사용자들 추가
		Set<Long> excludeSet = new HashSet<>(getViewedUserIds(userId));

		// 추가로 제외할 사용자들 추가
		if (additionalExcludes != null) {
			excludeSet.addAll(additionalExcludes);
		}

		// 자기 자신도 제외
		excludeSet.add(userId);

		log.debug("사용자 {}의 제외 대상: {}명", userId, excludeSet.size());

		return new ArrayList<>(excludeSet);
	}

	private String getViewHistoryKey(Long userId) {
		return VIEW_HISTORY_KEY_PREFIX + userId;
	}
}
