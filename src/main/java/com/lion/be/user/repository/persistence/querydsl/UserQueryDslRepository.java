package com.lion.be.user.repository.persistence.querydsl;

import static com.lion.be.user.domain.entity.QUser.*;
import static com.lion.be.user.domain.entity.QUserPhoto.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.lion.be.user.domain.OnboardingStatus;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

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
			return List.of(); // 빈 리스트 반환
		}

		BooleanBuilder whereClause = new BooleanBuilder();

		whereClause.and(user.clusterId.eq(clusterId))
			.and(user.id.ne(currentUserId))
			.and(user.onboardingStatus.eq(OnboardingStatus.COMPLETED))
			.and(user.role.ne(Role.ADMIN));

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
	 * 기본 조건 생성 (온보딩 완료 + 제외 목록)
	 * 어드민 유저는제외
	 */
	private BooleanBuilder buildBaseConditions(Long currentUserId, List<Long> excludeUserIds) {
		BooleanBuilder whereClause = new BooleanBuilder();

		if (currentUserId != null) {
			whereClause.and(user.id.ne(currentUserId));
		}

		whereClause.and(user.onboardingStatus.eq(OnboardingStatus.COMPLETED));
		whereClause.and(user.role.ne(Role.ADMIN));

		if (excludeUserIds != null && !excludeUserIds.isEmpty()) {
			whereClause.and(user.id.notIn(excludeUserIds));
		}

		return whereClause;
	}

	/**
	 * 포지션별 랜덤 사용자 조회 (제외 목록 적용)
	 *
	 * 특정 포지션의 사용자들 중에서 랜덤하게 조회
	 * 클러스터 기반 추천에서 부족한 사용자를 보완할 때 사용
	 *
	 * @param userId 현재 사용자 ID (제외 대상)
	 * @param filterPosition 필터링할 포지션 (BACKEND, FRONTEND 등)
	 * @param excludeUserIds 제외할 사용자 ID 목록
	 * @param pageable 페이징 정보
	 * @return 조건에 맞는 사용자 목록
	 */
	public List<User> findRandomUsersByPositionExcluding(Long userId, Position filterPosition, List<Long> excludeUserIds, Pageable pageable) {
		BooleanBuilder whereClause = buildBaseConditions(userId, excludeUserIds);

		// 포지션 조건 추가
		whereClause.and(user.position.eq(filterPosition));

		if (pageable.getPageSize() <= 0) {
			return List.of();
		}

		return jpaQueryFactory
			.selectFrom(user)
			.leftJoin(user.userPhotos, userPhoto).fetchJoin()
			.where(whereClause)
			.orderBy(user.id.desc()) // 랜덤 효과
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}
