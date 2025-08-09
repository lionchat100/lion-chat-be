package com.lion.be.feed.domain.entity;

import com.lion.be.global.entity.BaseEntity;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    private long likeCount;

    public Feed(String title, String content, User user) {
        this.isDeleted = false; // 기본값 설정
        this.title = title;
        this.content = content;
        this.user = user;
        this.likeCount = 0L;
    }

    public void delete() {
        this.isDeleted = true; // 삭제 상태로 변경
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
