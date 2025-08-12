package com.lion.be.userlike.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepositoryImpl;
import com.lion.be.userlike.domain.entity.LikeCreatedEvent;
import com.lion.be.userlike.domain.entity.UserLikes;
import com.lion.be.userlike.repository.UserLikesRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLikesWriteService {

	private final UserLikesRepositoryImpl userLikesRepositoryImpl;
	private final UserRepositoryImpl userRepositoryImpl;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * 좋아요 토글 (생성/삭제)
	 */
	@Transactional
	public boolean toggleLike(Long currentUserId, Long targetUserId) {
		if (currentUserId.equals(targetUserId)) {
		throw new CustomException(ErrorCode.USER_CAN_NOT_LIKE_HIMSELF);
		}

		if (userLikesRepositoryImpl.existsByFromUserIdAndToUserId(currentUserId, targetUserId)) {
			// 좋아요 취소
			userLikesRepositoryImpl.deleteByFromUserIdAndToUserId(currentUserId, targetUserId);
			return false;
		} else {
			// 좋아요 추가
			User fromUser = userRepositoryImpl.findById(currentUserId);
			User toUser = userRepositoryImpl.findById(targetUserId);

			UserLikes userLikes = new UserLikes(fromUser, toUser);
			userLikesRepositoryImpl.save(userLikes);

			// 서비스 계층에서 이벤트 발행
			LikeCreatedEvent event = new LikeCreatedEvent(
				currentUserId,
				targetUserId,
				fromUser.getName()
			);
			eventPublisher.publishEvent(event);

			return true;
		}
	}
}
