package com.lion.be.user.repository;

import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;
import com.lion.be.user.repository.persistence.querydsl.UserQueryDslRepository;

import java.util.List;
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

    // 사용자 조회 (순서 통일)
    @Override
    public List<User> fetchCompletedUsersExcluding(
        Long currentUserId,
        List<Long> excludeUserIds,
        int size
    ) {
        Pageable pageable = PageRequest.of(0, size);

        return userQueryDslRepository.findCompletedUsersExcluding(
            currentUserId,
            excludeUserIds != null ? excludeUserIds : List.of(),
            pageable
        );
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
    public List<User> fetchAllCompletedUsersExcluding(
        Long currentUserId,
        List<Long> excludeUserIds
    ) {
        return userQueryDslRepository.findAllCompletedUsersExcluding(
            currentUserId,
            excludeUserIds != null ? excludeUserIds : List.of()
        );
    }

	@Override
	public boolean existsByNickname(
		String nickname
	) {
		return userJpaRepository.existsByNickname(nickname);
	}
}
