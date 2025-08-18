package com.lion.be.feed_comment.domain.dto;

import java.time.LocalDateTime;


public record FeedCommentResponse(Long id,
                                  Long feedId,
                                  WriterResponse writer,
                                  String content,
                                  long likeCount,
                                  boolean isLiked,
                                  LocalDateTime createdAt){

    public FeedCommentResponse(Long id, Long feedId, String content, LocalDateTime createdAt,
                               Long userId, String userName, String userImageUrl, long likeCount) {
        this(id, feedId, new WriterResponse(userId, userName, userImageUrl), content, likeCount, false,
                createdAt);
    }

}