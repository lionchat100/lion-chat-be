package com.lion.be.feed.repository;

import com.lion.be.feed.domain.entity.QFeed;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class FeedRepositoryImpl implements FeedRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    // 배치 업데이트를 위한 메서드 구현
    @Override
    public void batchUpdateFeedLikeCount(List<Long> feedIds, List<Long> likeCounts) {
        QFeed feed = QFeed.feed;

        // 배치 업데이트 로직 구현
        if(feedIds == null || likeCounts == null || feedIds.size() != likeCounts.size()) {
            log.debug("피드 좋아요 배치 업데이트 할 것이 없음");
            return;
        }

        CaseBuilder.Cases<Long, NumberExpression<Long>> likeCountCase =
                new CaseBuilder()
                        .when(feed.id.eq(feedIds.get(0)))
                        .then(likeCounts.get(0));

        for(int i = 1; i < feedIds.size(); i++) {
            Long feedId = feedIds.get(i);
            Long likeCount = likeCounts.get(i);
            likeCountCase.when(feed.id.eq(feedId)).then(likeCount);
        }


        queryFactory
                .update(feed)
                .set(feed.likeCount, likeCountCase.otherwise(feed.likeCount)) // 기존 좋아요 수를 유지
                .where(feed.id.in(feedIds))
                .execute();
    }

    @Override
    public void batchUpdateFeedCommentCount(List<Long> feedIds, List<Long> commentCounts) {
        // 배치 업데이트 로직 구현
        QFeed feed = QFeed.feed;

        // 배치 업데이트 로직 구현
        if(feedIds == null || commentCounts == null || feedIds.size() != commentCounts.size()) {
            log.debug("피드 좋아요 배치 업데이트 할 것이 없음");
            return;
        }

        CaseBuilder.Cases<Long, NumberExpression<Long>> commentCountCase =
                new CaseBuilder()
                        .when(feed.id.eq(feedIds.get(0)))
                        .then(commentCounts.get(0));

        for(int i = 1; i < feedIds.size(); i++) {
            Long feedId = feedIds.get(i);
            Long commentCount = commentCounts.get(i);
            commentCountCase.when(feed.id.eq(feedId)).then(commentCount);
        }


        queryFactory
                .update(feed)
                .set(feed.commentCount, commentCountCase.otherwise(feed.commentCount)) // 기존 좋아요 수를 유지
                .where(feed.id.in(feedIds))
                .execute();
    }
}
