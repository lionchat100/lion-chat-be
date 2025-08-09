package com.lion.be.feed_comment.domain.dto;

import java.time.LocalDateTime;

public record FeedCommentResponse(Long id,
                                  Long feedId,
                                  FeedCommentUserResponse feedCommentUserResponse,
                                  String content,
                                  long likeCount,
                                  boolean isLiked,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt) {
    // JPQL에서 사용할 새로운 생성자 추가
    public FeedCommentResponse(Long id, Long feedId, String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                               Long userId, String userName, String userImageUrl, long likeCount) {
        this(id, feedId, new FeedCommentUserResponse(userId, userName, userImageUrl), content, likeCount, false, createdAt,
                updatedAt);
    }

}