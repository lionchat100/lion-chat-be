package com.lion.be.user.repository.persistence.querydsl;

import static com.lion.be.user.domain.entity.QUser.*;
import static com.lion.be.user.domain.entity.QUserPhoto.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.lion.be.user.domain.OnboardingStatus;
import com.lion.be.user.domain.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	/**
	 * 완료된 사용자 조회 (페이징, 제외 목록 적용)
	 */
	public List<User> findCompletedUsersExcluding(
		Long currentUserId,
		List<Long> excludeUserIds,
		Pageable pageable
	) {
		BooleanBuilder whereClause = buildBaseConditions(currentUserId, excludeUserIds);

		return jpaQueryFactory
			.selectFrom(user)
			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(whereClause)
			.orderBy(user.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	/**
	 * 동일 클러스터 내 사용자 조회
	 */
	public List<User> findUsersByClusterExcluding(
		Integer clusterId,
		Long currentUserId,
		List<Long> excludeUserIds,
		int size
	) {
		if (size <= 0) {
			return List.of(); // 빈 리스트 반환
		}

		BooleanBuilder whereClause = new BooleanBuilder();

		whereClause.and(user.clusterId.eq(clusterId))
			.and(user.id.ne(currentUserId))
			.and(user.onboardingStatus.eq(OnboardingStatus.COMPLETED));

		if (excludeUserIds != null && !excludeUserIds.isEmpty()) {
			whereClause.and(user.id.notIn(excludeUserIds));
		}

		return jpaQueryFactory
			.selectFrom(user)
			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(whereClause)
			.orderBy(user.createdAt.desc())
			.limit(size) // 이제 안전함
			.fetch();
	}

	/**
	 * 랜덤 사용자 조회 (제외 목록 적용)
	 */
	public List<User> findRandomUsersWithExclusion(
		Long currentUserId,
		List<Long> excludeUserIds,
		Pageable pageable
	) {
		BooleanBuilder whereClause = buildBaseConditions(currentUserId, excludeUserIds);

		// 🔥 페이지 사이즈 검증 추가
		if (pageable.getPageSize() <= 0) {
			return List.of();
		}

		return jpaQueryFactory
			.selectFrom(user)
			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(whereClause)
			.orderBy(user.id.desc()) // ID 역순으로 랜덤 효과
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	/**
	 * 완료된 모든 사용자 조회 (페이징 없음)
	 */
	public List<User> findAllCompletedUsersExcluding(
		Long currentUserId,
		List<Long> excludeUserIds
	) {
		BooleanBuilder whereClause = buildBaseConditions(currentUserId, excludeUserIds);

		return jpaQueryFactory
			.selectFrom(user)
			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(whereClause)
			.orderBy(user.createdAt.desc())
			.fetch();
	}

	/**
	 * 기본 조건 생성 (온보딩 완료 + 제외 목록)
	 */
	private BooleanBuilder buildBaseConditions(Long currentUserId, List<Long> excludeUserIds) {
		BooleanBuilder whereClause = new BooleanBuilder();

		if (currentUserId != null) {
			whereClause.and(user.id.ne(currentUserId));
		}

		whereClause.and(user.onboardingStatus.eq(OnboardingStatus.COMPLETED));

		if (excludeUserIds != null && !excludeUserIds.isEmpty()) {
			whereClause.and(user.id.notIn(excludeUserIds));
		}

		return whereClause;
	}
}
