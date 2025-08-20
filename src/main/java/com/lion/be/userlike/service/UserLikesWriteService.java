package com.lion.be.userlike.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.user.repository.UserRepository;
import com.lion.be.userlike.domain.entity.LikeCreatedEvent;
import com.lion.be.notification.domain.entity.UserLikes;
import com.lion.be.userlike.repository.UserLikesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLikesWriteService {

	private final UserLikesRepository userLikesRepository;
	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;

	/**
	 * 좋아요 토글 (생성/삭제)
	 */
	@Transactional
	public boolean toggleLike(Long currentUserId, Long targetUserId) {
		if (currentUserId.equals(targetUserId)) {
			throw new CustomException(ErrorCode.USER_CAN_NOT_LIKE_HIMSELF);
		}

		if (userLikesRepository.existsByFromUserIdAndToUserId(currentUserId, targetUserId)) {
			// 좋아요 취소
			userLikesRepository.deleteByFromUserIdAndToUserId(currentUserId, targetUserId);
			return false;
		} else {
			String fromUserNickname = userRepository.fetchNicknameById(currentUserId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

			if (!userRepository.existsById(targetUserId)) {
				throw new CustomException(ErrorCode.USER_NOT_FOUND);
			}



			userLikesRepository.save();

			// 서비스 계층에서 이벤트 발행
			LikeCreatedEvent event = new LikeCreatedEvent(
				currentUserId,
				targetUserId,
				fromUserNickname
			);
			eventPublisher.publishEvent(event);

			return true;
		}
	}
}
