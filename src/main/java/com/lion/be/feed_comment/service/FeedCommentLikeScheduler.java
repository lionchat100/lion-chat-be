package com.lion.be.feed_comment.service;

import com.lion.be.feed_comment.repository.FeedCommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        // 1. Redis Set에서 처리할 대상 ID를 안전하게 가져옵니다.
        List<Object> objCommentIds = redisTemplate.opsForSet().pop(RedisKey.DIRTY_COMMENT_LIKE_KEY, 100);

        // pop 결과가 null이거나 비어있으면 즉시 종료 (NPE 방지)
        if (objCommentIds == null || objCommentIds.isEmpty()) {
            log.debug("업데이트 할 피드 댓글 수가 존재하지 않음");
            return;
        }

        List<Long> commentIdList = objCommentIds.stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .toList();

        // 2. Redis 'MGET'을 사용하여 모든 피드의 좋아요 수를 한 번에 조회합니다.
        List<String> commentLikeKeys = commentIdList.stream()
                .map(feedId -> RedisKey.COMMENT_LIKE_COUNT_KEY_PREFIX + feedId)
                .toList();

        List<Object> commentLikeObjList = redisTemplate.opsForValue().multiGet(commentLikeKeys);

        // multiGet 결과 자체가 null인 예외 케이스 처리
        if (commentLikeObjList == null) {
            log.warn("Redis multiGet 결과가 null입니다. 작업을 중단합니다.");
            return;
        }

        // 3. 유효한 데이터(ID와 Count가 모두 존재하는)만 필터링하여 최종 리스트를 생성합니다.
        List<Long> validCommentIds = new ArrayList<>();
        List<Long> validCommentLikes = new ArrayList<>();

        for (int i = 0; i < commentIdList.size(); i++) {
            Object commentCountObj = commentLikeObjList.get(i);
            // likeCount가 Redis에 실제로 존재할 경우 (null이 아닐 경우)에만 리스트에 추가
            if (commentCountObj != null) {
                try {
                    validCommentIds.add(commentIdList.get(i));
                    if (commentCountObj instanceof Number) {
                        // increment/decrement로 저장된 경우 (Number 타입)
                        log.info("피드 댓글 좋아요 수 업데이트: feedId={}, likeCount={}",
                                commentIdList.get(i), commentCountObj);
                        validCommentLikes.add(((Number) commentCountObj).longValue());
                    } else {
                        // set으로 저장된 경우 (String 타입)
                        log.info("피드 댓글 좋아요 수 업데이트: feedId={}, likeCount={}",
                                commentIdList.get(i), commentCountObj);
                        validCommentLikes.add(Long.parseLong(commentCountObj.toString()));
                    }
                } catch (NumberFormatException e) {
                    log.debug("댓글 좋아요 수를 Long으로 파싱하는데 실패했습니다. feedId: {}, value: {}",
                            commentIdList.get(i), commentCountObj, e);
                }
            } else {
                // Redis에 해당 피드의 좋아요 카운트 키가 없는 경우. (데이터 불일치 가능성)
                log.warn("피드 ID {}에 해당하는 댓글 수 카운트가 Redis에 존재하지 않습니다.", commentIdList.get(i));
            }
        }

        // 4. 최종적으로 유효한 데이터가 있을 때만 배치 업데이트를 호출합니다.
        if (!commentIdList.isEmpty()) {
            log.debug("유효한 피드 댓글 수 업데이트 대상: {}개", commentIdList.size());
            feedCommentRepository.batchUpdateFeedCommentLikeCount(validCommentIds, validCommentLikes);
        } else {
            log.debug("유효한 업데이트 대상이 없습니다.");
        }

        log.debug("피드 좋아요 카운트 Batch update 끝");
    }

}
