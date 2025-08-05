// UserRepositoryImpl.java - 수정된 버전
package com.lion.be.user.repository;

import com.lion.be.user.controller.dto.UserCardFilterRequest;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;

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
    public List<User> findMatchingUsersExcluding(
        Long currentUserId,
        UserCardFilterRequest filterRequest,
        int size,
        List<Long> excludeUserIds
    ) {
        Pageable pageable = PageRequest.of(0, size);

        // 제외 목록이 있고 비어있지 않은 경우에만 제외 쿼리 사용
        if (excludeUserIds != null && !excludeUserIds.isEmpty()) {
            return userJpaRepository.findMatchingUsersWithExclusion(
                currentUserId,
                excludeUserIds,
                filterRequest.preferredGender(),
                filterRequest.preferredMbti(),
                filterRequest.preferredUniversityName(),
                filterRequest.preferredPosition(),
                pageable
            );
        } else {
            // 제외 목록이 없거나 비어있으면 기본 쿼리 사용
            return userJpaRepository.findMatchingUsers(
                currentUserId,
                filterRequest.preferredGender(),
                filterRequest.preferredMbti(),
                filterRequest.preferredUniversityName(),
                filterRequest.preferredPosition(),
                pageable
            );
        }
    }

}
