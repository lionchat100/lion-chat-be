package com.lion.be.feed.service;

import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.feed_comment.repository.FeedCommentRepository;
import com.lion.be.global.util.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedCommentCountScheduler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedRepository feedRepository;

    // 10초마다 실행
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void syncLikesToDb() {
        log.debug("피드 댓글 카운트 Batch update 시작");

        List<Object> objFeedIds = redisTemplate.opsForSet().pop(RedisKey.DIRTY_COMMENT_COUNT_KEY, 100);

        if (objFeedIds == null || objFeedIds.isEmpty()) {
            log.debug("업데이트 할 피드 댓글 수가 존재하지 않음");
            return;
        }

        for (Object feedIdObj : objFeedIds) {
            String feedIdStr = (String) feedIdObj;
            Long feedId = Long.parseLong(feedIdStr);

            String CommentCountKey = RedisKey.COMMENT_COUNT_KEY + feedId;
            Object CommentCountObj = redisTemplate.opsForValue().get(CommentCountKey);

            if (CommentCountObj != null) {
                long commentCount;
                if (CommentCountObj instanceof Number) {
                    // increment/decrement로 저장된 경우 (Number 타입)
                    commentCount = ((Number) CommentCountObj).longValue();
                } else {
                    // set으로 저장된 경우 (String 타입)
                    commentCount = Long.parseLong(CommentCountObj.toString());
                }

                feedRepository.updateCommentCount(feedId, commentCount);
                log.debug("댓글 수 업데이트 commentId: {}, likeCount: {}", feedId, commentCount);
            }
        }

        log.debug("피드 댓글 수 카운트 Batch update 끝: {}번", objFeedIds.size());
    }
}
