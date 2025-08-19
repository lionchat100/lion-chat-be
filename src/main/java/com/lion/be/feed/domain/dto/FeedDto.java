package com.lion.be.feed.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@Setter
public class FeedDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long likeCount;
    private Boolean isLiked;
    private Long commentCount;

    public void like(){
        this.isLiked = true;
    }

    public void unlike(){
        this.isLiked = false;
    }

    public FeedDto(Long id, String title, String content, LocalDateTime createdAt, Long likeCount, Boolean isLiked, Long commentCount){
        this.isLiked =  isLiked;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.id = id;
        this.title = title;
        this.createdAt = createdAt.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();;
    }

}
