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

    public FeedCommentResponse(Long id, Long feedId, String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                               Long userId, String userName, String userImageUrl) {
        this(id, feedId, new FeedCommentUserResponse(userId, userName, userImageUrl), content, 0, false, createdAt,
                updatedAt);
    }

}