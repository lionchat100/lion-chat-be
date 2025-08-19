package com.lion.be.userlike.repository;

import java.util.List;

import com.lion.be.user.domain.entity.User;
import com.lion.be.userlike.domain.entity.UserLikes;

public interface UserLikesRepository {

	UserLikes save(UserLikes userLikes);

	List<Long> fetchLikedUserIdsAmon(Long currentUserId, List<Long> targetUserIds);

	boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

	void deleteByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

	List<User> fetchLikedUsersByFromUserId(Long userId);
}
