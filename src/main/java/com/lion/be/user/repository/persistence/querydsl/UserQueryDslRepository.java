package com.lion.be.user.repository.persistence.querydsl;

import static com.lion.be.user.domain.entity.QUser.*;
import static com.lion.be.user.domain.entity.QUserPhoto.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.lion.be.user.domain.OnboardingStatus;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private static final BooleanExpression COMPLETED_NON_ADMIN_USER =
		user.onboardingStatus.eq(OnboardingStatus.COMPLETED)
			.and(user.role.ne(Role.ADMIN));

	/**
	 * 동일 클러스터 내 사용자 조회
	 * 어드민 유저는 제외
	 */
	public List<User> findUsersByClusterExcluding(
		Integer clusterId,
		Long currentUserId,
		List<Long> excludeUserIds,
		int size
	) {
		if (size <= 0) {
			return List.of();
		}

		BooleanBuilder conditions = buildBaseConditions(currentUserId, excludeUserIds);
		conditions.and(user.clusterId.eq(clusterId));

		return jpaQueryFactory
			.selectFrom(user)
//			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(conditions)
			.orderBy(user.createdAt.desc())
			.limit(size)
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
		if (pageable.getPageSize() <= 0) {
			return List.of();
		}

		BooleanBuilder conditions = buildBaseConditions(currentUserId, excludeUserIds);

		return jpaQueryFactory
			.selectFrom(user)
//			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(conditions)
			.orderBy(user.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	public List<User> findRandomUsersByPositionExcluding(
		Long userId,
		Position filterPosition,
		List<Long> excludeUserIds,
		Pageable pageable
	) {
		if (pageable.getPageSize() <= 0) {
			return List.of();
		}

		BooleanBuilder conditions = buildBaseConditions(userId, excludeUserIds);
		conditions.and(user.position.eq(filterPosition));

		return jpaQueryFactory
			.selectFrom(user)
//			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(conditions)
			.orderBy(user.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	public Optional<User> findByIdWithPhotos(Long userId) {
		User result = jpaQueryFactory
			.selectFrom(user)
			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(user.id.eq(userId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	/**
	 * 공통 조건을 생성합니다.
	 * - 온보딩 완료된 사용자
	 * - 관리자가 아닌 사용자
	 * - 현재 사용자 제외
	 * - 제외 목록에 있는 사용자들 제외
	 */
	private BooleanBuilder buildBaseConditions(Long currentUserId, List<Long> excludeUserIds) {
		BooleanBuilder whereClause = new BooleanBuilder();

		// 기본 조건: 온보딩 완료 + 관리자 아님
		whereClause.and(COMPLETED_NON_ADMIN_USER);

		// 현재 사용자 제외
		if (currentUserId != null) {
			whereClause.and(user.id.ne(currentUserId));
		}

		// 제외 목록 적용
		if (excludeUserIds != null && !excludeUserIds.isEmpty()) {
			whereClause.and(user.id.notIn(excludeUserIds));
		}

		return whereClause;
	}
}
