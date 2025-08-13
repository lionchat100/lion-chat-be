package com.lion.be.feed_comment.service;

import com.lion.be.feed_comment.repository.FeedCommentRepository;
import java.util.List;

import com.lion.be.global.util.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedCommentLikeScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedCommentRepository feedCommentRepository;

    // 10초마다 실행
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void syncLikesToDb() {
        log.debug("피드 댓글 좋아요 카운트 Batch update 시작");

        List<Object> objCommentIds = redisTemplate.opsForSet().pop(RedisKey.DIRTY_COMMENT_LIKE_KEY, 100);

        if (objCommentIds == null || objCommentIds.isEmpty()) {
            log.debug("업데이트 할 피드 댓글 좋아요가 존재하지 않음");
            return;
        }

        for (Object commentIdObj : objCommentIds) {
            String commentIdStr = (String) commentIdObj;
            Long commentId = Long.parseLong(commentIdStr);

            String likeCountKey = RedisKey.COMMENT_LIKE_COUNT_KEY_PREFIX + commentId;
            Object likeCountObj = redisTemplate.opsForValue().get(likeCountKey);

            if (likeCountObj != null) {
                long likeCount;
                if (likeCountObj instanceof Number) {
                    // increment/decrement로 저장된 경우 (Number 타입)
                    likeCount = ((Number) likeCountObj).longValue();
                } else {
                    // set으로 저장된 경우 (String 타입)
                    likeCount = Long.parseLong(likeCountObj.toString());
                }

                feedCommentRepository.updateLikeCount(commentId, likeCount);
                log.debug("업데이트 commentId: {}, likeCount: {}", commentId, likeCount);
            }
        }

        log.debug("피드 댓글 좋아요 카운트 Batch update 끝: {}번", objCommentIds.size());
    }

}
