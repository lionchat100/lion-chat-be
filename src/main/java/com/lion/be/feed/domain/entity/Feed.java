package com.lion.be.feed.domain.entity;

import com.lion.be.global.entity.BaseEntity;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Feed extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Boolean isDeleted;

    //List<FeedComment> feedComments;

    //List<FeedLike> feedLikes;

    public Feed(String content, String title, User user) {
        this.isDeleted = false; // 기본값 설정
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public void delete() {
        this.isDeleted = true; // 삭제 상태로 변경
    }


}
