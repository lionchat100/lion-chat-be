package com.lion.be.feed_comment.service;

import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.repository.FeedCommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lion.be.global.aop.ElapsedTime;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.global.util.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedCommentReadService {

    private final FeedCommentRepository feedCommentRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LIKE_COUNT_KEY_PREFIX = "comment:like_count:";
    private static final String LIKED_USERS_KEY_PREFIX = "comment:liked_users:";

    @ElapsedTime
    public Slice<FeedCommentResponse> fetchAll(Long feedId, Pageable pageable, Long userId) {

        Slice<FeedCommentResponse> slice = feedCommentRepository.fetchAllByFeedId(feedId, pageable);

        List<FeedCommentResponse> enrichedContent = slice.getContent().stream()
                .map(comment -> {
                    String likeCountKey = LIKE_COUNT_KEY_PREFIX + comment.id();
                    String likedUsersKey = LIKED_USERS_KEY_PREFIX + comment.id();

                    Object likeCountObj = redisTemplate.opsForValue().get(likeCountKey);
                    long finalLikeCount;

                    if (likeCountObj != null) {
                        finalLikeCount = ((Number) likeCountObj).longValue();
                    } else {
                        finalLikeCount = comment.likeCount(); // DB에서 조회한 likeCount 사용
                        redisTemplate.opsForValue().set(likeCountKey, finalLikeCount);
                    }

                    boolean isLiked = Boolean.TRUE.equals(
                            redisTemplate.opsForSet().isMember(likedUsersKey, String.valueOf(userId))
                    );

                    return new FeedCommentResponse(
                            comment.id(),
                            comment.feedId(),
                            comment.feedCommentUserResponse(),
                            comment.content(),
                            finalLikeCount, // 최종 계산된 좋아요 수
                            isLiked,
                            comment.createdAt(),
                            comment.updatedAt()
                    );
                }).collect(Collectors.toList());

        return new SliceImpl<>(enrichedContent, slice.getPageable(), slice.hasNext());
    }


    @ElapsedTime
    public Slice<FeedCommentResponse> fetchAll2(Long feedId, Pageable pageable, Long userId) {
        Slice<FeedCommentResponse> slice = feedCommentRepository.fetchAllByFeedId(feedId, pageable);


        List<Long> commentIds = slice.getContent().stream()
                .map(FeedCommentResponse::id)
                .toList();

        List<Object> likeCounts = redisTemplate.opsForValue().multiGet(
                commentIds.stream().map(id -> RedisKey.COMMENT_LIKE_COUNT_KEY_PREFIX + id).collect(Collectors.toList())
        );


        List<FeedCommentResponse> enrichedContent = new ArrayList<>();
        for(int i=0; i< slice.getContent().size(); i++){
            FeedCommentResponse comment = slice.getContent().get(i);
            String likeCountKey = RedisKey.COMMENT_LIKE_COUNT_KEY_PREFIX + comment.id();
            String likedUsersKey = RedisKey.COMMENT_LIKED_USERS_KEY_PREFIX + comment.id();

            Object likeCountObj = likeCounts.get(i);

            long likeCount;
            // Redis에서 좋아요 수를 가져오거나, 없으면 DB에서 가져온 값을 사용
            if(likeCountObj != null){
                likeCount = ((Number) likeCountObj).longValue();
            }else{
                likeCount = comment.likeCount();
                redisTemplate.opsForValue().set(likeCountKey, likeCount);
            }

            // 현재 사용자가 좋아요를 눌렀는지 확인
            boolean isLiked = Boolean.TRUE.equals(
                    redisTemplate.opsForSet().isMember(likedUsersKey, String.valueOf(userId))
            );

            // 댓글 응답 객체 업데이트
            enrichedContent.add(
                    new FeedCommentResponse(
                            comment.id(),
                            comment.feedId(),
                            comment.feedCommentUserResponse(),
                            comment.content(),
                            likeCount, // 최종 계산된 좋아요 수
                            isLiked,
                            comment.createdAt(),
                            comment.updatedAt()
                    )
            );
        }

        return new SliceImpl<>(enrichedContent, slice.getPageable(), slice.hasNext());
    }


    public FeedCommentResponse fetchById(Long commentId, Long id) {
        FeedCommentResponse comment = feedCommentRepository.findCommentById(commentId);

        if (comment == null) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUNT);
        }

        String likeCountKey = LIKE_COUNT_KEY_PREFIX + comment.id();
        String likedUsersKey = LIKED_USERS_KEY_PREFIX + comment.id();

        Object likeCountObj = redisTemplate.opsForValue().get(likeCountKey);
        long finalLikeCount;

        if (likeCountObj != null) {
            finalLikeCount = ((Number) likeCountObj).longValue();
        } else {
            finalLikeCount = comment.likeCount(); // DB에서 조회한 likeCount 사용
            redisTemplate.opsForValue().set(likeCountKey, finalLikeCount);
        }

        boolean isLiked = Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(likedUsersKey, String.valueOf(id))
        );

        return new FeedCommentResponse(
                comment.id(),
                comment.feedId(),
                comment.feedCommentUserResponse(),
                comment.content(),
                finalLikeCount, // 최종 계산된 좋아요 수
                isLiked,
                comment.createdAt(),
                comment.updatedAt()
        );
    }
}
