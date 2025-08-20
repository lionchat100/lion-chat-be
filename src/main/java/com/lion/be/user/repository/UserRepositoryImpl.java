package com.lion.be.user.repository;

import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.UserPhoto;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;
import com.lion.be.user.repository.persistence.querydsl.UserQueryDslRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;
	private final UserQueryDslRepository userQueryDslRepository;

	@Override
	public User findById(Long userId) {
		return userJpaRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("findById"));
	}

	@Override
	public Optional<User> fetchByEmail(String email) {
		return userJpaRepository.findByEmail(email);
	}

	@Override
	public User save(User user) {
		return userJpaRepository.save(user);
	}

	@Override
	public Optional<User> fetchById(Long userId) {
		return userJpaRepository.findById(userId);
	}

	@Override
	public List<User> fetchUsersByClusterExcluding(
		Integer clusterId,
		Long currentUserId,
		List<Long> excludeUserIds,
		int size
	) {
		return userQueryDslRepository.findUsersByClusterExcluding(
			clusterId,
			currentUserId,
			excludeUserIds != null ? excludeUserIds : List.of(),
			size
		);
	}

	@Override
	public List<User> fetchRandomUsersExcluding(
		Long currentUserId,
		int size,
		List<Long> excludeUserIds
	) {
		Pageable pageable = PageRequest.of(0, size);

		return userQueryDslRepository.findRandomUsersWithExclusion(
			currentUserId,
			excludeUserIds != null ? excludeUserIds : List.of(),
			pageable
		);
	}

	@Override
	public boolean existsByNickname(
		String nickname
	) {
		return userJpaRepository.existsByNickname(nickname);
	}

	@Override
	public void deleteAll() {
		userJpaRepository.deleteAll();
	}

	@Override
	public List<User> fetchRandomUsersByPositionExcluding(Long userId, Position filterPosition, int remainingSize, List<Long> extendedExcludeIds) {
		Pageable pageable = PageRequest.of(0, remainingSize);

		return userQueryDslRepository.findRandomUsersByPositionExcluding(
			userId,
			filterPosition,
			extendedExcludeIds != null ? extendedExcludeIds : List.of(),
			pageable
		);
	}

	@Override
	public Optional<User> fetchByIdWithPhotos(Long userId) {
		return userQueryDslRepository.findByIdWithPhotos(userId);
	}


	@Override
	public List<User> fetchAllUser(List<Long> userIds) {
		return userJpaRepository.fetchAllUser(userIds);
	}

	/**
	 * N+1 문제 해결을 위한 UserPhoto 배치 조회
	 */
	@Override
	public Map<Long, List<UserPhoto>> findPhotosMapByUserIds(List<Long> userIds) {
		return userQueryDslRepository.findPhotosMapByUserIds(userIds);
	}
}
