package com.lion.be.userlike.repository;

import java.util.List;

import com.lion.be.userlike.domain.entity.UserLikes;

public interface UserLikesRepository {

	UserLikes save(UserLikes userLikes);

	List<Long> findLikedUserIdsAmon(Long currentUserId, List<Long> targetUserIds);

	boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

	void deleteByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

	List<UserLikes> findByFromUserId(Long userId);
}
