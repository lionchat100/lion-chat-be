package com.lion.be.feed.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FeedDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long likeCount;
    private Boolean isLiked;
    private Long commentCount;

}
