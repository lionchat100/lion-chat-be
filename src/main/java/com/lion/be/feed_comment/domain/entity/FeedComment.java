package com.lion.be.feed_comment.domain.entity;

import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.global.entity.BaseEntity;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    private String content;

    @Getter
    private boolean isDeleted;

    private FeedComment(Feed feed, User user, String content) {
        // TODO: null값 체크
        this.feed = feed;
        this.user = user;
        this.content = content;
        this.isDeleted = false;
    }

    public static FeedComment of(Feed feed, User user, String content) {
        return new FeedComment(feed, user, content);
    }

}
