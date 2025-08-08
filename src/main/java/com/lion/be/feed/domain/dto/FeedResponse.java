package com.lion.be.feed.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FeedResponse {
    private FeedDto feed;
    private FeedWriterDto writer;

    public FeedResponse(Long feedId, String title, String content, LocalDateTime createdAt,
                        long likeCount, boolean isLiked, long commentCount,
                        String writerName, Long writerId, String writerImageUrl) {
        this.feed = new FeedDto(feedId, title, content, createdAt, likeCount, isLiked, commentCount);
        this.writer = new FeedWriterDto(writerName, writerId, writerImageUrl);
    }
}
