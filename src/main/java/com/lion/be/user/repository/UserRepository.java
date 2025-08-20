package com.lion.be.user.repository;

import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User findById(Long userId);

    Optional<User> fetchByEmail(String email);

    User save(User user);

    Optional<User> fetchById(Long userId);
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

  	boolean existsByNickname(String nickname);

    void deleteAll();

	/**
	 * 랜덤 유저 조회 (포지션 필터리)
	 * @param userId
	 * @param filterPosition
	 * @param remainingSize
	 * @param extendedExcludeIds
	 * @return
	 */
	List<User> fetchRandomUsersByPositionExcluding(
		Long userId,
		Position filterPosition,
		int remainingSize,
		List<Long> extendedExcludeIds
	);

	Optional<User> fetchByIdWithPhotos(Long userId);

	Optional<String> fetchNicknameById(Long userId);

	boolean existsById(Long userId);

	List<User> fetchAllUser(@Param("userIds") List<Long> userIds);
}
