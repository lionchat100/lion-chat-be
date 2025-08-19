package com.lion.be.userlike.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.lion.be.user.domain.entity.User;
import com.lion.be.userlike.domain.entity.UserLikes;
import com.lion.be.userlike.repository.persistence.jpa.UserLikesJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserLikesRepositoryImpl implements UserLikesRepository {

	private final UserLikesJpaRepository userLikesJpaRepository;

	// 카드에서 가져온 유저들중 내가 좋아요 한유저를 찾는 메서드
	@Override
	public List<Long> fetchLikedUserIdsAmon(Long currentUserId, List<Long> targetUserIds) {
		return userLikesJpaRepository.findLikedUserIdsAmon(currentUserId, targetUserIds);
	}

	@Override
	public boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId) {
		return userLikesJpaRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId);
	}

	@Override
	public void deleteByFromUserIdAndToUserId(Long fromUserId, Long toUserId) {
		userLikesJpaRepository.deleteByFromUserIdAndToUserId(fromUserId, toUserId);
	}

	@Override
	public UserLikes save(UserLikes userLikes) {
		return userLikesJpaRepository.save(userLikes);
	}

	@Override
	public List<User> fetchLikedUsersByFromUserId(Long userId) {
		return userLikesJpaRepository.findLikedUsersByFromUserId(userId);
	}
}
