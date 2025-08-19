package com.lion.be.feed_comment.domain.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public record FeedCommentResponse(Long id,
                                  Long feedId,
                                  WriterResponse writer,
                                  String content,
                                  long likeCount,
                                  boolean isLiked,
                                  ZonedDateTime createdAt){

    public FeedCommentResponse(Long id, Long feedId, String content, LocalDateTime createdAt,
                               Long userId, String userName, String userImageUrl, long likeCount) {
        this(id, feedId, new WriterResponse(userId, userName, userImageUrl), content, likeCount, false,
                createdAt.atZone(ZoneId.of("Asia/Seoul")));
    }

    public FeedCommentResponse(Long id, Long feedId, String content, LocalDateTime createdAt,
                               Long userId, String userName, String userImageUrl){
        this(id, feedId, new WriterResponse(userId, userName, userImageUrl), content, 0, false, createdAt.atZone(ZoneId.of("Asia/Seoul")));
    }

}