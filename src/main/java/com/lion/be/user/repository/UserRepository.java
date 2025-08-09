package com.lion.be.user.repository;

import com.lion.be.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User findById(Long userId);

    Optional<User> fetchByEmail(String email);

    User save(User user);

    Optional<User> fetchById(Long userId);
    /**
     * 완료된 사용자 조회 (페이징, 제외 목록 적용)
     */
    List<User> fetchCompletedUsersExcluding(
        Long currentUserId,
        List<Long> excludeUserIds,
        int size
    );

    /**
     * 동일 클러스터 내 사용자 조회
     */
    List<User> fetchUsersByClusterExcluding(
        Integer clusterId,
        Long currentUserId,
        List<Long> excludeUserIds,
        int size
    );

    /**
     * 랜덤 사용자 조회
     */
    List<User> fetchRandomUsersExcluding(
        Long currentUserId,
        int size,
        List<Long> excludeUserIds
    );

    /**
     * 완료된 모든 사용자 조회 (페이징 없음)
     */
    List<User> fetchAllCompletedUsersExcluding(
        Long currentUserId,
        List<Long> excludeUserIds
    );

	  boolean existsByNickname(String nickname);
  
    List<User> findMatchingUsersExcluding(
            Long currentUserId,
            UserCardFilterRequest filterRequest,
            int size,
            List<Long> excludeUserIds
    );

    void deleteAll();
}
