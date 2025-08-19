package com.lion.be.userlike.repository.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.lion.be.userlike.domain.entity.UserLikes;

import io.lettuce.core.dynamic.annotation.Param;

public interface UserLikesJpaRepository extends JpaRepository<UserLikes, Long> {

	@Query("""
        SELECT ul.toUser.id 
        FROM UserLikes ul 
        WHERE ul.fromUser.id = :currentUserId 
        AND ul.toUser.id IN :targetUserIds
    """) //카드 내에서 좋아요 한 유무체크
	List<Long> findLikedUserIdsAmon(
		@Param("currentUserId") Long currentUserId,
		@Param("targetUserIds") List<Long> targetUserIds
	);

	boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

	@Modifying
	@Query("DELETE FROM UserLikes ul WHERE ul.fromUser.id = :fromUserId AND ul.toUser.id = :toUserId")
	void deleteByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

	@Query("SELECT ul FROM UserLikes ul " +
		"JOIN FETCH ul.toUser u " +
		"LEFT JOIN FETCH u.userPhotos up " +
		"WHERE ul.fromUser.id = :userId " +
		"ORDER BY u.id, up.orderIndex")
	List<UserLikes> findByFromUserIdWithToUser(@Param("userId") Long userId);
}
