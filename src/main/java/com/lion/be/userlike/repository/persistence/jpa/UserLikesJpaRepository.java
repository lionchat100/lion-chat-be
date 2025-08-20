package com.lion.be.userlike.repository.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.lion.be.user.domain.entity.User;
import com.lion.be.notification.domain.entity.UserLikes;

import io.lettuce.core.dynamic.annotation.Param;

public interface UserLikesJpaRepository extends JpaRepository<UserLikes, Long> {

	@Query("""
        SELECT ul.toUserId 
        FROM UserLikes ul 
        WHERE ul.fromUserId = :currentUserId 
        AND ul.toUserId IN :targetUserIds
    """) //카드 내에서 좋아요 한 유무체크
	List<Long> findLikedUserIdsAmon(
		@Param("currentUserId") Long currentUserId,
		@Param("targetUserIds") List<Long> targetUserIds
	);

	boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

	@Modifying
	@Query("DELETE FROM UserLikes ul WHERE ul.fromUserId = :fromUserId AND ul.toUserId = :toUserId")
	void deleteByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

	@Query("""
        SELECT u FROM User u 
        LEFT JOIN FETCH u.userPhotos up
        WHERE u.id IN (
            SELECT ul.toUserId FROM UserLikes ul 
            WHERE ul.fromUserId = :userId
        )
        ORDER BY u.id, up.orderIndex
    """)
	List<User> findLikedUsersByFromUserId(@Param("userId") Long userId);
}
