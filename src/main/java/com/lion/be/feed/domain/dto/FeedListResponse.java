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
public class FeedListResponse {
    private Long feedId;
    private String title;
    private String content;
    private String username;
    private Long userId;
    private String profileImageUrl;
    private Long likeCount;
    private Boolean isLiked;
    private Long commentCount;
    private LocalDateTime createdAt;
}
